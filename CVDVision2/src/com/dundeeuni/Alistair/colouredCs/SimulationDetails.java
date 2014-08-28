//Code belongs to Dr David Flatla

package com.dundeeuni.Alistair.colouredCs;

public class SimulationDetails
{
	public static final String PROTAN = "protan";
	public static final String DEUTAN = "deutan";
	public static final int MIN_AMOUNT = 0;
	public static final int MAX_AMOUNT = 200;

	private String rgType = PROTAN;
	private double rgAmount = 0.0;
	private double byAmount = 0.0;

	/* Create a new SimulationDetails object with PROTAN as the rgType. */
	public static SimulationDetails createProtanSimulationDetails()
	{
		return new SimulationDetails(PROTAN, 0.0, 0.0);
	}

	/* Create a new SimulationDetails object with DEUTAN as the rgType. */
	public static SimulationDetails createDeutanSimulationDetails()
	{
		return new SimulationDetails(DEUTAN, 0.0, 0.0);
	}

	/* Private constructor for creating the specified SimulationDetails. */
	private SimulationDetails(String newRGType, double newRGAmount, double newBYAmount)
	{
		this.rgType = newRGType;
		this.rgAmount = newRGAmount;
		this.byAmount = newBYAmount;
	}

	/* Are these SimulationDetails for a protan individual? */
	public boolean isProtan()
	{
		return PROTAN.equals(rgType);
	}

	/* Set this SimulationDetails to be for a protan individual. */
	public void setToProtan()
	{
		this.rgType = PROTAN;
	}

	/* Are these SimulationDetails for a deutan individual? */
	public boolean isDeutan()
	{
		return DEUTAN.equals(rgType);
	}

	/* Set this SimulationDetails to be for a deutan individual. */
	public void setToDeutan()
	{
		this.rgType = DEUTAN;
	}

	/* Get the current red-green confusion amount.
	 * 0.0 => no confusion
	 * >0.0 => some confusion (no maximum) */
	public double getRGAmount()
	{
		return rgAmount;
	}

	/* Set the rgAmount to the specified value (>= 0.0). */
	public void setRGAmount(double newRGAmount)
	{
		if (newRGAmount < 0.0) {
			throw new RuntimeException("SimulationDetails.setRGAmount: parameter must be >= 0.0, but is " + newRGAmount);
		}
		this.rgAmount = newRGAmount;
	}

	/* Get the current blue-yellow confusion amount.
	 * 0.0 => no confusion
	 * >0.0 => some confusion (no maximum) */
	public double getBYAmount()
	{
		return byAmount;
	}

	/* Set the byAmount to the specified value (>= 0.0). */
	public void setBYAmount(double newBYAmount)
	{
		if (newBYAmount < 0.0) {
			throw new RuntimeException("SimulationDetails.setBYAmount: parameter must be >= 0.0, but is " + newBYAmount);
		}
		this.byAmount = newBYAmount;
	}
}
