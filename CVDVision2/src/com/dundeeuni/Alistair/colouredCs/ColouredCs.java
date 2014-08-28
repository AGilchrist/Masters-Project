package com.dundeeuni.Alistair.colouredCs;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.opencv.core.Mat;

import com.dundeeuni.Alistair.LUTgenerator.ColorConverter;
import com.dundeeuni.Alistair.LUTgenerator.GenerateLUTs;
import com.dundeeuni.Alistair.cvdvision.CVDdetails;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

public class ColouredCs extends View implements CalibrationConstants, OnTouchListener
{
	//variables
	private ArrayList<CalibrationTrial> testingTrials = new ArrayList<CalibrationTrial>();
	private ArrayList<CalibrationTrial> finishedTrials = new ArrayList<CalibrationTrial>();
	private CVDdetails details;
	private ColouredCActivity act;
	private CalibrationTrial ct;
	private Random rand = new Random();
	private Paint paint = new Paint();
	
	private Canvas c = new Canvas();
	private ImageLoader imageLoader;
	private Context cont;
	private EditText yourEditText;
	private Mat matrix;
	
	private Bitmap[] backgroundMasks = new Bitmap[8];
	private Bitmap Screen;
	private Bitmap myBitmap;
	private int trialIndex, maskIndex = (int) rand.nextInt(backgroundMasks.length), 
			width, height, screenCount = 0, skipCount = 0;
	private long stateChangedTime, touchTime, reactionTime;
	//these variables were used to gather data on the participants as they performed the tests
	//it is useless after the experiments are finished
	//private long[] reaction_times = new long[60];
	//private int[][] Ccolours = new int[60][3];
	private boolean simulated, finished = false;
	
