//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.colouredCs;

import java.util.*;

import android.content.Context;
import android.widget.Toast;

import com.dundeeuni.Alistair.LUTgenerator.*;

public class CalibrationTrial implements CalibrationConstants, Comparable<CalibrationTrial>
{
	CalibrationType ct;
	double hueAngle;
	int[] baseColorChannels;
	public ArrayList<Integer[]> fullWalkingColors;
	private int[] originalColors;
	private int minIndex;
	private int maxIndex;
	private Toast toast;

	/* Construct a new CalibrationTrial. */
	public CalibrationTrial(CalibrationType ct, double hueAngle, int baseColor, int[] testingLineColors, int[] originalColors)
	{
		this.ct = ct;
		this.hueAngle = hueAngle;
		this.baseColorChannels = ColorConverter.unpackageIntRGB(baseColor);
		this.fullWalkingColors = new ArrayList<Integer[]>();
		this.originalColors = originalColors;
		int[] walkingColors;
		switch (ct)
		{
			case HUE:
				populateFullWalkingColors(testingLineColors);
				break;
			case LUMINANCE_UP:
				walkingColors = new int[256-Math.max(baseColorChannels[0], Math.max(baseColorChannels[1], baseColorChannels[2]))];
				for (int i=0; i<walkingColors.length; i=i+1) {
					walkingColors[i] = ColorConverter.packageIntRGB(baseColorChannels[0]+i, baseColorChannels[1]+i, baseColorChannels[2]+i);
				}
				populateFullWalkingColors(walkingColors);
				break;
			case LUMINANCE_DOWN:
				walkingColors = new int[1+Math.min(baseColorChannels[0], Math.min(baseColorChannels[1], baseColorChannels[2]))];
				for (int i=0; i<walkingColors.length; i=i+1) {
					walkingColors[i] = ColorConverter.packageIntRGB(baseColorChannels[0]-i, baseColorChannels[1]-i, baseColorChannels[2]-i);
				}
				populateFullWalkingColors(walkingColors);
				break;
		}
		this.minIndex = 0;
		this.maxIndex = fullWalkingColors.size()-1;
	}

	/* A test to see if the trial is on its first presentation (at maxIndex). */
	private boolean isFirstPresentation()
	{
		return (minIndex == 0 && maxIndex == fullWalkingColors.size()-1);
	}

	/* Manipulate minIndex and maxIndex depending on whether the user could see a difference or not. */
	public void recordIsDifferentiable(boolean isDifferentiable, Context context, Boolean skipped)
	{
		if (isFirstPresentation())
		{
//			maxIndex = maxIndex-1;
			// decrease maxIndex if the colors were differentiable
			if (isDifferentiable)
			{
				toast = Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT);
				toast.show();
				maxIndex = maxIndex-1;
			}
			// stop trial if the colors were not differentiable
			else
			{
				if(skipped == true)
					toast = Toast.makeText(context, "Skipped", Toast.LENGTH_SHORT);
				else
					toast = Toast.makeText(context, "Wrong!", Toast.LENGTH_SHORT);
				toast.show();
				minIndex = maxIndex; // this means the trial is finished - we found no differences ever
			}
		}
		else
		{
			// decrease maxIndex if the colors were differentiable
			if (isDifferentiable)
			{
				toast = Toast.makeText(context, "Correct!", Toast.LENGTH_SHORT);
				toast.show();
				maxIndex = getCurrentIndex();
			}
			// increase minIndex if the colors were not differentiable
			else
			{
				if(skipped == true)
					toast = Toast.makeText(context, "Skipped", Toast.LENGTH_SHORT);
				else
					toast = Toast.makeText(context, "Wrong!", Toast.LENGTH_SHORT);
				toast.show();
				minIndex = getCurrentIndex();
			}
		}
	}

	/* Get the current index to test for this trial. */
	public int getCurrentIndex()
	{
		if (isFirstPresentation())
		{
			return maxIndex;
		}
		else
		{
			return (maxIndex+minIndex)/2;
		}
	}

	/* Return whether this trial has finished or not. */
	public boolean isFinished()
	{
		return (minIndex+1) >= maxIndex;
	}

	/* Summarize this Trial as a String and return. */
	public String toString()
	{
		// determine base color and search colors and their properties
//		int baseColor = fullWalkingColors.get(0)[RLM_VALUES.length/2];
//		int minColor = fullWalkingColors.get(minIndex)[RLM_VALUES.length/2];
//		int maxColor = fullWalkingColors.get(maxIndex)[RLM_VALUES.length/2];

		// use the original colors, not the possibly modified colors
		int baseColor = originalColors[0];
		int minColor = originalColors[minIndex];
		int maxColor = originalColors[maxIndex];

		double[] baseLUV = ColorConverter.RGBtoLUV(baseColor);
		double[] minLUV = ColorConverter.RGBtoLUV(minColor);
		double[] maxLUV = ColorConverter.RGBtoLUV(maxColor);
		double averageLimitL = (minLUV[0] + maxLUV[0]) / 2.0;
		double averageLimitU = (minLUV[1] + maxLUV[1]) / 2.0;
		double averageLimitV = (minLUV[2] + maxLUV[2]) / 2.0;

		// output the relevant data
		String separator = "   ";
		StringBuffer result = new StringBuffer();
		result.append(ct.getName());
		result.append(separator);
		switch(ct)
		{
			case LUMINANCE_UP: case LUMINANCE_DOWN:
				result.append("-9.9");	// negative hue angle indicates a luminance measurement
				result.append(separator);
				result.append(Math.abs(baseLUV[0] - averageLimitL));	// distance
				break;
			case HUE:
				result.append(hueAngle);	// hue angle
				result.append(separator);
				result.append(ColorConverter.findDistance(baseLUV[1], baseLUV[2], averageLimitU, averageLimitV)); // distance
				break;
		}
		result.append(separator);
		result.append(averageLimitL);	// luminance of final colour
		result.append(separator);
		result.append(averageLimitU);	// u-coordinate of final colour
		result.append(separator);
		result.append(averageLimitV);	// v-coordinate of final colour
		return result.toString();
	}

	/* Compare this CalibrationTrial with another. */
	public int compareTo(CalibrationTrial other)
	{
		return (int) (Math.round(this.hueAngle - other.hueAngle));
	}

	/* For each color in 'walkingColors', generate its luminance mask RGB colors.
	 * If all the luminance mask colors are good, then add the luminance mask to 'fullWalkingColors'. */
	private void populateFullWalkingColors(int[] walkingColors)
	{
		// move through each color in 'walkingColors'
		for (int center : walkingColors)
		{
			// generate that color's luminance noise
			int[] lumNoise = LuminanceNoiseGenerator.generateLuminanceNoise(center, RLM_VALUES, true, true, true);

			// if the luminance noise was successfully generated, then add it to the fullWalkingColors set
			if (lumNoise != null)
			{
				// convert to an Integer array first
				Integer[] lumNoiseInteger = new Integer[lumNoise.length];
				for (int i=0; i<lumNoise.length; i=i+1) {
					lumNoiseInteger[i] = new Integer(lumNoise[i]);
				}

				// then add
				fullWalkingColors.add(lumNoiseInteger);
			}
		}
	}
}
