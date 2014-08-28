//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.LUTgenerator;

// Original code from:
//   http://metzmeanderings.blogspot.com/2010/01/colormodeljava-program-to-translate.html  <- no longer used
// Equations from Bruce Lindbloom and Wikipedia:
//   http://www.brucelindbloom.com/index.html?Eqn_XYZ_to_RGB.html
// Testing data from:
//   http://people.sc.fsu.edu/~jburkardt/f_src/colors/colors_prb_output.txt

public class ColorConverter
{
	// converted every possible RGB color into XYZ and reported the minimum and maximum values
	// used the sRGB transform to convert from RGB to XYZ
	public static final double MIN_X = 0.0;
	public static final double MAX_X = 0.95047;
	public static final double MIN_Y = 0.0;
	public static final double MAX_Y = 1.0000001;
	public static final double MIN_Z = 0.0;
	public static final double MAX_Z = 1.08883;

	// converted every possible RGB color into xyY and reported the minimum and maximum values
	// used the sRGB transform to convert from RGB to XYZ (then to xyY)
	public static final double MIN_x = 0.15000000831312826;
	public static final double MAX_x = 0.6399999255194091;
	public static final double MIN_y = 0.0600000033252513;
	public static final double MAX_y = 0.6000000167796455;

	// converted every possible RGB color into LUV and reported the minimum and maximum values
	// used the sRGB transform to convert from RGB to XYZ (then to LUV)
	public static final double MIN_LUM_LUV = 0.0;
	public static final double MAX_LUM_LUV = 100.00000386666655;
	public static final double MIN_U_LUV = -83.06282248772624;
	public static final double MAX_U_LUV = 175.0239745963978;
	public static final double MIN_V_LUV = -134.10498157887673;
	public static final double MAX_V_LUV = 107.39409378472106;
	public static final double MIN_CHROMA_LUV = 0.0;
	public static final double MAX_CHROMA_LUV = 179.04953454992582;

	// converted every possible RGB color into LAB and reported the minimum and maximum values
	// used the sRGB transform to convert from RGB to XYZ (then to LAB)
	public static final double MIN_LUM_LAB = 0.0;
	public static final double MAX_LUM_LAB = 100.00000386666655;
	public static final double MIN_A_LAB = -86.17385493791946;
	public static final double MAX_A_LAB = 98.2448002875424;
	public static final double MIN_B_LAB = -107.8619171648283;
	public static final double MAX_B_LAB = 94.47705120353054;
	public static final double MIN_CHROMA_LAB = 0.0;
	public static final double MAX_CHROMA_LAB = 133.81320505740877;

	// calculate some constants
	public static final double EPSILON = 216.0/24389.0;
	public static final double KAPPA = 24389.0/27.0;

	// these are the XYZ values for the D65 whitepoint
	public static final double X_SUB_W = 0.9504;	// 95.04;
	public static final double Y_SUB_W = 1.0000;	// 100.00;
	public static final double Z_SUB_W = 1.0888;	// 108.88;

	// these are the CIE 1964 UCS coordinates for the reference white (u' v')
	public static final double U_PRIME_W = (4.0*X_SUB_W) / (X_SUB_W + 15.0*Y_SUB_W + 3.0*Z_SUB_W);
	public static final double V_PRIME_W = (9.0*Y_SUB_W) / (X_SUB_W + 15.0*Y_SUB_W + 3.0*Z_SUB_W);

	/* Find the Euclidian distance between these two points. */
	public static double findDistance(double x1, double y1, double x2, double y2)
	{
		double d1 = x1 - x2;
		double d2 = y1 - y2;
		return Math.sqrt(d1*d1 + d2*d2);
	}