	public ColouredCs(Context context, CVDdetails det)
	{
		super(context);
		//set the context of the main menu activity
		cont = context;
		//set the CVDdetails from the main menu for use in simulating CVD
		details = det;
		//set the onTouchListener to register touches to the screen
		this.setOnTouchListener(this);
		imageLoader = details.getImageLoader();
		try {
			//setup some variables in the gernateLUTs class
			GenerateLUTs.setup(imageLoader);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void setvalues(ColouredCActivity a, Bitmap[] bitmaps, int w, int h)
	{
		//for each rotated C in the bitmap array
		for(int i=0; i<bitmaps.length; i++){
			//scale the bitmap to fit the screen
			backgroundMasks[i] = Bitmap.createScaledBitmap(bitmaps[i], w, h, false);
		} 
		//set the activity of the parent activity
		act = a;
		//get the editText from the parent actiivty
		yourEditText = act.getName();
		//set the width and height of the device's screen
		width = w;
		height = h;
		stateChangedTime = System.currentTimeMillis();
		matrix = new Mat();
		try {
			//generate calibration trials
			generateCalibrationTrials();
		} catch (IOException ioe) {
			System.err.println("Could not generate calibration trials.");
			ioe.printStackTrace(System.err);
			System.exit(1);
		}
		trialIndex = 0;
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		//get a random number between 0 and the number of rotated Cs in the bitmap array 
		maskIndex = (int) rand.nextInt(backgroundMasks.length);
		//set whether a CVD simulation is being run  
		simulated = details.isSimulated();
		//if it is
		if(simulated == true){
			//create a bitmap the same size as the canvas
			myBitmap = Bitmap.createBitmap(canvas.getWidth(), canvas.getHeight(), Config.ARGB_8888);
			//create a render script based on this bitmap
			details.createSimulationScriptBitmap(myBitmap);
			//set the render script look-up-table to be the personal
			details.setScriptLUT();
			//set the bitmap to the canvas so it will be drawn on instead of the canvas
			c.setBitmap(myBitmap);
		}
		//check if the testingTrials is finished yet
			if (!testingTrials.isEmpty()){
				//get the next trialIndex
				ct = testingTrials.get(trialIndex);
				//get the arrays that represent the colours of the background and the C
				Integer[] backgroundNoise = testingTrials.get(trialIndex).fullWalkingColors.get(0);
				Integer[] foregroundNoise = testingTrials.get(trialIndex).fullWalkingColors.get(testingTrials.get(trialIndex).getCurrentIndex());
				int index;
				//iterate down the screen from the top of the canvas to the bottom			
				for (int y=0; y<height; y=y+SIZE+GAP)
				{
					//iterate accross the canvas from the left side to the right
					for (int x=0; x<width; x=x+SIZE+GAP)
					{
						//set a rectF object that will be an oval shape
						RectF oval = new RectF();
						//define its coordinates
						oval.set(x, y, (x+SIZE), (y+SIZE));
						//check if the pixel colour at this location on the selected rotated C
						//image isn't white
						if(backgroundMasks[maskIndex].getPixel(x, y) < 0xFFFFFFFF){
							//select a random colour from the foreground colour arrays
							index = (int) rand.nextInt(foregroundNoise.length);
							//unpackage the colour into its own array
							int[] RGB = ColorConverter.unpackageIntRGB(foregroundNoise[index]);
							//set the paint colour to be this with full alpha 
							paint.setARGB(255, RGB[0], RGB[1], RGB[2]);
							//Ccolours[screenCount][0] = RGB[0];
							//Ccolours[screenCount][1] = RGB[1];
							//Ccolours[screenCount][2] = RGB[2];
						}else{
							//select a random colour from the background colour array
							index = (int) rand.nextInt(backgroundNoise.length);
							//unpackage the colour into its own array
							int[] RGB = ColorConverter.unpackageIntRGB(backgroundNoise[index]);
							//set the paint colour to be this with full alpha
							paint.setARGB(255, RGB[0], RGB[1], RGB[2]);
						}
						//set the style to be fill so oval won't be hollow
						paint.setStyle(Style.FILL);
						//check if CVD is being simulated
						if(simulated)
							//draw to the bitmap mounted to canvas c
							c.drawOval(oval, paint);
						else
							//draw to the standard canvas
							canvas.drawOval(oval, paint);
					}
				}
				
				//set paint style to stroke for drawing lines
				paint.setStyle(Style.STROKE);
				//set the colour to be full white
				paint.setColor(Color.WHITE);
				
				//check if CVD is being simualted
					if(simulated){
						//draw all lines onto the bitmap mounted on canvas c
						c.drawLine((width/3), 0, (width/3), height, paint);
						c.drawLine((2*(width/3)), 0, (2*(width/3)), height, paint);
						c.drawLine(0, (height/3), width, (height/3), paint);
						c.drawLine(0, (2*(height/3)), width, (2*(height/3)), paint);
					}
					else{
						//draw lines to the standard canvas
						canvas.drawLine((width/3), 0, (width/3), height, paint);
						canvas.drawLine((2*(width/3)), 0, (2*(width/3)), height, paint);
						canvas.drawLine(0, (height/3), width, (height/3), paint);
						canvas.drawLine(0, (2*(height/3)), width, (2*(height/3)), paint);
					}
				
				//check if CVD is being simulated
				if(simulated == true){
					//set CVDdetails bitmap to transformed by the render script
					details.setScreenBitmap(myBitmap);
					//run the renderscript
					details.runSimulationScript();
					//retrieve the transformed bitmap
					Screen = details.getScriptOutput();
					//draw bitmap onto the canvas
					canvas.drawBitmap(Screen, null, new Rect(0,0,Screen.getWidth(),Screen.getHeight()), null);
				}
				
			}else{
				//run exittest function
				exittest();
			}
		}
	// Called repeatedly to draw to the screen.

	@Override
	public boolean onTouch(View screen, MotionEvent event) {
		//if the test has finished
		if(finished == true)
			//remove focus from the editText
			setEditTextFocus(false);
		//get the current time is milliseconds
		touchTime = System.currentTimeMillis();
		//calculate the reaction time
		reactionTime = touchTime - stateChangedTime;
		//set the entry in the reaction_times array to be equal to this reaction time
		//reaction_times[screenCount] = reactionTime;
		//increment the screenCount
		screenCount++;
		//get the x and y coordinates of the position that was touched
		float x = event.getRawX();
		float y = event.getRawY();
		//check which of the nine sections of the screen the position touched was in 
		if(x < (width/3)){
			if(y > ((height*2)/3)){
				ct.recordIsDifferentiable(maskIndex == BL_MASK, cont, false);
			}else
			if(y < (height/3)){
				ct.recordIsDifferentiable(maskIndex == TL_MASK, cont, false);
			}
			else{
				ct.recordIsDifferentiable(maskIndex == LL_MASK, cont, false);
			}
		}else
		if(x > ((width*2)/3)){
			if(y > ((height*2)/3)){
				ct.recordIsDifferentiable(maskIndex == BR_MASK, cont, false);
			}else
			if(y < (height/3)){
				ct.recordIsDifferentiable(maskIndex == TR_MASK, cont, false);
			}else
			{
				ct.recordIsDifferentiable(maskIndex == RR_MASK, cont, false);
			}
		}else
		{
			if(y > ((height*2)/3)){
				ct.recordIsDifferentiable(maskIndex == BB_MASK, cont, false);
			}else
			if(y < (height/3)){
				ct.recordIsDifferentiable(maskIndex == TT_MASK, cont, false);
			}else
			{
				ct.recordIsDifferentiable(false, cont, true);
				skipCount ++;
			}
		}
		//run this function
		checkCT();
		return false;
	}
	
	private void checkCT(){
		//check if the current trial is finished
		if (ct.isFinished())
		{
			//add this data to the finishedTrials
			finishedTrials.add(ct);
			//remove this data from the testingTrails so it doesn't get tested again
			testingTrials.remove(ct);

			// test to see if the calibration procedure is finished
		}
		
		//if testingTrails is finished
		if (testingTrials.isEmpty())
		{
			//run the exittest function
			exittest();
		}else{
			//increment the trialIndex
			trialIndex ++;
			//check if the trialIndex has exceeded the current testingTrials size
			if (trialIndex >= testingTrials.size())
			{
				//shuffle testingTrials
				Collections.shuffle(testingTrials);
				//reset the trialIndex
				trialIndex = 0;
			}
			//invalidate to recall the draw function and draw a new C
			invalidate();
		}
	}
	
	private void generateCalibrationTrials() throws IOException
	{
		// determine the base color for this calibration
		double[] LUV = { BASE_LUM, 0.0, 0.0 };
		int baseRGB = ColorConverter.LUVtoRGB(LUV);
		for (int line=0; line<NUM_LINES; line=line+1) {
			testingTrials.add(new CalibrationTrial(CalibrationType.HUE, HUE_ANGLES[line], baseRGB, LINE_COLORS[line], LINE_COLORS[line]));
		}
		// shuffle sessions
		Collections.shuffle(testingTrials);
	}
	
	private void exittest(){
		//set view and canvas to be invisible preventing the editText and save profile button
		//from being covered by canvas
		this.setVisibility(View.INVISIBLE);
		//set finished to be true
		finished = true;
		//call the parent activity's visible function to make the editText and save profile
		//button become visible
		act.visible();
		//set a focus change listener on the editText
		yourEditText.setOnFocusChangeListener(new OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
            	//for the view editText
                if (v == yourEditText)
                {
                	//if being focused on
                    if (hasFocus)
                    {
                        //open keyboard
                        ((InputMethodManager) cont.getSystemService(Context.INPUT_METHOD_SERVICE)).showSoftInput(yourEditText,
                                InputMethodManager.SHOW_FORCED);

                    }
                    else
                    { //close keyboard
                        ((InputMethodManager) cont.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                                yourEditText.getWindowToken(), 0);
                    }
                }
            }
        });
		//set the focus to be on the editText
		setEditTextFocus(true);
		//listen for the enter key to be pressed
		yourEditText.setOnKeyListener(new OnKeyListener() {
	        public boolean onKey(View v, int keyCode, KeyEvent event) {
	            // If the event is a key-down event on the "enter" button
	            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
	                (keyCode == KeyEvent.KEYCODE_ENTER)) {
	            	//remove focus from editText and close keyboard
	        		setEditTextFocus(false);
	              return true;
	            }
	            return false;
	        }

	    }); 
		//sort finishedTrials
		Collections.sort(finishedTrials);
	}
	
	public void setEditTextFocus(boolean isFocused)
	{
		//set everything to being focused on
	    yourEditText.setCursorVisible(isFocused);
	    yourEditText.setFocusable(isFocused);
	    yourEditText.setFocusableInTouchMode(isFocused);

	    if (isFocused)
	    {
	        yourEditText.requestFocus();
	    }
	}
	
	public String getName(){
		String name = yourEditText.getText().toString();
		return name;
	}
	
	public ArrayList<CalibrationTrial> getProfileData(){
		return finishedTrials;
	}
	
	/*public long[] getReactionTimes(){
		return reaction_times;
	}
	
	public int[][] getC_Colours(){
		return Ccolours;
	}*/
	
	public int getSkipCount(){
		return skipCount;
	}
	
	public void setFinish(boolean state){
		finished = state;
	}
	
	public Boolean isDone(){
		return finished;
	}
}
