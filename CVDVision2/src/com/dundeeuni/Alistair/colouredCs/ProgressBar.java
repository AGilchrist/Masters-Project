/*package com.dundeeuni.Alistair.colouredCs;

import processing.core.*;

public class ProgressBar
{
	PApplet app = new PApplet();
	int notDoneColor = 0x00404040;
	int doneColor    = 0x00C0C0C0;
	int width;
	int height;
	double progress; // a value between 0.0 and 1.0 (inclusive)
	public PImage image;

	public ProgressBar(int width, int height)
	{
		this.width = width;
		this.height = height;
		this.progress = 0.0;
		this.image = app.createImage(this.width, this.height, processing.core.PConstants.RGB);
		updateImage();
	}

	// Set the progress of this progress bar. //
	public void setProgress(double newProgress)
	{
		if (newProgress >= 0.0 && newProgress <= 1.0)
		{
			this.progress = newProgress;
			updateImage();
		}
	}

	// Update the image provided by this class. //
	public void updateImage()
	{
		int boundary = (int) Math.round(width*progress);
		image.loadPixels();
		for (int y=0; y<image.height; y=y+1)
		{
			for (int x=0; x<image.width; x=x+1)
			{
				if (x < boundary)
				{
					image.set(x, y, doneColor);
				}
				else
				{
					image.set(x, y, notDoneColor);
				}
			}
		}
		image.updatePixels();
	}
}*/
