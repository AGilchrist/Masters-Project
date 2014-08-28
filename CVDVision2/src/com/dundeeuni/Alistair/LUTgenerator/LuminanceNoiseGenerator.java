//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.LUTgenerator;

public class LuminanceNoiseGenerator
{
	public static int[] generateLuminanceNoise(int rgb1, double[] luminanceNoise, boolean allowOutOfGamutColors)
	{
		double EPSILON = 2.5;
		int[] result = new int[luminanceNoise.length];
		double[] LUV1 = ColorConverter.RGBtoLUV(rgb1);
		for (int n=0; n<luminanceNoise.length; n=n+1)
		{
			// determine this particular luminance value (capped to legal luminance bounds)
			double newLuminance = LUV1[0] + luminanceNoise[n];
/*			if (newLuminance < ColorConverter.MIN_LUM_LUV) {
				newLuminance = ColorConverter.MIN_LUM_LUV;
			}
			else if (newLuminance > ColorConverter.MAX_LUM_LUV) {
				newLuminance = ColorConverter.MAX_LUM_LUV;
			}
*/
			// generate the LUV color with modified luminance, and convert this to RGB
			double[] LUV2 = { newLuminance, LUV1[1], LUV1[2] };
			int rgb2 = ColorConverter.LUVtoRGB(LUV2);

			// check to see if the resulting RGB color is out-of-gamut, but only if it matters
			if (!allowOutOfGamutColors) {
				double[] LUV3 = ColorConverter.RGBtoLUV(rgb2);
				double difference = ColorConverter.findDistance(LUV2, LUV3);
				if (difference > EPSILON) {
					return null;
				}
			}
			result[n] = rgb2;
		}
		return result;
	}

	/* For the supplied 'rgb' color, generate an array of int rgb colors that vary
	 * only in the amount of luminance.  The luminance noise is defined in the
	 * 'luminanceNoise' array.  Negative luminanceNoise values will produce darker
	 * output rgb colors.  Positive luminanceNoise values will produce lighter
	 * output rgb colors.  The two boolean parameters alter the behaviour of the
	 * method.  If 'capLuminance' is set to true, then the maximum luminance possible
	 * is capped at ColorConverter.MAX_L, and the minimum luminace possible is capped
	 * at ColorConverter.MIN_L.  Likewise, if 'capRGB' is set to true, then the minimum
	 * and maximum RGB channel values permitted are 0 and 255, respectively. If, regardless
	 * of cap settings, the rgb color produced is invalid, then 'null' is returned.
	 * 'useExtremeRGBValues' specifies whether RGB channel values of 0 and 255 are allowed.
	 * If set to false, RGB channel values are limited to 1-254, otherwise they are allowed
	 * to be 0-255 (true). */
	public static int[] generateLuminanceNoise(int rgb, double[] luminanceNoise, boolean capLuminance, boolean capRGB, boolean useExtremeRGBValues)
	{
		int[] result = new int[luminanceNoise.length];
		double[] LUV = ColorConverter.RGBtoLUV(rgb);
		for (int n=0; n<luminanceNoise.length; n=n+1)
		{
			double noise = luminanceNoise[n];
			// check to see if the luminance exceeds the maximum possible luminance
			if (LUV[0]+noise > ColorConverter.MAX_LUM_LUV) {
				if (capLuminance) {
					noise = ColorConverter.MAX_LUM_LUV - LUV[0];
				}
				else {
					return null;
				}
			}
			// check to see if the luminance falls below the minimum possible luminance
			if (LUV[0]+noise < ColorConverter.MIN_LUM_LUV) {
				if (capLuminance) {
					noise = -1.0 * (LUV[0] - ColorConverter.MIN_LUM_LUV);
				}
				else {
					return null;
				}
			}

			// generate the LUV color with modified luminance, and convert this to RGB
			double[] Luv2 = { LUV[0]+noise, LUV[1], LUV[2] };
			int[] RGB2 = ColorConverter.unpackageIntRGB(ColorConverter.LUVtoRGB(Luv2));
			int rgbMin, rgbMax;
			if (useExtremeRGBValues) {
				rgbMin = 0;
				rgbMax = 255;
			}
			else {
				rgbMin = 1;
				rgbMax = 254;
			}

			if (RGB2[0] < rgbMin || RGB2[1] < rgbMin || RGB2[2] < rgbMin) {
				if (capRGB) {
					RGB2[0] = Math.max(RGB2[0], rgbMin);
					RGB2[1] = Math.max(RGB2[1], rgbMin);
					RGB2[2] = Math.max(RGB2[2], rgbMin);
				}
				else {
					return null;
				}
			}
			if (RGB2[0] > rgbMax || RGB2[1] > rgbMax || RGB2[2] > rgbMax) {
				if (capRGB) {
					RGB2[0] = Math.min(RGB2[0], rgbMax);
					RGB2[1] = Math.min(RGB2[1], rgbMax);
					RGB2[2] = Math.min(RGB2[2], rgbMax);
				}
				else {
					return null;
				}
			}
			result[n] = ColorConverter.packageIntRGB(RGB2);
		}
		return result;
	}

	public static void main(String[] args)
	{
		test1(8);
	}

	/* Generate every RGB color and generate stats regarding how they fail for 25% RLM (-12.5, 12.5). */
	public static void test1(int step)
	{
		int count=0;
		double[] RLM = { -12.5, 12.5 };
		int rgb, fff=0, fft=0, ftf=0, ftt=0, tff=0, tft=0, ttf=0, ttt=0;
		for (int r=0; r<=255; r=r+step)
		{
			System.out.print(r + " ");
			for (int g=0; g<=255; g=g+step)
			{
				for (int b=0; b<=255; b=b+step)
				{
					count = count + 1;
					rgb = ColorConverter.packageIntRGB(r, g, b);
					if (null == generateLuminanceNoise(rgb, RLM, false, false, false))	{ fff++; }
					if (null == generateLuminanceNoise(rgb, RLM, false, false, true))	{ fft++; }
					if (null == generateLuminanceNoise(rgb, RLM, false, true, false))	{ ftf++; }
					if (null == generateLuminanceNoise(rgb, RLM, false, true, true))	{ ftt++; }
					if (null == generateLuminanceNoise(rgb, RLM, true, false, false))	{ tff++; }
					if (null == generateLuminanceNoise(rgb, RLM, true, false, true))	{ tft++; }
					if (null == generateLuminanceNoise(rgb, RLM, true, true, false))	{ ttf++; }
					if (null == generateLuminanceNoise(rgb, RLM, true, true, true))		{ ttt++; }
				}
			}
		}

		System.out.println("\n\nTotal Trials\t" + count);
		System.out.println("CAP_LUM\tCAP_RGB\tUSE_0+255\tFAIL_#");
		System.out.println("false\tfalse\tfalse\t" + fff);
		System.out.println("false\tfalse\ttrue\t" + fft);
		System.out.println("false\ttrue\tfalse\t" + ftf);
		System.out.println("false\ttrue\ttrue\t" + ftt);
		System.out.println("true\tfalse\tfalse\t" + tff);
		System.out.println("true\tfalse\ttrue\t" + tft);
		System.out.println("true\ttrue\tfalse\t" + ttf);
		System.out.println("true\ttrue\ttrue\t" + ttt);
	}
}
