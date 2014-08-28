//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.LUTgenerator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.os.Environment;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

public class GenerateLUTs
{
	public static final String PATH = "file:///mnt/sdcard/CVDVisionFiles/LUT/";
	public static final String ORIGINAL_FILE = PATH + "RGB.small.png";
	public static final String PROTAN_FILE = PATH + "RGB.small.protan.png";
	public static final String DEUTAN_FILE = PATH + "RGB.small.deutan.png";
	public static final String TRITAN_FILE = PATH + "RGB.small.tritan.png";
	
	private static ImageSize targetSize = new ImageSize(512, 512);

	public static final double[] PROT_VALUES = { 0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0 };	// 165.0
	public static final double[] DEUT_VALUES = { 0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0 };	// 165.0
	public static final double[] TRIT_VALUES = { 0.0, 10.0, 20.0, 30.0, 40.0, 50.0, 60.0, 70.0, 80.0, 90.0, 100.0, 110.0, 120.0, 130.0, 140.0, 150.0, 160.0 };	// 165.0

	private static Bitmap originalImage, protanImage, deutanImage, tritanImage, lutImage;
	private static int[] originalRGBs, protanRGBs, deutanRGBs, tritanRGBs, lutRGBs;
	
	private static int rIndex, gIndex, bIndex, rgIndex, byIndex, rgDichRGB, rgRGB, byDichRGB, byShiftedDichRGB, simulatedPixelRGB, finalPixelRGB, aIndex;
	private static double[] rgDichLUV, origLUV, rgLUV, byDichLUV, byShiftedDichLUV, shiftedBY_DichLUV, byLUV; 
	
	public static void setup(ImageLoader loader) throws IOException{
		// load each dichromat colour look-up-table
		originalImage = loader.loadImageSync(ORIGINAL_FILE, targetSize);
		protanImage = loader.loadImageSync(PROTAN_FILE, targetSize);
		deutanImage = loader.loadImageSync(DEUTAN_FILE, targetSize);
		tritanImage = loader.loadImageSync(TRITAN_FILE, targetSize);
		lutImage = originalImage.copy(Config.ARGB_8888, true);
		
		//declare the arrays that will hold the contents of each look-up-table to avoid a null pointer exception in the next step
		originalRGBs = new int[(originalImage.getWidth()*originalImage.getHeight())];
		protanRGBs = new int[(protanImage.getWidth()*protanImage.getHeight())];
		deutanRGBs = new int[(deutanImage.getWidth()*deutanImage.getHeight())];
		tritanRGBs = new int[(tritanImage.getWidth()*tritanImage.getHeight())];
		lutRGBs = new int[(lutImage.getWidth()*lutImage.getHeight())];

		// get the contents of each look-up-table as a 1D array
		originalImage.getPixels(originalRGBs, 0, originalImage.getWidth(), 0, 0, originalImage.getWidth(), originalImage.getHeight());
		protanImage.getPixels(protanRGBs, 0, protanImage.getWidth(), 0, 0, protanImage.getWidth(), protanImage.getHeight());
		deutanImage.getPixels(deutanRGBs, 0, deutanImage.getWidth(), 0, 0, deutanImage.getWidth(), deutanImage.getHeight());
		tritanImage.getPixels(tritanRGBs, 0, tritanImage.getWidth(), 0, 0, tritanImage.getWidth(), tritanImage.getHeight());
		lutImage.getPixels(lutRGBs, 0, lutImage.getWidth(), 0, 0, lutImage.getWidth(), lutImage.getHeight());
		
	}
	
