/*
Code property of Alistair Gilchrist
*/

package com.dundeeuni.Alistair.cvdvision;

/*
 * This is a class that will handle all of the variables necessary to run the CVD simulation.
 * This is because the CVD simulation is used in two different activities and will save memory
 * on the device and make the code more modular and organised.
 * A single instance of this class will be created in the main activity when the application is
 * started and will be passed through to both the coloured C activity and the real-time simulation
 * activity.
 */

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.support.v8.renderscript.*;
import android.util.Log;

public class CVDdetails {
	
	//variables for the class
	private Bitmap LUT;
	private RenderScript mRS;
	private Allocation mInAllocation;
    private Allocation mOutAllocation;
    private Allocation mLUTAllocation;
    private ScriptC_lut mLUTScript;
    private Bitmap mBitmapIn;
    private Bitmap mBitmapOut;
    private Mat matrix;
	
    //image loader class to load the Look-up-tables
	private ImageLoader imageLoader;
	//a context variable
	Context cont;
	
	//final and unchangeable strings for the locations of the four look-up-table images
	final private String Personal = "file:///mnt/sdcard/CVDVisionFiles/LUT/personal.png";
	final private String Protan = "file:///mnt/sdcard/CVDVisionFiles/LUT/RGB.small.protan.png";
	final private String Deuteran = "file:///mnt/sdcard/CVDVisionFiles/LUT/RGB.small.deutan.png";
	final private String Tritan = "file:///mnt/sdcard/CVDVisionFiles/LUT/RGB.small.tritan.png";
	private String current_LUT;
	//final and unchangeable image size since all of the look-up-tables are the same size and won't change
    final private ImageSize targetSize = new ImageSize(512, 512);
    
    //a boolean that says whether a simulation of CVD is currently being run or not 
    boolean simulated;
    
    private BaseLoaderCallback mOpenCVCallBack = new BaseLoaderCallback(cont) {
        @Override
        public void onManagerConnected(int status) {
          switch (status) {
              case LoaderCallbackInterface.SUCCESS:
              {
                 Log.i("CVDVision", "OpenCV loaded successfully");
                 matrix = new Mat();

              } break;


              default:
              {
                  super.onManagerConnected(status);
              } break;
            }
         }
    };
	
	public CVDdetails(Context context) {
		//set the context variable to be the context of the main activity to allow for the class
		//function to be used from action bar buttons
		cont = context;
		Log.i("CVDVision", "Trying to load OpenCV library");
        if (!OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, cont, mOpenCVCallBack))
        {
          Log.e("CVDVision", "Cannot connect to OpenCV Manager");
        }
        else{ Log.i("CVDVision", "opencv successfull");
        }
		//set up a configuration for the image loader to work
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context)
        .build();
        //configure an instance of ImageLoader
        ImageLoader.getInstance().init(config);
        //create an instance of ImageLoader
        imageLoader = ImageLoader.getInstance();
        //set the LUT bitmap to initially be the Protan look-up-table
		LUT = imageLoader.loadImageSync(Protan, targetSize);
		//set simulated to be false since the application will not initially be simulating CVD
		simulated = false;
	}
	
	//set up a render script to handle the simulation based on a matrix that represents the
	//device's screen (this will be used in the real-time simulation activity which used openCV
	//which represents the camera view data on the device's screen as a matrix.
	public void createSimulationScriptMat(Mat theScreen)
    {
		//create a render script and set it to the mRS variable
    	mRS = RenderScript.create(cont);
    	//run the createBitmapsMat function passing through the screen data
    	createBitmaps(theScreen);
    	//set the InAllocation to pass through screen data bitmaps to the render script
    	mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
    	//set the OutAllocation to retrieve the transformed screen data from the render script
    	mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());
    	//set the LUTAllocation to pass through look-up-table bitmaps to the render script
    	mLUTAllocation = Allocation.createFromBitmap(mRS, LUT, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
    	//create an instance of the render script and set it to the script variable
    	mLUTScript = new ScriptC_lut(mRS);
    	//set the script's initaial LUTAllocation
    	mLUTScript.set_lut(mLUTAllocation);
    }
	
	private void createBitmaps(Mat theScreen) {
		//create the BitmapIn variable for passing through screen data to the render script
		mBitmapIn = Bitmap.createBitmap(theScreen.width(), theScreen.height(), Config.ARGB_8888);
		//create the BitmapOut variable for retrieving the transformed screen data from the render script
    	mBitmapOut = Bitmap.createBitmap(theScreen.width(), theScreen.height(), Config.ARGB_8888);
    	//transform the current screen data from a matrix to a bitmap and set it to the BitmapIn varaible
    	Utils.matToBitmap(theScreen, mBitmapIn);
	}

