package com.dundeeuni.Alistair.cvdvision;

/*
 * This is the class file for capturing video feed from the device's camera and running a CVD
 * simulation on top. 
*/

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.Utils;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import com.dundeeuni.Alistair.LUTgenerator.GenerateLUTs;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.widget.Toast;

public class OpenCV extends Activity implements CvCameraViewListener2 {
	
	//variables
    private CameraBridgeViewBase OpenCvCamera;
    private Profiles myProfiles;
    private CVDdetails details;
    
    private ImageLoader imageLoader;
    private MenuItem mDynamicMenuItem;
    private File fileDirectory;
    private Toast toast;
    
    private Bitmap Screen;
    private Mat mRGBA, originalRGBA;
    
    private int View_Mode;
    private Set<String> ProfileNames;
    private String result, pictureID;
    private boolean hasCameraStarted = false, takepicture = false, coloured = true;
    private int limit;

    private static final String TAG = "CVDVision";
    
    public OpenCV() {
        
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //ensures that the android device does not go to sleep while the application is running
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		//set the display to a blank layout so that the camera feed covers the device's entire
		//screen
		setContentView(R.layout.opencvcamera);
		//set the OpenCvCamera to the selected view so that the object isn't null
		OpenCvCamera = (CameraBridgeViewBase) findViewById(R.id.OpenCV_Cameraview);
		//set the openCvCamera object to be visible to the user
		OpenCvCamera.setVisibility(SurfaceView.VISIBLE);
		//set a listener for the OpenCvCamera object
        OpenCvCamera.setCvCameraViewListener(this);
        //set the view mode to be normal camera view
        View_Mode = 0;
        //get the instance of the parent activity CVDVisionActivity which is the main menu
        Activity parent = CVDVisionActivity.getInstance();
        //get the colour blindness profiles from the parent activity
        myProfiles = ((CVDVisionActivity) parent).getProfiles();
        //get the CVDdetails class defined in the parent activity as it is needed to run real-time
        //simulations of CVD
        details = ((CVDVisionActivity) parent).getCVDdetails();
        //get the imageLoader defined in the CVDdetails class
        imageLoader = details.getImageLoader();
        //get the list of profile names
        ProfileNames = myProfiles.retrieveAllProfileNames();
        //set a limit for item IDs for registering clicks to profile items
        limit = myProfiles.getProfileNumber()+5;
        
        try {
        	//set a load of values in the imageLoader to be able to load the look-up-tables
			GenerateLUTs.setup(imageLoader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }


	@Override
    public void onPause()
    {
        super.onPause();
        //check that the camera exists
        if (OpenCvCamera != null)
        	//shut down and release camera
            OpenCvCamera.disableView();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_9, this, OpenCvLoader);
    }

    public void onDestroy() {
        super.onDestroy();
        //repeated just in case the application crashes
        if (OpenCvCamera != null)
            OpenCvCamera.disableView();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        try {
        	//set a base ID for menu items
            int base = 5;
            //for each name in the list of profile names
            for(String name : ProfileNames){
            	//add a whitespace and the word Sight to the end of its name
                String id = name + " Sight";
                //add an item to the Mode_menu sub-menu with an itemID and an order number
                //starting at 5 since there are four items in the menu to begin with and the id
                //variable as its title
                mDynamicMenuItem.getSubMenu().add(0, base, base, id);
                base++;
            }
        } catch (Exception e) {
            Log.e(TAG, "Problem adding item to menu.", e);
        }
        //ensure that the altering menu is visible to user
        mDynamicMenuItem.setVisible(true);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	//add menu items from the opencv_camera menu layout XML file
    	MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.open_cvcamera, menu);
        menu.add(0, Menu.FIRST, 0, "Sight Mode").setIcon(R.drawable.ic_action_mode);
        //grab the Mode_menu item from the XML file to make it possible to dynamically add
        //profile items to it in the onPrepareOptionsMenu function
        mDynamicMenuItem = menu.findItem(R.id.Mode_Menu);
		return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	// get the id of the item clicked
    	int id = item.getItemId();
    	//get the title of the item clicked
    	String title = item.getTitle().toString();
    	//if the camera item was clicked
    	if (id == R.id.Snapshot) {
    		//set the takepicture variable to be true
    		takepicture = true;
			return true;
		}else
		//if the stop item was clicked
		if(id == R.id.Stop){
			//run the stopcamera function
			stopcamera();
	        return true;
		}else
		//if the Protan Sight item was clicked
		if(id == R.id.Protan_Mode){
			//check if the application is already running the Protan simulation
			if(View_Mode != 2){
		        //set the look-up-table that the render script uses to be the standard Protan LUT
		        details.setCVDType(details.getProtanLUT());
		        //set the render script look-up-table
		        details.setScriptLUT();
		        //set the view_mode to be 2 so that the program knows that its is running a
		        //Protan simulation
		        View_Mode = 2;
        	}
			return true;
		}else
		//same for Deutan
		if(id == R.id.Deuteran_Mode){
			if(View_Mode != 3){
        		details.setCVDType(details.getDeutanLUT());
        		details.setScriptLUT();
		        View_Mode = 3;
        	}
			return true;
		}else
		//same for Tritan
		if(id == R.id.Tritan_Mode){
			if(View_Mode != 4){
        		details.setCVDType(details.getTritanLUT());
        		details.setScriptLUT();
		        View_Mode = 4;
        	}
			return true;
		}else
		//same for monochromatic
		if(id == R.id.Monochromatic){
			if(View_Mode != 5){
        		View_Mode = 5;
        	}
			return true;
		}else
		//check if the id is between 5 the lowest ID possible for personal profile items
		//and the limit which is always 1 more than the ID of the last personal profile item
    	if(id >= 5 && id < limit){
    		//subtract 6 characters from the title to remove the whitespace and the word Sight
    		//from the title to avoid null pointer error
    		title = title.substring(0, title.length()-6);
       		if(title.equals(details.getCurrentGenerated()))
    		//run the personal simulation function using this personal profile
    			personalSimulation(title, true);
    		else
    			personalSimulation(title, false);
	    	return true;
	    }else{
	    	return super.onOptionsItemSelected(item);
	    }
    }