	/* Find the Euclidian distance between each color. */
	public static double findDistance(double[] c1, double[] c2)
	{
		// check input array length (must be equal)
		if (c1.length != c2.length) {
			throw new RuntimeException("findDistance: array parameters must be the same length.");
		}

		// determine the distance
		if (c1.length == 1) { // avoid square and square root for single dimension case
			return Math.abs(c1[0] - c2[0]);
		}
		else { // all other cases (dimension >= 2)
			double sumSquares = 0.0;
			for (int i=0; i<c1.length; i=i+1)
			{
				sumSquares = sumSquares + Math.pow(c1[i] - c2[i], 2.0);
			}
			return Math.sqrt(sumSquares);
		}
	}

	/* Convenience function for moving from int RGB to L*u*v* coordinates. */
	public static double[] RGBtoLUV(int rgb)
	{
		return XYZtoLUV(RGBtoXYZ(intRGBToDoubleRGB(rgb)));
	}

	/* Convenience function for moving from L*u*v* coordinates to int RGB. */
	public static int LUVtoRGB(double[] LUV)
	{
		return packageIntRGB(doubleRGBToIntRGB(XYZtoRGB(LUVtoXYZ(LUV))));
	}

	/* Convenience function for moving from int RGB to L*a*b* coordinates. */
	public static double[] RGBtoLAB(int rgb)
	{
		return XYZtoLAB(RGBtoXYZ(intRGBToDoubleRGB(rgb)));
	}

	/* Convenience function for moving from L*a*b* coordinates to int RGB. */
	public static int LABtoRGB(double[] LAB)
	{
		return packageIntRGB(doubleRGBToIntRGB(XYZtoRGB(LABtoXYZ(LAB))));
	}

	/* Shared method for converting both L*u*v* and L*a*b* to LCH (luminance,
	 * chroma, hue angle) representation. */
	public static double[] LXYtoLCH(double[] lxy)
	{
		// extract the L, x, and y components of the input (x=a or u, y=b or v)
		double L = lxy[0]; // doubles as the lightness for LCH
		double x = lxy[1];
		double y = lxy[2];

		// determine the chroma
		double C = Math.sqrt(x*x + y*y);

		// determine the hue (degrees)
		double H = Math.toDegrees(Math.atan2(y, x));
		while (H < 0.0)    { H = H + 360.0; }	// make sure hue is not < 0.0
		while (H >= 360.0) { H = H - 360.0; }	// make sure hue is not >= 360.0 

		// create the result array and return
		double[] LCH = { L, C, H };
		return LCH;
	}

	/* Shared method for converting the LCH representation of both L*u*v* and
	 * L*a*b* back to their CIE representation (out of LCH representation). */
	public static double[] LCHtoLXY(double[] lch)
	{
		// extract the L, C, and H components of the input
		double L = lch[0]; // doubles as the lightness for Luv and Lab
		double C = lch[1];
		double H = Math.toRadians(lch[2]); // convert hue from degrees to radians

		// determine 'u' or 'a' (x)
		double x = C * Math.cos(H);

		// determine 'v' or 'b' (y)
		double y = C * Math.sin(H);

		// create the result array and return
		double[] lxy = { L, x, y };
		return lxy;
	}

	/* Convert the supplied Luv color to LCH_uv space (which is a
	 * Lightness, Chroma, Hue angular representation of Luv space. */
	public static double[] LUVtoLCH_UV(double[] luv)
	{
		return LXYtoLCH(luv);
	}

	/* Convert the supplied LCH_uv color to Luv color. */
	public static double[] LCH_UVtoLUV(double[] lch)
	{
		return LCHtoLXY(lch);
	}

	/* Convert the supplied Luv color to XYZ color. */
	public static double[] LUVtoXYZ (double[] luv)
	{
		// extract the L, u, and v components of the input
		double L = luv[0];
		double u = luv[1];
		double v = luv[2];

		// determine the 'Y' component of the XYZ color
		double Y;
		if (L > (KAPPA*EPSILON)) {
			Y = Math.pow(((L + 16.0)/116.0), 3.0);
		}
		else {
			Y = L / KAPPA;
		}

		// temporary values
		double a = (1.0/3.0) * (((52.0 * L) / (u + (13.0 * L * U_PRIME_W))) - 1.0);		
		double b = (-5.0 * Y);
		double c = (-1.0 / 3.0);
		double d = Y * (((39.0 * L) / (v + (13 * L * V_PRIME_W))) - 5.0);

		// determine the 'X' and 'Z' components of the XYZ color
		double X = (d - b) / (a - c);
		if (Double.isNaN(X)) { X = 0.0; }
		double Z = (X * a) + b;
		if (Double.isNaN(Z)) { Z = 0.0; }
		double[] XYZ = { X, Y, Z };
		return XYZ;
	}