	public static void main(Boolean protan, Boolean deutan, Boolean tritan, double RGAmount, double BYAmount) throws java.io.IOException
	{
		// iterate through all the possible values for LUT inputs
		//for (double trit : TRIT_VALUES) {
			//for (double prot : PROT_VALUES) {
				// generate the protan LUTs
				// create a File object for the output file
		if(protan == true && deutan == false){
				for (int i=0; i<originalRGBs.length; i++) {
					lutRGBs[i] = shiftColorRG_BY(originalRGBs[i], RGAmount, BYAmount, protanRGBs, tritanRGBs);
				}
				// output final LUT image
				lutImage.setPixels(lutRGBs, 0, lutImage.getWidth(), 0, 0, lutImage.getWidth(), lutImage.getHeight());
		}else
		if(deutan == true && protan == false){
			for (int i=0; i<originalRGBs.length; i++) {
				lutRGBs[i] = shiftColorRG_BY(originalRGBs[i], RGAmount, BYAmount, deutanRGBs, tritanRGBs);
			}
			// output final LUT image
			lutImage.setPixels(lutRGBs, 0, lutImage.getWidth(), 0, 0, lutImage.getWidth(), lutImage.getHeight());
		}else
		if(tritan == true){
			for (int i=0; i<originalRGBs.length; i++) {
				lutRGBs[i] = shiftColorRG_BY(originalRGBs[i], 0, BYAmount, protanRGBs, tritanRGBs);
			}
			// output final LUT image
			lutImage.setPixels(lutRGBs, 0, lutImage.getWidth(), 0, 0, lutImage.getWidth(), lutImage.getHeight());
		}
		else{
			for (int i=0; i<originalRGBs.length; i++) {
				lutRGBs[i] = shiftColorRG_BY(originalRGBs[i], 0, 0, protanRGBs, tritanRGBs);
			}
			// output final LUT image
			lutImage.setPixels(lutRGBs, 0, lutImage.getWidth(), 0, 0, lutImage.getWidth(), lutImage.getHeight());
		}
				
				File fileDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/CVDVisionFiles/LUT/");
				fileDirectory.mkdirs();
				// create a File object for the output file
				File outputFile = new File(fileDirectory, "personal.png");
				// now attach the OutputStream to the file object, instead of a String representation
				FileOutputStream out = new FileOutputStream(outputFile);
			    lutImage.compress(CompressFormat.PNG, 100, out);
			    out.flush();
			    out.close();
	}

	/* Perform a single dichromatic shift of 'origLUV' toward 'dichLUV' by 'shiftAmount' distance. */
	private static double[] shiftColor(double[] origLUV, double[] dichLUV, double shiftAmount)
	{
		// use the dichromatic color as a default replacement color for the simulation
		double[] resultLUV = { dichLUV[0], dichLUV[1], dichLUV[2] };

		// test to see if the person has 'less than' dichromatic CVD 
		if (ColorConverter.findDistance(origLUV, dichLUV) > shiftAmount) {
			// translate both colors so that 'origLUV' is at the origin
			// 'draw' a sphere around the origin (origLUV)
			// 'draw' a ray from the origin to the translated 'dichLUV' coordinates
			// substitute the parametric equation for the ray into the equation for a sphere
			// 		ray starting at origin = x*t, y*t, z*t; where x,y,z are coordinates for translated 'dichLUV'
			// 		sphere centered on origin with radius 'r' = x^2 + y^2 + z^2 = r^2
			//		solve for (tx)^2 + (ty)^2 + (tz)^2 = r^2; where x,y,z are coordinates for translated 'dichLUV'
			// 		gives t = r/sqrt(x^2 + y^2 + z^2)
			// multiply the translated 'dichLUV' coordinates by t to get the translated intersection
			// 'untranslate' the intersection so 'origLUV' is back where it started

			// translate both colours so that origLUV is at the origin
			double[] transDichLUV = { dichLUV[0]-origLUV[0], dichLUV[1]-origLUV[1], dichLUV[2]-origLUV[2] };

			// 'shiftAmount' is the radius for the sphere around the origin (shifted origLUV)
			double t = shiftAmount / Math.sqrt((transDichLUV[0]*transDichLUV[0]) + (transDichLUV[1]*transDichLUV[1]) + (transDichLUV[2]*transDichLUV[2]));

			// find the intersection and translate it back to its original position
			resultLUV[0] = (t*transDichLUV[0]) + origLUV[0];
			resultLUV[1] = (t*transDichLUV[1]) + origLUV[1];
			resultLUV[2] = (t*transDichLUV[2]) + origLUV[2];
		}
		return resultLUV;
	}