	@Override
	public void onCameraViewStarted(int width, int height) {
		hasCameraStarted = true;
	}

	@Override
	public void onCameraViewStopped() {
		hasCameraStarted = false;
	}

	@Override
	public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
		//grab the rgba data for the frame
		mRGBA = inputFrame.rgba();
		originalRGBA = mRGBA.clone();
		//check if the camera has just been started
		if(hasCameraStarted)
		{
			//create the render script that will run the real-time simulation
			details.createSimulationScriptMat(mRGBA);
			//set the hasCameraStarted to false since we only need to create the render script
			//once
			hasCameraStarted = false;
		}
		
		//check if the view_mode is between 1 and 4 for Personal, Protan, Deutan, or Tritan 
		//respectively
		if(View_Mode > 0 && View_Mode < 5){
			if(hasCameraStarted == false){
				//transform the matrix of the screen data to a bitmap iamge
				details.matrixToBitmap(mRGBA);
				//run the simulation script using the currently LUT
				details.runSimulationScript();
				//get the output from the render script
				Screen = details.getScriptOutput();
				//convert it back to a matrix since openCV needs a matrix to return to be 
				//rendered to the device's screen
				Utils.bitmapToMat(Screen, mRGBA);
				coloured = true;
			}
		}else 
		//check if view_mode is 5 for Monochromatic
		if(View_Mode == 5)
		{
			//set the mRGBA variable to be the greyscale of the current frame
			mRGBA = inputFrame.gray();
			coloured = false;
		}
		
		//if the takepicture variable is true
		if(takepicture)
			//run the takepicture function
			takeapicture(mRGBA, originalRGBA, coloured);
		
