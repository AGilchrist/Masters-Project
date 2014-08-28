/*
Code property of Alistair Gilchrist
*/

package com.dundeeuni.Alistair.colouredCs;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import com.dundeeuni.Alistair.LUTgenerator.GenerateLUTs;
import com.dundeeuni.Alistair.cvdvision.*;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ColouredCActivity extends Activity implements OnClickListener, CalibrationConstants {
	
	//a load of variable need for this activity
	static SimulationDetails simulationDetails = null;
	static ArrayList<CalibrationTrial> trialData;
	private Profiles myProfiles;
	private Set<String> ProfileNames;
	private CVDdetails details;
	private ColouredCs test;
	private ImageLoader imageLoader;
	
	private RelativeLayout frameLayout;
	private EditText text;
	private TextView prompt;
	private MenuItem mDynamicMenuItem;
	private Button mProfileButton;

	private Bitmap[] backgroundMasks = new Bitmap[8];
	private static float threshold = 10;
	public static final String PATH = "file:///mnt/sdcard/CVDVisionFiles/LUT/";
	private CharSequence name;
	private String result, message;
	private int width, height, skipCount, limit;
	private double protan, protan1, protan2, deutan, deutan1, deutan2, tritan, tritan1, tritan2;
	private long[] reaction_times = new long[60];
	private int[][] Ccolours = new int[60][3];

	private Toast toast;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set the content view
		setContentView(R.layout.activity_coloured_c);
		//run the setup function to set up a load of variable for use
		setup();
		//declare a button variable for the button on the content view
		Button captureBtn = (Button)findViewById(R.id.coloured_C_btn);
		//listen for a click on the button
		captureBtn.setOnClickListener(this);
	}

	public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        try {
        	//set the base to four to avoid any confusion since their are four view modes based
        	//on Protan, Deutan, Tritan and Monochromatic view in the real-time simulation
        	//activity
            int base = 4;
            //for each name in the list of Profile Names
            for(String name : ProfileNames){
            	//add the word Sight to the end of the profile name
                String id = name + " Sight";
                //add an item to the drop-down menu with an ItemID and order equal to base value
                //and the id variable as its title
                mDynamicMenuItem.getSubMenu().add(0, base, base, id);
                //increment the base variable to prevent items overwriting each other
                base++;
            }
            //set the limit variable as the base so that it can be used in a if statement to check
            //for the pressing of an item in the menu
            limit = base;
        } catch (Exception e) {
        	//give a toast message to the scren if something went wrong
        	toast = Toast.makeText(this, "Error adding profile to menu", Toast.LENGTH_SHORT);
        	toast.show();
        }
        //set the menu to be visible
        mDynamicMenuItem.setVisible(true);
        return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.coloured_c, menu);
		//set the DynamicMenuItem variable to the Mode_Menu of the main activity action bar
		mDynamicMenuItem = menu.findItem(R.id.Mode_Menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//get the id of the item selected
		int id = item.getItemId();
		//get the title of the item selected and transform it to a string
    	String title = item.getTitle().toString();
    	//check if it was the stop item selected
		if(id == R.id.Stop){
			//run the end function
			end();
	        return true;
		}else
		//check if the ItemID is that of one of the profile items added to the DynamicMenuItem
		//in the onPrepareOptionsMenu function
    	if(id >= 4 && id < limit){
    		//subtract 6 from the title to remove the whitespace and the word Sight that was
    		//added to the title to avoid any crashes
    		title = title.substring(0, title.length()-6);
    		//if the selected title is the same as the title of the profile that the current personal LUT is for
    		if(title.equals(details.getCurrentGenerated()))
    		//run the personalSimualtion function passing through the title which will now be
    		//the name of the profile that was selected to be simulated and a Boolean to tell the function not to generate a LUT for this profile
    			personalSimulation(title, true);
    		else
    		//else run the personalSimulation function passing through a false Boolean to tell the function to generate a new personal LUT based off of the profile's data
    			personalSimulation(title, false);
	    	return true;
	    }else{
	    	return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	public void onClick(View v) {
		//if the start the activity button was pressed
		if (v.getId() == R.id.coloured_C_btn) { 
	    	try {
	    		//set the content view to be the blank background
	    		setContentView(R.layout.blank);
	    		//set the ProfileButton variable to be the save profile button on the blank layout 
	    		mProfileButton = (Button)findViewById(R.id.save_profile);
	    		//set the button to NOT be visible
	    		mProfileButton.setVisibility(View.GONE);
	    			    		
	    		//set the frameLayout variable to be the blank layout
	    		frameLayout = (RelativeLayout)findViewById(R.id.Blank);
	    		//set the content variable to be the device's window
	    		View content = getWindow().findViewById(Window.ID_ANDROID_CONTENT);
	    		//set the width and height variables to be the width and height of the device's
	    		//screen
	    		width = content.getWidth();
	    		height = content.getHeight();
	    		
	    		//set the prompt variable to be the TextView on the blank layout
	    		prompt = (TextView)frameLayout.findViewById(R.id.text);
	    		//set the prompt to NOT be seen
	    		prompt.setVisibility(View.GONE);
	    		//set the text variable to be the editText on the blank layout
	    		text = (EditText) findViewById(R.id.ProfileName);
	    		//set the editText to NOT be seen either
	    		text.setVisibility(View.GONE);
	    		
	    		//create a new instance of the CoulerCs class which is a view and set it to the
	    		//test variable
	    		test = new ColouredCs(getApplicationContext(), details);
	    		//set some important values for the ColouredCs class, including the context
	    		//for functions being called from action bar items, the array of bitmaps that are
	    		//the eight C shapes, and the dimensions of the device screen
	    		test.setvalues(this, backgroundMasks, width, height);
	    		
	    		//check to see if the frameLayout variable isn't null
	    		if(frameLayout != null){
	    			//set the DynamicMenuItem which contains the various vision modes that can 
	    			//be simulated over the top of the Coloured Cs test to be invisible so 
	    			//that the use can only set a CVD to simualte from the activity main menu 
	    			//and not in the middle of the test
	    			mDynamicMenuItem.setVisible(false);
	    			//add the ColouredCs view test to the layout so that it can be rendered 
	    			//and seen by the user
	    		 	frameLayout.addView(test);
	    		 	//create a bitmap the same size as the device's screen
	    		 	Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	    		 	//create an unchangeable canvas from the bitmap
	    		 	final Canvas c = new Canvas(bitmap);
	    		 	//call the test's draw function passing through the canvas to be drawn on
	    		 	test.draw(c);
	    		 	
	    		}else{
	    			//display an error message
	        	    toast = Toast.makeText(this, "unable to draw circles", Toast.LENGTH_LONG);
	        	    toast.show();
	    			}
	    		}
	    	catch(ActivityNotFoundException anfe){
	    	    //display an error message
	    	    toast = Toast.makeText(this, "No such activity", Toast.LENGTH_SHORT);
	    	    toast.show();
	    	}
	    	}
		//check if the save profile button was clicked
		if (v.getId() == R.id.save_profile) { 
			//run the saveProfile function
			saveProfile();
	    	}
	}
	
	public void setup()
	{
		//get the instance of the parent activity CVDVisionActivity
		Activity parent = CVDVisionActivity.getInstance();
		//get the CVDdetails instance from the parent activity to be able to run CVD simulations
        details = ((CVDVisionActivity) parent).getCVDdetails();
        //get the Profiles instance from the parent activity
        myProfiles = ((CVDVisionActivity) parent).getProfiles();
        //get the list of profile names
        ProfileNames = myProfiles.retrieveAllProfileNames();
        //get the imageLoader instance created in the CVDdetails class
        imageLoader = details.getImageLoader();

		// load the background masks (rotated 'c' characters)
		backgroundMasks[TT_MASK] = imageLoader.loadImageSync(PATH + "tt.png");
		backgroundMasks[TR_MASK] = imageLoader.loadImageSync(PATH + "tr.png");
		backgroundMasks[RR_MASK] = imageLoader.loadImageSync(PATH + "rr.png");
		backgroundMasks[BR_MASK] = imageLoader.loadImageSync(PATH + "br.png");
		backgroundMasks[BB_MASK] = imageLoader.loadImageSync(PATH + "bb.png");
		backgroundMasks[BL_MASK] = imageLoader.loadImageSync(PATH + "bl.png");
		backgroundMasks[LL_MASK] = imageLoader.loadImageSync(PATH + "ll.png");
		backgroundMasks[TL_MASK] = imageLoader.loadImageSync(PATH + "tl.png");

		//set the simualted variable in the CVDdetails class to be false initially
		details.setSimulated(false);
		
		try {
        	//set a load of values in the imageLoader to be able to load the look-up-tables
			GenerateLUTs.setup(imageLoader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void end(){
		//set the CVDdetials simualted variable to be false in case a simulation of CVD was being
		//run over the test
		details.setSimulated(false);
		//create a new intent
		Intent intent = new Intent();
		//put the string value null under the name ProfileName so that the main activity will not
		//try to add a profile to the list of profiles
		//this is done since this function is only called if the user pressing the cross icon in
		//the action bar to stop the test before finishing
		intent.putExtra("ProfileName", "null");
		//set intent resultcode
		setResult(RESULT_OK, intent);
		//call finish to close the ColouredCActivity and return to the main menu activity
		finish();
	}
	
	public void saveProfile(){
		//set the test finished variable to be true since this function gets called if the user
		//presses the save profile button that is only made visible after the test has been finished
		test.setFinish(true);
		//retrieve the name entered into the editText field by the user before pressing the
		//save profile button
		name = test.getName();
		//initialise a count variable to 0
		int count = 0;
		//retrieve the trialData from the completed test
		trialData = test.getProfileData();
		//for each calibration trial in the trailData
		for(CalibrationTrial g : trialData){
			//increment the count variable
			count ++;
			//switch statement for each of the size calibration trials in the trial data
			switch(count){
				//set the variables to be the hue angle for each of the calibration trials
				case 1: protan1 = Double.parseDouble(g.toString().split("   ")[2]);
				case 2: tritan1 = Double.parseDouble(g.toString().split("   ")[2]);
				case 3: deutan1 = Double.parseDouble(g.toString().split("   ")[2]);
				case 4: protan2 = Double.parseDouble(g.toString().split("   ")[2]);
				case 5: tritan2 = Double.parseDouble(g.toString().split("   ")[2]);
				case 6: deutan2 = Double.parseDouble(g.toString().split("   ")[2]);
			}
		}
		//obtain the average protan, deutan and tritan results from the trial data
		protan = ((protan1+protan2)/2);
		deutan = ((deutan1+deutan2)/2);
		tritan = ((tritan1+tritan2)/2);
		//create a new intent
		Intent intent = new Intent();
		//add the name of the profile to the intent under the ProfileName
		intent.putExtra("ProfileName", name);
		//check if the average Protan is higher than the average Deutan and is higher than the
		//threshold value that indicates if the user has colour blindness
		if(protan > deutan && protan > threshold){
			//if it is then set the Protan to be true in the intent and the Deutan to be false
			//since a person can't be both Protan and Deutan
			intent.putExtra("Protan", true);
			intent.putExtra("Deutan", false);
			//set the severity of their Protan deficiency
			intent.putExtra("RGAmount", protan);
			message = "This profile has Protan colour blindness of strength " + protan;
		}else
		//else check if their average Deutan is higher than their average Protan and is higher
		//than the threshold value
		if(deutan > protan && deutan > threshold){
			//if it is then set the Deutan to be true in the intent and the Protan to be false
			intent.putExtra("Protan", false);
			intent.putExtra("Deutan", true);
			//set the severity of their Deutan deficiency
			intent.putExtra("RGAmount", deutan);
			message = "This profile has Deutan colour blindness of strength " + deutan;
		}else{
			//if they fail both checks the set both the Protan and Deutan to be false in the 
			//intent since the user isn't either a Protan or a Deutan
			intent.putExtra("Protan", false);
			intent.putExtra("Deutan", false);
			//set a 0 value since they arn't Protan or Deutan
			intent.putExtra("RGAmount", 0);
		}
		//check if the user has Tritan CVD
		if(tritan > threshold){
			//set Tritan to be true in the intent
			intent.putExtra("Tritan", true);
			//set the severity of Tritan the user has
			intent.putExtra("BYAmount", tritan);
			message = message + "\nThis profile has Tritan colour blindness of strength " + tritan;
		}else{
			//set Tritan to be false in the intent
			intent.putExtra("Tritan", false);
			//set a 0 value since the user isn't a tritan
			intent.putExtra("BYAmount", 0);
		}
		//set the resultcode for the intent
		setResult(RESULT_OK, intent);
		//check if a CVD simualtion was run and write out the user's reaction times, and the
		//value of their Protan, Deutan and Tritan to a txt file in the device's directory with
		//the look-up-tables and rotated Cs
		//this function is no longer needed since it was only to gather data on the participants as they completed the coloured C test 
		//saveData();
		//null the test
		test = null;
		//set the CVDdetails simulated variable to be false
		details.setSimulated(false);
		toast = Toast.makeText(this, message, Toast.LENGTH_LONG);
		toast.show();
		finish();
	}
	
	public void saveData(){
		//get the array of reaction times for each screen
		//reaction_times = test.getReactionTimes();
		//get the R,G and B values for each screen
		//Ccolours = test.getC_Colours();
		//get the number of screens skipped
		skipCount = test.getSkipCount();
		//the name of the file to write to
		String filename = "Data.txt";
		try {
			//create a filewriter and tell it to amend new text to the end of the file
			//rather than overwrite
			FileWriter filewriter = new FileWriter(Environment.getExternalStorageDirectory()
			+ "/CVDVisionFiles/LUT/" + filename, true);
		    BufferedWriter out = new BufferedWriter(filewriter);
		    //write data
		    if(!name.equals(null))
		    	out.write(name.toString());
		    out.write("'s reaction times were:\n");
		    for (int i = 0; i < 60; ++i) {
		    	if(reaction_times[i] != 0){
			    	out.write("time = " + reaction_times[i] + ", Red = ");
			    	out.write(Ccolours[i][0] + ", Green = " + Ccolours[i][1] + ", Blue = " 
			    	+ Ccolours[i][2] + "\n");
		    	}
			}
			out.write("number of skipped frames = ");
			out.write(String.valueOf(skipCount));
			out.write(", protan = ");
			out.write(String.valueOf(protan));
			out.write(", deutan = ");
			out.write(String.valueOf(deutan));
			out.write(", tritan = ");
			out.write(String.valueOf(tritan));
			out.write("\n\n");
			//close the writers
			out.close();
			filewriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void onBackPressed(){
		//if the back button is pressed during the test call the end function
		end();
	}
	
	public void onItemClick(MenuItem item) throws IOException {
		//if an item in the view mode drop down list is pressed
		//check the id of the item pressed
		if(item.getTitle().equals("Protan Sight")){
        	//check if the test is null since the CVD simulation cannot be pressed and ran
			//in the middle of the test
			if(test == null){
				//retrieve the string location of the Protan look-up-table and set it to the 
        		//CVDdetails LUT variable 
				details.setCVDType(details.getProtanLUT());
				//set the CVDdetails simulated variable to be true
		    	details.setSimulated(true);
			}
        }else
        if(item.getTitle().equals("Deuteran Sight")){
        	//check if the test is null since the CVD simulation cannot be pressed and ran
			//in the middle of the test
        	if(test == null){
        		//retrieve the string location of the Deutan look-up-table and set it to the 
        		//CVDdetails LUT variable 
	        	details.setCVDType(details.getDeutanLUT());
	        	//set the CVDdetails simulated variable to be true
	        	details.setSimulated(true);
        	}
        }else
        if(item.getTitle().equals("Tritan Sight")){
        	//check if the test is null since the CVD simulation cannot be pressed and ran
			//in the middle of the test
        	if(test == null){
        		//retrieve the string location of the Tritan look-up-table and set it to the 
        		//CVDdetails LUT variable 
        		details.setCVDType(details.getTritanLUT());
        		//set the CVDdetails simulated variable to be true
        	   	details.setSimulated(true);
        	}
        }
	}
	
	public void personalSimulation(String name, Boolean isGenerated){
		//get the profile data for the selected profile using the item title that was passed
		//though
		if(isGenerated == false){
	    	result = myProfiles.retrieveProfile(name).getData();
	    	//try to generate a personalised look-up-table using the profile data string dividing
	    	//by the whitespace that is set in-between each value
	    	//the first value is whether the profile has Protan CVD
	    	//the second value is whether the profile has Deutan CVD
	    	//the third value is whether the profile has Tritan CVD
	    	//the fourth value is the severity of the Red-Green deficiency
	    	//the fifth value is the severity of the Blue-Yellow deficiency
	    	try {
				GenerateLUTs.main(Boolean.parseBoolean(result.split(" ")[0]), 
						Boolean.parseBoolean(result.split(" ")[1]), 
						Boolean.parseBoolean(result.split(" ")[2]), 
						Double.parseDouble(result.split(" ")[3]),
						Double.parseDouble(result.split(" ")[4]));
			} catch (NumberFormatException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
    	//get the location of the personalised look-up-table and set it to the CVDdetails LUT 
    	//variable  	
    	details.setCVDType(details.getPersonalLUT());
    	//set the CVDdetails simulated variable to be true
    	details.setSimulated(true);
		details.setCurrentGenerated(name);
	}
	
	public void visible(){
		//check if the test has been initialised and is finsihed
		if(test != null && test.isDone() == true){
			//set the prompt message to be visible
			prompt.setVisibility(View.VISIBLE);
			//set the editText field to be visible to the user
			text.setVisibility(View.VISIBLE);
			//set the save profile button to be visible
			mProfileButton.setVisibility(View.VISIBLE);
			//listen for a click to the save profile button
			mProfileButton.setOnClickListener(this);
		}
	}
	
	public EditText getName(){
		//return the editText
		return text;
	}
	
}

