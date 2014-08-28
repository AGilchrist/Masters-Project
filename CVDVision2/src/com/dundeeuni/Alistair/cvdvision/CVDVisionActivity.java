package com.dundeeuni.Alistair.cvdvision;

/*
 * The main activity of the application. Is the first thing to be called when the application
 * is started
 */

import java.util.Set;

import com.dundeeuni.Alistair.colouredCs.ColouredCActivity;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class CVDVisionActivity extends Activity implements OnClickListener {
	
	//some variables
	static final int REQUEST_VIDEO_CAPTURE = 1;
	
	private static Activity activity;
	private static CVDdetails details;
	private Profiles myProfiles = new Profiles();
	
	private Set<String> ProfileNames;
	
	private MenuItem mDynamicMenuItem;
	private Intent intent, infointent, calibrateintent;
    private Button captureBtn;
    
    private String value, name, key, result;
    private int count = 0, limit;
    private double RGAmount, BYAmount;
    private Boolean protan, deutan, tritan, quit = false, hasquit = false;
    
    private Toast toast;
    
    private static final String TAG = "CVDVision";
    
    SharedPreferences settings;
    SharedPreferences.Editor editor;

   	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//set the context activity to be this
		activity = this;
		//set intents for starting the three activities openCV for the real-time simultaions
		//ColourCActivity for the coloured C test and the User Guide
		intent = new Intent(this, OpenCV.class);
		calibrateintent = new Intent(this, ColouredCActivity.class);
		infointent = new Intent(this, UserGuide.class);
		//create an instance of CVDdetails
	    details = new CVDdetails(this);
		//set the main menu layout to render to the screen
		setContentView(R.layout.activity_cvdvision);		
		//create a button object that is defined in the layout xml file
		captureBtn = (Button)findViewById(R.id.start_btn);
		//set a listener to listen for a click
		captureBtn.setOnClickListener(this);
		//set a setting from the shared preferences to restore profiles if restarting the app
		settings = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		//editor to save profile details in the shared preferences
		editor = settings.edit();
	}
   	
   	public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        count = myProfiles.getProfileNumber();
        if(count > 0){
	        try {
	        	//set a base of 0 since their are only Profile names to be added to the menu
	            int base = 0;
	            //for each name in the lsit of profile names
	            for(String name : ProfileNames){
	            	//add a whitespace and the word Sight to the end
	                String id = name + " Sight";
	                //add an item to the menu with ItemID and order of base and id for a title
	                mDynamicMenuItem.getSubMenu().add(0, base, base, id);
	                //increment the base
	                base++;
	            }
	        } catch (Exception e) {
	            Log.e(TAG, "Error adding profile to menu", e);
	        }
        }
        //check if there is at least one profile
        if(limit > 0)
        	//set the menu to be visible
        	mDynamicMenuItem.setVisible(true);
        else
        	//set the menu to be invisible since no profiles exist at the moment
        	mDynamicMenuItem.setVisible(false);
        return true;
    }

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.cvdvision, menu);
		//add to the action-bar a new sub-menu
		menu.add(0, Menu.FIRST, 0, "Sight Mode").setIcon(R.drawable.ic_action_mode);
		//get the profile menu from the layout XML file and set it to the DynameicMenuItem vairable
		mDynamicMenuItem = menu.findItem(R.id.Profile_Menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		//get the ItemID of the item clicked
		int id = item.getItemId();
		//get the title of the item clicked and covert it to a string
    	String title = item.getTitle().toString();
    	//if the calibrate item was pressed
    	if(id == R.id.Calibrate){
    		//run the calibration function
			Calibration();
			return true;
		}else
		//if the info item was pressed
		if(id == R.id.Info){
			//run the information function
			Information();
            return true;
		}else
		//if the stop item was pressed
		if(id == R.id.Stop){
			//run the quiting function
			quiting();
            return true;
		}else
		//check if the ItemID is between 0 and the limit for items in the sub-menu
		if(id >= 0 && id < limit){
			//subtract 6 from the title to remove the whitespace and the word Sight to avoid
			//null pointer crash
			title = title.substring(0, title.length()-6);
			//delete the specified profile from the sorted map of profiles
    		myProfiles.deleteProfile(title);
    		//decrement limit since there is 1 less profile
    		limit --;
    		editor.clear();
    		editor.commit();
    		ProfileNames = myProfiles.retrieveAllProfileNames();
    		count = 0;
    		for(String name : ProfileNames){
    			count ++;
    			result = myProfiles.retrieveProfile(name).getData();
    			storeProfiles(count, name, Boolean.parseBoolean(result.split(" ")[0]), 
    			Boolean.parseBoolean(result.split(" ")[1]), Boolean.parseBoolean(result.split(" ")[2]), 
    			Double.parseDouble(result.split(" ")[3]), Double.parseDouble(result.split(" ")[4]));
    		}
    		//call this to recall the onPrepareOptionsMenu function since to make the deleted
    		//profile disappear from the sub-menu
    		invalidateOptionsMenu();
    	    return true;
    	    }
        return super.onOptionsItemSelected(item);
    }

	@Override
	//if a button in the main menu view is registered as being clicked
	public void onClick(View v) {
		//check if the id of the clicked button matches the start button
	    if (v.getId() == R.id.start_btn) { 
	    	try {
	    		//start the OpenCV activity using the intent intent
	    		startActivity(intent);
	    	}
	    	catch(ActivityNotFoundException anfe){
	    	    //display an error message if the application fails for some reason fails
	    	    String errorMessage = "It seems your device doesn't support video, "
	    	    + "How did you download this application!";
	    	    toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
	    	    toast.show();
	    		}
	    	}
	}
	
	private void Calibration() {
		try {
			//set a requestCode
			int requestCode = 1;
			//start the ColouredCs activity and await a result which will contain the name of 
			//the profile created and whether it is Protan, Deutan, Tritan, the Red-Green 
			//severity and the Blue-Yellow severity
			startActivityForResult(calibrateintent, requestCode);
		}
		catch(ActivityNotFoundException anfe){
    	    //display an error message if the application fails for some reason fails
    	    String errorMessage = "Something bad happened";
    	    toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
    	    toast.show();
    	}
	}
	
	private void Information(){
		try {
			//start the User Guide activity
    		startActivity(infointent);
    	}
    	catch(ActivityNotFoundException anfe){
    	    //display an error message if the application fails for some reason fails
    	    String errorMessage = "Something bad happened";
    	    toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
    	    toast.show();
    		}
	}
	
	@Override
	protected void onActivityResult (int requestCode, int resultCode, Intent data) {
		//check if the data isn't null
		if(data != null){
			//retrieve the various bits of data attached to the intent and remove them
			//just in case
			value = data.getStringExtra("ProfileName");
			data.removeExtra("ProfileName");
			protan = data.getBooleanExtra("Protan", false);
			data.removeExtra("Protan");
			deutan = data.getBooleanExtra("Deutan", false);
			data.removeExtra("Deutan");
			tritan = data.getBooleanExtra("Tritan", false);
			data.removeExtra("Tritan");
			RGAmount = data.getDoubleExtra("RGAmount", 0);
			data.removeExtra("RGAmount");
			BYAmount = data.getDoubleExtra("BYAmount", 0);
			data.removeExtra("BYAmount");
			//if the value variable isn't equal to the string null then there is a profile
			if(!value.equals("null")){
				//create a profile using the retrieved variables
				myProfiles.createProfile(value, protan, deutan, tritan, RGAmount, BYAmount);
				//set a count
				count = myProfiles.getProfileNumber();
				//save the profile
				storeProfiles(count, value, protan, deutan, tritan, RGAmount, BYAmount);
				//retrieve all profile names after adding a new profile
				ProfileNames = myProfiles.retrieveAllProfileNames();
				//increment limit
				limit ++;
				//call this to redraw the action-bar sub-menu since a new profile was created
				invalidateOptionsMenu();
			}
		}
	}
	
	private void storeProfiles(int count, String value, Boolean protan, Boolean deutan, Boolean tritan, Double RGAmount, Double BYAmount){
		key = "name"+count;					  
        editor.putString(key, value);
		key = "protan"+count;
		editor.putBoolean(key, protan);
		key = "deutan"+count;
		editor.putBoolean(key, deutan);
		key = "tritan"+count;
		editor.putBoolean(key, tritan);
		key = "RGAmount"+count;
		editor.putString(key, Double.toString(RGAmount));
		key = "BYAmount"+count;
		editor.putString(key, Double.toString(BYAmount));
		//add to the editor the total number of profiles
		editor.putInt("profilecount", count);
		//commit all values added to the editor
		editor.commit();
	}
	
	@Override
	public void onResume(){
		super.onResume();
		//get the number of profiles stored in the shared preferences settings
		count = settings.getInt("profilecount", 0);
			//check if there is at least one profile stored in the shared preferences
			if(count != 0){
				//for each profile
				for(int i = 1; i < count+1; i++){
					//set the key and retrieve the variable from the preferences
					key = "name"+i;
					name = settings.getString(key, "");
					key = "protan"+i;
					protan = settings.getBoolean(key, false);
					key = "deutan"+i;
					deutan = settings.getBoolean(key, false);
					key = "tritan"+i;
					tritan = settings.getBoolean(key, false);
					key = "RGAmount"+i;
					//need to parse the RGAmount and BYAmount from strings to doubles as doubles
					//cannot be stored in the preferences so the values had to be stored as 
					//strings
					RGAmount = Double.parseDouble(settings.getString(key, ""));
					key = "BYAmount"+i;
					BYAmount = Double.parseDouble(settings.getString(key, ""));
					//check if the name was successfully retrieved
					if(!name.equals("")){
						//recreate the profile and add it to the sorted map of profiles
						myProfiles.createProfile(name, protan, deutan, tritan, RGAmount, BYAmount);
					}
				}
			}
		//retrieve the set of profile names
		ProfileNames = myProfiles.retrieveAllProfileNames();
        limit = myProfiles.getProfileNumber();
	}
	
	@Override
	public void onBackPressed(){
		//run the quiting function
		quiting();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		//check if the home button was pressed
	    if ((keyCode == KeyEvent.KEYCODE_HOME)) {
	    	//run the quiting function
	    	quiting();                   
	        return true;
	    }
	    return super.onKeyDown(keyCode, event);
	}
	
	public void quiting(){
		//set quit to be true
		quit = true;
		//close the activity
		finish();
	}
	
	public Profiles getProfiles(){
		return myProfiles;
	}
	
	public CVDdetails getCVDdetails(){
		return details;
	}
	
	public static Activity getInstance()
	{
	return activity;
	}
	
}
