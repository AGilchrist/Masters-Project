/*
Code property of Alistair Gilchrist
*/

package com.dundeeuni.Alistair.cvdvision;

import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class Profiles {
	//the number of profiles that exist
	private int ProfileCounter = 0;
	//arrange the application's profiles as a sorted map of key-value pairs with the name
	//given to the profiles by the user as the key and the profile of class profile as the
	//value
	SortedMap<String, Profile> myProfiles;
	
	public Profiles(){
		//create a new treepmap of kay-value pairs with a string for a key(the profile name)
		//and the a class profile for the value
		myProfiles = new TreeMap<String, Profile>();
	}
	
	public void createProfile(String name, Boolean protan, Boolean deutan, Boolean tritan, double RGAmount, double BYAmount){
		//add to the sortedmap a new profile with the name as the key passing all of the 
		//profile's data into the profile's constructor
		myProfiles.put(name, new Profile(protan, deutan, tritan, RGAmount, BYAmount));
		//increment the counter
		ProfileCounter = myProfiles.size();
	}
	
	public void deleteProfile(String name){
		//remove the specified profile from the sorted map (i.e. delete the profile)
		myProfiles.remove(name);
		//reduce the counter by one
		ProfileCounter = myProfiles.size();
	}
	
	public Set<String> retrieveAllProfileNames(){
		//retrieve the keyset of profile names
		return myProfiles.keySet();
	}
	
	public Profile retrieveProfile(String name){
		//retrieve the a specified profile from the sorted map
		Profile profile = myProfiles.get(name);
		//return the profile
		return profile;
	}
	
	public int getProfileNumber(){
		//retrieve the total number of profiles that exist
		return ProfileCounter;
	}

}