//set up a render script to handle the simulation based on a bitmap that represents the
//device's screen (this will be used in the coloured C activity which used a bitmap to
//represent the canvas that the C's will be drawn on to be displayed to the user.
	public void createSimulationScriptBitmap(Bitmap theScreen)
    {
		//create a render script and set it to the mRS variable
    	mRS = RenderScript.create(cont);
    	//transform the screen bitmap to a matrix to be able to pass through to the createBitmaps function
    	Utils.bitmapToMat(theScreen, matrix);
    	//run the createBitmapsMat function passing through the screen data
    	createBitmaps(matrix);
    	//set the InAllocation to pass through screen data bitmaps to the render script
    	mInAllocation = Allocation.createFromBitmap(mRS, mBitmapIn,
                Allocation.MipmapControl.MIPMAP_NONE,
                Allocation.USAGE_SCRIPT);
    	//set the OutAllocation to retrieve the transformed screen data from the render script
    	mOutAllocation = Allocation.createTyped(mRS, mInAllocation.getType());
    	//set the LUTAllocation to pass through look-up-table bitmaps to the render script
    	mLUTAllocation = Allocation.createFromBitmap(mRS, LUT, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
    	//create an instance of the render script and set it to the script variable
    	mLUTScript = new ScriptC_lut(mRS);
    	//set the script's initaial LUTAllocation
    	mLUTScript.set_lut(mLUTAllocation);
    }
	
	public void matrixToBitmap(Mat screenData){
		//tranform the matrix screen data to a Bitmap and set it to the BitmapIn variable
		Utils.matToBitmap(screenData, mBitmapIn);
	}
	
	public void setScreenBitmap(Bitmap screen){
		//set the BitmapIn variable to be the current screen bitmap
		mBitmapIn = screen;
	}
	
	public void runSimulationScript()
    {
		//copy the current BitmapIn variable to the InAllocation variable
    	mInAllocation.copyFrom(mBitmapIn);
    	//run the render script passing through the InAllocation as the image to be transformed
    	//and set the result to the OutAllocation variable
    	mLUTScript.forEach_invert(mInAllocation, mOutAllocation);
    	//copy to OutAllocation to the BitmapOut variable
    	mOutAllocation.copyTo(mBitmapOut);
    }
	
	public Bitmap getScriptOutput(){
		//return the BitmapOut variable
		return mBitmapOut;
	}
	
	public String getPersonalLUT(){
		//return the string location of the personal look-up-table image
		return Personal;
	}
	
	public String getProtanLUT(){
		//return the string location of the Protan look-up-table image
		return Protan;
	}
	
	public String getDeutanLUT(){
		//return the string location of the Deutan look-up-table image
		return Deuteran;
	}
	
	public String getTritanLUT(){
		//return the string location of the Tritan look-up-table image
		return Tritan;
	}
	
	public ImageLoader getImageLoader(){
		//return the image loader 
		return imageLoader;
	}
	
	public void setCVDType(String location){
		//load the look-up-table image found at the specified location into the LUT variable
		LUT = imageLoader.loadImageSync(location, targetSize);
	}
	
	public void setScriptLUT(){
		//set the LUTAllocation variable for the render script to be the current LUT variable
		mLUTAllocation.copyFrom(LUT);
        //set the new LUT to the render script
        mLUTScript.set_lut(mLUTAllocation);
	}
	
	public void setSimulated(boolean state){
		//set whether a simulation of CVD is being run or not
		simulated = state;
	}
	
	public boolean isSimulated(){
		//return whether a simulation of CVD is being run or not
		return simulated;
	}
	
	public void setCurrentGenerated(String name){
		current_LUT = name;
	}
	
	public String getCurrentGenerated(){
		return current_LUT;
	}

}
