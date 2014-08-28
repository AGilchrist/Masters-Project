//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.colouredCs;

public enum CalibrationType implements Comparable<CalibrationType>
{
	LUMINANCE_UP("LUMINANCE_UP", 0),
	LUMINANCE_DOWN("LUMINANCE_DOWN", 1),
	HUE("HUE", 2);

	private String name;
	private int index;
	CalibrationType(String name, int index)
	{
		this.name = name;
		this.index = index;
	}

	/* Return the name for this CalibrationType. */
	public String getName()
	{
		return name;
	}

	/* Return the index for this CalibrationType. */
	public int getIndex()
	{
		return index;
	}
}