	/* Shift the supplied 'origRGB' colour by 'rgShift' then 'byShift' amounts. 
	 * 'rgRGBs' is the LUT array for dichromatic RG colours (protan xor deutan),
	 * 'byRGBs' is the LUT array for dichromatic BY colours (tritan). */
	private static int shiftColorRG_BY(int origRGB, double rgShift, double byShift, int[] rgRGBs, int[] byRGBs)
	{
		// RG shifting
		// calculate the index to look up the R-G dichromat simulation colour in 'rgRGBs'
		rIndex = ((origRGB & 0x00FF0000) >> 18);  // special to the small simulation files (16 + 2)
		gIndex = ((origRGB & 0x0000FF00) >> 10);  // special to the small simulation files ( 8 + 2)
		bIndex = ((origRGB & 0x000000FF) >>  2);  // special to the small simulation files ( 0 + 2)
		rgIndex = (rIndex << 12) | (gIndex << 6) | bIndex;

		// find the dichromatic colour and shift the original colour towards it
		rgDichRGB = 0x00FFFFFF & rgRGBs[rgIndex];
		rgDichLUV = ColorConverter.RGBtoLUV(rgDichRGB);
		origLUV = ColorConverter.RGBtoLUV(origRGB);
		rgLUV = shiftColor(origLUV, rgDichLUV, rgShift);


		// BY shifting
		// start with the result from the RG shifting just performed above
		rgRGB = ColorConverter.LUVtoRGB(rgLUV);

		// get the tritan dichromat RGB value and its LUV coordinates
		rIndex = ((rgRGB & 0x00FF0000) >> 18);  // special to the small simulation files (16 + 2)
		gIndex = ((rgRGB & 0x0000FF00) >> 10);  // special to the small simulation files ( 8 + 2)
		bIndex = ((rgRGB & 0x000000FF) >>  2);  // special to the small simulation files ( 0 + 2)
		byIndex = (rIndex << 12) | (gIndex << 6) | bIndex;

		// find the dichromatic colour and shift the rg-shifted colour towards it
		byDichRGB = 0x00FFFFFF & byRGBs[byIndex];
		byDichLUV = ColorConverter.RGBtoLUV(byDichRGB);

		// shift the dichromatic colour according to the shift performed above (RG-shift) to prevent amplifying colours instead of reducing them
		// get the tritan dichromat RGB value and its LUV coordinates
		rIndex = ((byDichRGB & 0x00FF0000) >> 18);  // special to the small simulation files (16 + 2)
		gIndex = ((byDichRGB & 0x0000FF00) >> 10);  // special to the small simulation files ( 8 + 2)
		bIndex = ((byDichRGB & 0x000000FF) >>  2);  // special to the small simulation files ( 0 + 2)
		byIndex = (rIndex << 12) | (gIndex << 6) | bIndex;

		// find the dichromatic colour and shift the rg-shifted colour towards it
		byShiftedDichRGB = 0x00FFFFFF & rgRGBs[byIndex];
		byShiftedDichLUV = ColorConverter.RGBtoLUV(byShiftedDichRGB);
		double[] grey = { byShiftedDichLUV[0], 0.0, 0.0 };
		shiftedBY_DichLUV = shiftColor(byDichLUV, grey, rgShift);
		byLUV = shiftColor(rgLUV, shiftedBY_DichLUV, byShift);

		// find the final RGB color and return it
		simulatedPixelRGB = ColorConverter.LUVtoRGB(byLUV);
		finalPixelRGB = (0xFF000000 | simulatedPixelRGB );
		return finalPixelRGB;	// old return type
		
		// get the final RGB colour and return its 3 components as a short array
//		int[] finalRGB = ColorConverter.unpackageIntRGB(ColorConverter.LUVtoRGB(byLUV));
//		short[] finalShortRGB = {(short) finalRGB[0], (short) finalRGB[1], (short) finalRGB[2] };
//		return finalShortRGB;
	}
}