	/* Convert the supplied XYZ color to Luv color. */
	public static double[] XYZtoLUV (double[] xyz)
	{
		// retrieve the input values
		double X = xyz[0];
		double Y = xyz[1];
		double Z = xyz[2];

		// determine some constants
		double ySubR = (Y / Y_SUB_W);
		double denominator = X + (15.0 * Y) + (3.0 * Z);
		double uPrime;
		double vPrime;
		if (denominator == 0.0) {
			uPrime = U_PRIME_W;
			vPrime = V_PRIME_W;
		}
		else {
			uPrime = (4.0 * X) / denominator;
			vPrime = (9.0 * Y) / denominator;
		}

		// determine the 'L' component of the Luv color
		double L;
		if (ySubR > EPSILON) {
			L = (116.0 * Math.cbrt(ySubR)) - 16.0;
		}
		else {
			L = KAPPA * ySubR;
		}

		// determine the 'u' and 'v' components of the Luv color
		double u = 13.0 * L * (uPrime - U_PRIME_W);
		double v = 13.0 * L * (vPrime - V_PRIME_W);
		double[] LUV = { L, u, v };
		return LUV;
	}

	/* Convert the supplied Lab color to LCH_ab space (which is a
	 * Lightness, Chroma, Hue angular representation of Lab space). */
	public static double[] LABtoLCH_AB(double[] lab)
	{
		return LXYtoLCH(lab);
	}

	/* Convert the supplied LCH_ab color to Lab color. */
	public static double[] LCH_ABtoLAB(double[] lch)
	{
		return LCHtoLXY(lch);
	}

	/* Convert the supplied Lab color to XYZ color. */
	public static double[] LABtoXYZ(double[] lab)
	{
		// determine function values for X, Y, and Z
		double fSubY = (lab[0] + 16.0) / 116.0;
		double fSubZ = fSubY - (lab[2] / 200.0);
		double fSubX = (lab[1] / 500.0) + fSubY;

		// determine the xSubR value
		double xSubR;
		double fSubXCubed = Math.pow(fSubX, 3.0);
		if (fSubXCubed > EPSILON) {
			xSubR = fSubXCubed;
		}
		else {
			xSubR = (116.0 * fSubX - 16.0) / KAPPA;
		}

		// determine the ySubR value
		double ySubR;
		double fSubYCubed = Math.pow(fSubY, 3.0);
		if (fSubYCubed > EPSILON) {
			ySubR = fSubYCubed;
		}
		else {
			ySubR = (116.0 * fSubY - 16.0) / KAPPA;
		}

		// determine the zSubR value
		double zSubR;
		double fSubZCubed = Math.pow(fSubZ, 3.0);
		if (fSubZCubed > EPSILON) {
			zSubR = fSubZCubed;
		}
		else {
			zSubR = (116.0 * fSubZ - 16.0) / KAPPA;
		}

		// de-normalize the 'X', 'Y', and 'Z' components using the reference white values
		double X = xSubR * X_SUB_W;
		double Y = ySubR * Y_SUB_W;
		double Z = zSubR * Z_SUB_W;
		double[] XYZ = { X, Y, Z };
		return XYZ;
	}