		//return the curretn mRGBA matrix to be rendered to the screen
		return mRGBA;
	}
	
	@Override
	public void onBackPressed(){
		//run the stopcamera function
		stopcamera();
	}
  
	
	private BaseLoaderCallback OpenCvLoader = new BaseLoaderCallback(this) {
       @Override
       public void onManagerConnected(int status) {
           switch (status) {
	           case LoaderCallbackInterface.SUCCESS:
	           {
	        	   //enable camera feed
	               OpenCvCamera.enableView();
           } break;
               default:
               {
                   super.onManagerConnected(status);
               } break;
           }
       }
   };
    
    private void stopcamera(){
		//check if the OpenCvCamera object exists to avoid crashing the application 
		//if the stopcamera button is pressed from the main menu screen
		if (OpenCvCamera != null){
			//disable the OpenCvCamera to stop camera feed
			OpenCvCamera.disableView();
			//end the current activity and return to the CVDVisionActivity which is the main menu
			finish();
		}
	}
    
    @SuppressLint("SimpleDateFormat")
	private void takeapicture(Mat data, Mat originalData, Boolean RGB)
    {
    	//check if the screen data is in RGBA format or grayscale
    	if(RGB){
    		//transform the data from BGR (the actual format that openCV store colour data in)
    		// to rgba
    		Imgproc.cvtColor(data, data, Imgproc.COLOR_BGR2RGB);
    		Imgproc.cvtColor(originalData, originalData, Imgproc.COLOR_BGR2RGB);
    	}
    	else{
    		//transform grayscale to rgb data
    		Imgproc.cvtColor(data, data, Imgproc.COLOR_GRAY2RGB);
    		Imgproc.cvtColor(originalData, originalData, Imgproc.COLOR_BGR2RGB);
    	}
    	//create a new simple date format
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        //use the current data and time
        String currentDateandTime = sdf.format(new Date());
        //check if the extrernal SD card is inserted
        if( android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED))
        {
        	//the file directory for CVDVision pictures
	        fileDirectory = new File(Environment.getExternalStorageDirectory().getPath() + "/CVDVisionFiles/Pictures/");
	        //ensure that the directory exists
	        fileDirectory.mkdirs();
	        //set the pictures title to be the current data and time
	        if(View_Mode == 1)
	        	pictureID = currentDateandTime + "_Personal";
	        else if(View_Mode == 2)
	        	pictureID = currentDateandTime + "_Protan";
	        else if(View_Mode == 3)
	        	pictureID = currentDateandTime + "_Deutan";
	        else if(View_Mode == 4)
	        	pictureID = currentDateandTime + "_Tritan";
	        else if(View_Mode == 5)
	        	pictureID = currentDateandTime + "_Monochromatic";
	        else
	        	pictureID = currentDateandTime;
	        //save the picture into the selected directory
	        Highgui.imwrite(Environment.getExternalStorageDirectory().getPath() + "/CVDVisionFiles/Pictures/" + pictureID + ".jpg", data);
	        Highgui.imwrite(Environment.getExternalStorageDirectory().getPath() + "/CVDVisionFiles/Pictures/" + pictureID + "_original" + ".jpg", originalData);
        }else
        {
        	//display error message if something went wrong
        	runOnUiThread(new Runnable() {
        	    public void run() {
                	toast = Toast.makeText(getApplicationContext(), 
                	"You either don't have an SDCard inserted or there is too little " +
                	"memory available to save the image!", Toast.LENGTH_LONG);
                	toast.show();
        	    }
        	});        	
        }
        //set takepicture to false so that only one picture is saved
	    takepicture = false;
    }
    
	public void personalSimulation(String name, Boolean isGenerated){
		//get the data for the selected profile
		if(isGenerated == false){
	    	result = myProfiles.retrieveProfile(name).getData();
	    	try {
	    		//generate a personal LUT based on the data retrieved
				GenerateLUTs.main(Boolean.parseBoolean(result.split(" ")[0]), 
						Boolean.parseBoolean(result.split(" ")[1]), 
						Boolean.parseBoolean(result.split(" ")[2]), 
						Double.parseDouble(result.split(" ")[3]),
						Double.parseDouble(result.split(" ")[4]));
			} catch (NumberFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
    	//set LUT used by the render script to be the newly created personal LUT
    	details.setCVDType(details.getPersonalLUT());
    	//set the LUT for the render script to use
    	details.setScriptLUT();
    	details.setCurrentGenerated(name);
    	//set the view_mode to be 1 for personal simulation
	    View_Mode = 1;
	}
}
