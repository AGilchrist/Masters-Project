/*
Code property of Alistair Gilchrist
*/

package com.dundeeuni.Alistair.cvdvision;

public class Profile {

	//double variables for the severity of a profile's Red-Green colour blindness and its
	//Blue-Yellow colour blindness
	private double RGSeverity, BYSeverity;
	//Boolean variables to indicate if the profile has Protan, Duetan or Tritan CVD
	private Boolean Protan, Deutan, Tritan;
	
	Profile(Boolean protan, Boolean deutan, Boolean tritan, double RGAmount, double BYAmount){
		//set each of the profile class's variables
		Protan = protan;
		Deutan = deutan;
		Tritan = tritan;
		RGSeverity = RGAmount;
		BYSeverity = BYAmount;
	}
	
	public String getData(){
		//return all the the profile's variables as a single string
		return (Protan+" "+Deutan+" "+Tritan+" "+RGSeverity+" "+BYSeverity);
	}
	
}