	/* Convert the supplied XYZ color to Lab color. */
	public static double[] XYZtoLAB(double[] xyz)
	{
		// retrieve and normalize the input values
		double xSubR = xyz[0] / X_SUB_W;
		double ySubR = xyz[1] / Y_SUB_W;
		double zSubR = xyz[2] / Z_SUB_W;

		// determine the X-related function value
		double fSubX;
		if (xSubR > EPSILON) {
			fSubX = Math.cbrt(xSubR);
		}
		else {
			fSubX = (KAPPA * xSubR + 16.0) / 116.0;
		}

		// determine the Y-related function value
		double fSubY;
		if (ySubR > EPSILON) {
			fSubY = Math.cbrt(ySubR);
		}
		else {
			fSubY = (KAPPA * ySubR + 16.0) / 116.0;
		}

		// determine the Z-related function value
		double fSubZ;
		if (zSubR > EPSILON) {
			fSubZ = Math.cbrt(zSubR);
		}
		else {
			fSubZ = (KAPPA * zSubR + 16.0) / 116.0;
		}

		// determine the 'L', 'a', and 'b' components
		double L = (116.0 * fSubY) - 16.0;
		double a = 500.0 * (fSubX - fSubY);
		double b = 200.0 * (fSubY - fSubZ);
		double[] LAB = { L, a, b };
		return LAB;
	}

	/* Convert the supplied XYZ color into sRGB color. */
	public static double[] XYZtoRGB(double[] xyz)
	{
		// determine RGB values
		// formula from http://www.easyrgb.com/index.php?X=MATH&H=02#text1
		// values from Bruce Lindbloom
		double R = (xyz[0] *  3.2404542) + (xyz[1] * -1.5371385) + (xyz[2] * -0.4985314);
		double G = (xyz[0] * -0.9692660) + (xyz[1] *  1.8760108) + (xyz[2] *  0.0415560);
		double B = (xyz[0] *  0.0556434) + (xyz[1] * -0.2040259) + (xyz[2] *  1.0572252);

		// compand/linearlze the RGB values (correct sRGB gamma two-part function) 
		if (R > 0.0031308)	{ R = (1.055 * Math.pow(R, 1.0/2.4)) - 0.055; }
		else				{ R = 12.92 * R; }
		if (G > 0.0031308)	{ G = (1.055 * Math.pow(G, 1.0/2.4)) - 0.055; }
		else				{ G = 12.92 * G; }
		if (B > 0.0031308)	{ B = (1.055 * Math.pow(B, 1.0/2.4)) - 0.055; }
		else				{ B = 12.92 * B; }

		// create and return the resulting RGB colour
		double[] RGB = { R, G, B };
		return RGB;

/*		// create the appropriate matrices
		Matrix xyzMatrix = new Matrix(xyz, 3);

		// multiple the M^-1 matrix with the XYZ matrix
		Matrix result = M_Inverse.times(xyzMatrix);

		// collapse the rows from the multiplication to get the rgb raw values
		double[] rgb = result.getRowPackedCopy();

		// calculate the resulting RGB values
		double R = Math.pow(rgb[0], (1.0/GAMMA));
		double G = Math.pow(rgb[1], (1.0/GAMMA));
		double B = Math.pow(rgb[2], (1.0/GAMMA));

		// a hack to fix things with NaN issues
		// seem to only get NaN when a value is supposed to be 0
		if (Double.isNaN(R)) { R = 0.0; }
		if (Double.isNaN(G)) { G = 0.0; }
		if (Double.isNaN(B)) { B = 0.0; }

		// return the result
		double[] RGB = { R, G, B };
		return RGB;
*/	}

	/* Convert the supplied sRGB color into XYZ color. */
	public static double[] RGBtoXYZ(double[] RGB)
	{
		// compand/linearize the RGB values (sRGB gamma function)
		double R, G, B;
		if ( RGB[0] > 0.04045 )	{ R = Math.pow(((RGB[0] + 0.055) / 1.055), 2.4); }
		else					{ R = RGB[0] / 12.92; }
		if ( RGB[1] > 0.04045 )	{ G = Math.pow(((RGB[1] + 0.055) / 1.055), 2.4); }
		else					{ G = RGB[1] / 12.92; }
		if ( RGB[2] > 0.04045 )	{ B = Math.pow(((RGB[2] + 0.055) / 1.055), 2.4); }
		else					{ B = RGB[2] / 12.92; }

		// determine XYZ values
		// formula from http://www.easyrgb.com/index.php?X=MATH&H=02#text2
		// values from Bruce Lindbloom
		double X = (R * 0.4124564) + (G * 0.3575761) + (B * 0.1804375);
		double Y = (R * 0.2126729) + (G * 0.7151522) + (B * 0.0721750);
		double Z = (R * 0.0193339) + (G * 0.1191920) + (B * 0.9503041);

		// create and return the resulting XYZ colour
		double[] XYZ = { X, Y, Z };
		return XYZ;

/*		// calculate the linearized RGB values (simplified sRGB)
		double R = Math.pow(RGB[0], GAMMA);
		double G = Math.pow(RGB[1], GAMMA);
		double B = Math.pow(RGB[2], GAMMA);
		double[] rgb = { R, G, B };

		// create the appropriate matrices
		Matrix rgbMatrix = new Matrix(rgb, 3);

		// multiply the M matrix with the RGB matrix
		Matrix result = M.times(rgbMatrix);

		// collapse the rows from the multiplication to get the xyz raw values
		double[] XYZ = result.getRowPackedCopy();

		return XYZ;
*/
	}

	/* Convert the supplied CIE 1931 chromaticity coordinates (xy) into 1964 CIE UCS coordinates (u'v').
	 * Note that u'v' are NOT the u*v* coordinates of the L*u*v* colour space. */
	public static double[] xy_to_UCSuv(double[] xy)
	{
		// extract the chromaticity coordinates
		double x = xy[0];
		double y = xy[1];

		// calculate the UCS coordinates (formula from Wikipedia entry for CIE L*u*v*)
		double uPrime = (4.0*x) / (-2.0*x + 12.0*y + 3.0);
		double vPrime = (9.0*y) / (-2.0*x + 12.0*y + 3.0);
		double[] UCSuv = { uPrime, vPrime };
		return UCSuv;
	}

	/* Convert the supplied 1964 CIE UCS coordinates (u'v') into CIE 1931 chromaticity coordinates (xy).
	 * Note that u'v' are NOT the u*v* coordinates of the L*u*v* colour space. */
	public static double[] UCSuv_to_xy(double[] UCSuv)
	{
		// extract the chromaticity coordinates
		double uPrime = UCSuv[0];
		double vPrime = UCSuv[1];

		// calculate the CIE 1931 chromaticity coordinates (formula from Wikipedia entry for CIE L*u*v*)
		double x = (9.0*uPrime) / (6.0*uPrime - 16.0*vPrime + 12.0);
		double y = (4.0*vPrime) / (6.0*uPrime - 16.0*vPrime + 12.0);
		double[] xy = { x, y };
		return xy;
	}

	/* Convert the supplied CIE 1964 UCS coordinates (u'v') into u*, v* coordinates from CIE L*u*v*. */
	public static double[] UCSluv_to_LUVluv(double[] UCSluv)
	{
		// extract the chromaticity coordinates
		double lStar = UCSluv[0];
		double uPrime = UCSluv[1];
		double vPrime = UCSluv[2];

		// determine the L*u*v* coordinates
		double uStar = (13.0*lStar) * (uPrime - U_PRIME_W);
		double vStar = (13.0*lStar) * (vPrime - V_PRIME_W);
		double[] LUVluv = { lStar, uStar, vStar };
		return LUVluv;
	}

	/* Convert the supplied CIE L*u*v* coordinates (L*u*v*) into u', v' coordinates from CIE 1964 UCS. */
	public static double[] LUVluv_to_UCSluv(double[] LUVluv)
	{
		// extract the chromaticity coordinates
		double lStar = LUVluv[0];
		double uStar = LUVluv[1];
		double vStar = LUVluv[2];

		// determine the u'v' coordinates
		double uPrime = uStar/(13.0*lStar) + U_PRIME_W;
		double vPrime = vStar/(13.0*lStar) + V_PRIME_W;
		double[] UCSluv = { lStar, uPrime, vPrime };
		return UCSluv;
	}

	/* Convert XYZ to xyY (CIE XYZ chromaticity diagram) coordinates. */
	public static double[] XYZto_xyY(double[] XYZ)
	{
		// extract the input values
		double X = XYZ[0];
		double Y = XYZ[1];
		double Z = XYZ[2];

		// check whether the sum is == 0.0
		// if it is, return the chromaticity coordinates for the whitepoint with Y=0
		double sum = X + Y + Z;
		if (sum == 0.0)
		{
			double sumWhite = X_SUB_W + Y_SUB_W + Z_SUB_W;
			double xWhite = X_SUB_W / sumWhite;
			double yWhite = Y_SUB_W / sumWhite;
			double[] xyY = { xWhite, yWhite, 0.0 };
			return xyY;
		}

		// normalize the values (to flatten to xy chromaticity coordinates)
		double x = X / sum;
		double y = Y / sum;
		double[] xyY = { x, y, Y };
		return xyY;
	}

	/* Convert xyY (CIE XYZ chromaticity diagram coordinates) to XYZ. */
	public static double[] xyYtoXYZ(double[] xyY)
	{
		// extract the input values
		double x = xyY[0];
		double y = xyY[1];
		double Y = xyY[2];

		// return the XYZ values
		double X = (x*Y) / y;
		double Z = ((1.0-x-y) * Y) / y;
		double[] XYZ = { X, Y, Z };
		return XYZ;
	}

	/* Convert the supplied int rgb color to 0.0-1.0 range double rgb color. */
	public static double[] intRGBToDoubleRGB(int rgb)
	{
		// extract the individual rgb values and return
		int[] rgb2 = unpackageIntRGB(rgb);
		return intRGBToDoubleRGB(rgb2[0], rgb2[1], rgb2[2]);
	}

	/* Package the array rgb values into a single int value. */
	public static int packageIntRGB(int[] rgb)
	{
		return packageIntRGB(rgb[0], rgb[1], rgb[2]);
	}

	/* Package the three rgb values into a single int value.
	 * Forces the values to be bounded between 0 and 255. */
	public static int packageIntRGB(int r1, int g1, int b1)
	{
		r1 = Math.max(0, r1);
		r1 = Math.min(255, r1);
		g1 = Math.max(0, g1);
		g1 = Math.min(255, g1);
		b1 = Math.max(0, b1);
		b1 = Math.min(255, b1);
		return (r1 << 16) | (g1 << 8) | (b1);
	}

	/* Unpackage the int rgb value into an array of int rgb values. */
	public static int[] unpackageIntRGB(int rgb)
	{
		int r1 = (rgb & 0x00FF0000) >> 16;
		int g1 = (rgb & 0x0000FF00) >> 8;
		int b1 = (rgb & 0x000000FF);

		int[] RGB = { r1, g1, b1 };
		return RGB;
	}

	/* Convert the supplied int rgb color to 0.0-1.0 range double rgb color. */
	public static double[] intRGBToDoubleRGB(int r1, int g1, int b1)
	{
		// normalize int RGB values to (0-255)
		r1 = Math.max(0, r1);
		r1 = Math.min(255, r1);
		g1 = Math.max(0, g1);
		g1 = Math.min(255, g1);
		b1 = Math.max(0, b1);
		b1 = Math.min(255, b1);

		// normalize to 0.0-1.0
		double R = (double) r1 / 255.0;
		double G = (double) g1 / 255.0;
		double B = (double) b1 / 255.0;

		// return the normalized values
		double[] RGB = { R, G, B };
		return RGB;
	}

	/* Convert the supplied double RGB color to 0-255 range int RGB color. */
	public static int[] doubleRGBToIntRGB(double[] rgb)
	{
		// convert values back to integer
		int r1 = (int) Math.round(rgb[0] * 255.0);
		int g1 = (int) Math.round(rgb[1] * 255.0);
		int b1 = (int) Math.round(rgb[2] * 255.0);

		// return integer values
		int[] RGB = { r1, g1, b1 };
		return RGB;
	}
}