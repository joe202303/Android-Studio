package com.example.testet300enroll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

import com.bumptech.glide.Glide;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.drawable.ClipDrawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import egistec.fingerauth.api.FPAuthListeners;
import egistec.fingerauth.api.FpResDef;
import egistec.fingerauth.api.SettingLib;

public class FPET310CFragmentForSwipeEnroll extends Fragment implements ET310ActionForSwipeEnroll,
  FPAuthListeners.EnrollListener, FPAuthListeners.StatusListener,
  FPAuthListeners.TinyEnrollListener, FPAuthListeners.ThreadImageListener,
  FPAuthListeners.EnrollMapProgressListener, FPAuthListeners.MatchedImageListener{

	private static final String TAG = "FPET310CFragmentForSwipeEnroll";
	private SettingLib mYu;
	private ImageView mEnrollFrame;
	private ImageView mEnrollMap;
	private TextView mEnrollStatus;
	//private TextView mEnrollProgress;
	private Button mEnrollOKView;
	private int pre_percentage;
	private int mCount;
	private String mEnrollID;
	private boolean mIsEnrolling;
	private ProgressDialog mProgressDialog;
	private boolean mIsCommingEnrollMap;
	private static final double SCALE_RATIO = 3;	
	byte[] mSavebitmap;
	EditText username ;
	private boolean mWetfinger;
	private long Wetfingertime = 0;
	
	private boolean mIsFingerOn;
	private long mStartTime;
	private long mRedundantMessageStartTime;
	private long mClearMessageStartTime;
	private TextView mHintTitleView, mEnrollAlert, mEnrollHint, mEnrollGuide;
	private Handler mHandler = new Handler();
	private String mMode;
	private Runnable updateTimer = new Runnable() {	
		@Override
	    public void run() {		    	   
		    Long spentTime = System.currentTimeMillis() - mStartTime;
		    Long seconds = (spentTime/1000) % 60;
		    Log.d(TAG, "seconds: " + seconds);
		            
		    if((5 == seconds) && mIsFingerOn){			            	
		    	Display display = getActivity().getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);
		        final int width = size.x;				            	
		            	
		        ObjectAnimator oa = ObjectAnimator.ofFloat(mHintTitleView, "translationX", 0, width);
		        oa.setDuration(300);
		        oa.addListener(new AnimatorListenerAdapter() {
		            public void onAnimationEnd(Animator animation) {
		        	    // do something when the animation is done
		        		mHintTitleView.setText(getActivity().getString(R.string.remove_your_finger_plz));
		        		ObjectAnimator oa = ObjectAnimator.ofFloat(mHintTitleView, "translationX", width, 0);
		        		oa.setDuration(300);
		        		oa.start();	
		        	}
		        });
		        oa.start();				        				            	
		        return;
		    }		            
		    mHandler.postDelayed(this, 1000);
		}
	};

	/* For swipe enroll UI */
	private View imageView;
	private View imageView1;
	private View Enroll_progress_background;
	private TextView Enroll_text;
	private ClipDrawable Enroll_progress_bar;
	private ClipDrawable Enroll_progress_bar_all_blue;
	private ImageView EnrollGif;

	private ProgressBar mEnrollProgress;
	public String mCasename;
	public String mFilename;
	public Boolean mCansaveimage;
	public int mImagecount;
	public String mFingernumber;
	public String mFilenamecount;
	
	private byte[] mMatchedImage;		
	
	public static final int STATUS_HIGHLY_SIMILAR  = 1245;
	
	public FPET310CFragmentForSwipeEnroll(String a, String b, String c,boolean d, int e){
		mCasename = a;
		mFilename = b;
		mFingernumber = c;
		mCansaveimage = d;
		mImagecount = e;
		
	}	
	
	public void setEnrollId(String enrollID) {		
		mEnrollID = enrollID;	
		Log.d(TAG, "+++ setEnrollId +++"+mEnrollID);
		 
		
	}	

	private void updateEnrollTitle(int resId){
		mHintTitleView.setText(resId);
		if(resId == R.string.place_your_finger){
			mHintTitleView.setTextColor(Color.WHITE);
		}else if(resId == R.string.remove_your_finger){
			mHintTitleView.setTextColor(Color.GREEN);
		}
		mHintTitleView.postInvalidate();
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		mMode = getActivity()
				  .getSharedPreferences(FPFingerActivity.PREF_KEY, Context.MODE_PRIVATE)
				  .getString("touchmode", "");
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = null;
		if(mMode.equals(FPFingerActivity.MODE_SWIPE)){
		  rootView = inflater.inflate(R.layout.fragment_classical_fpet310,
				container, false);
		}else{
		  rootView = inflater.inflate(R.layout.fragment_classical_fpet310_press,
				container, false);
		}
		Log.d(TAG, "+++ onCreateView new SettingLib()+++");
		mYu = new SettingLib(getActivity());
		mEnrollFrame = (ImageView) rootView.findViewById(R.id.enroll_frame);
		mEnrollMap = (ImageView) rootView.findViewById(R.id.enroll_map);
		mEnrollStatus = (TextView) rootView.findViewById(R.id.enroll_status);
		//mEnrollProgress = (TextView) rootView.findViewById(R.id.enroll_progress);
		mEnrollOKView = (Button) rootView.findViewById(R.id.tv_enroll_OK);
		mHintTitleView =  (TextView) rootView.findViewById(R.id.hint_title);
		mEnrollHint = (TextView) rootView.findViewById(R.id.hint_description);
		mEnrollAlert = (TextView) rootView.findViewById(R.id.hint_description2);
		mEnrollGuide = (TextView) rootView.findViewById(R.id.enroll_guide);
		mEnrollOKView.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v){
				Log.d(TAG, "+++ continue onClick +++");
				if(mMode.equals(FPFingerActivity.MODE_TOUCH)){
					saveEnrollTempLate();
					Intent returnIntend = new Intent(); 
					returnIntend.putExtra("index", mImagecount);
					getActivity().setResult(0, returnIntend);
					getActivity().finish();			
					return;
				}else{
					saveEnrollTempLate();
					Intent returnIntend = new Intent(); 				
					getActivity().setResult(0, returnIntend);
					getActivity().finish();	
				}
			}
		});

		/* For swipe enroll UI */
		imageView = rootView.findViewById(R.id.image);
		imageView1 = rootView.findViewById(R.id.image1);
		Enroll_progress_background = rootView.findViewById(R.id.enroll_progress_background);
		Enroll_progress_bar = (ClipDrawable) imageView.getBackground();
		Enroll_progress_bar_all_blue = (ClipDrawable) imageView1.getBackground();
        EnrollGif = (ImageView) rootView.findViewById(R.id.enroll_gif);
        Glide.with(this).load(R.drawable.begin_demo_v4_1016).into(EnrollGif);
		Enroll_text = (TextView) rootView.findViewById(R.id.enroll_text);

		mEnrollProgress = (ProgressBar) rootView.findViewById(R.id.ProgressBar);
		FPUtil.setBlockHomeKey(getActivity(), false);
		captureEnroll(mEnrollID);
		return rootView;
	}
	
	void saveEnrollTempLate()
	{
		mYu.setEnrollSession(true);
		mYu.VerifyPassWord();
		mYu.SetPassWord();
		mYu.setEnrollSession(false);
	}

	@Override
	public void onResume(){
		super.onResume();		
		mYu.setEnrollListener(this);
		mYu.setStatusListener(this);
		mYu.setTinyEnrollListener(this);
		mYu.setThreadImageListener(this);
		mYu.setEnrollMapProgressListener(this);
		mYu.setMatchedImageListener(this);

		//mYu.bind();
	}

	@Override
	public void onPause(){		
		super.onPause();
		mYu.abort();    
		//mYu.unbind();
	}
	
	@Override
	public void onDestroy (){			
		mYu = null;
		super.onDestroy();
	}
	
	private void captureEnroll(String enrollKey){
		Log.d(TAG, "+++ captureEnroll checheche +++");
		//mYu.connectDevice();
		//mYu.SettingUID(mEnrollID);
		mYu.swipeEnroll(enrollKey);
		mIsEnrolling = true;
		FPUtil.setBlockHomeKey(getActivity(), true);
	}	

	@Override
	public void onBadImage(int status) {		
		Log.d(TAG, "+++ onBadImage +++, status=" + status);
		long SpentTime = System.currentTimeMillis() - mRedundantMessageStartTime;
		if((status == FpResDef.FP_RES_GETTED_BAD_IMAGE) && (SpentTime > 2000)){
			mEnrollMap.setImageResource(R.drawable.bad_03);
			mEnrollGuide.setText("For the best result, please move your finger");
			mRedundantMessageStartTime = System.currentTimeMillis();
		}else if(status == FpResDef.FP_RES_PARTIAL_IMG){
			mEnrollMap.setImageResource(R.drawable.partial_05);
            mEnrollGuide.setText("Make sure your finger covers the entire sensor");
		}else if(status == FpResDef.FP_RES_WATER_IMG){
			mEnrollMap.setImageResource(R.drawable.partial_wet);
            mEnrollGuide.setText("Wipe the fingerprint sensor and try again");
		}else if(status == FpResDef.FP_RES_FAST_IMG){
            mEnrollGuide.setText("Keep your finger on sensor a little longer");
		}
	}

	@Override
	public void onServiceConnected() {
		Log.d(TAG, "+++ onServiceConnected +++");			
		//if(mYu.connectDevice())
		  captureEnroll(mEnrollID);
	}

	@Override
	public void onServiceDisConnected() {
		Log.d(TAG, "+++ onServiceDisConnected +++");
	}

	@Override
	public void onFingerFetch() {		
		if(mIsFingerOn){	
			updateEnrollTitle(R.string.remove_your_finger);
		}else{
			updateEnrollTitle(R.string.place_your_finger);
		}
	}

	@Override
	public void onFingerImageGetted() {
		Log.d(TAG, "+++++ onFingerImageGetted +++++");
		updateEnrollTitle(R.string.remove_your_finger);
	}

	@Override
	public void onUserAbort() {
		mIsEnrolling=false;
		if(mProgressDialog == null){
			((FPET310ActivityForSwipeEnroll)getActivity()).doFinish();
			return;
		}		
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
			NavUtils.navigateUpFromSameTask(getActivity());					
		}

	}

	@Override
	public void onStatus(int status) {
		Log.d(TAG, "+++ onStatus +++ " + status);
		if(status == 1075){ //capture ready
			Log.d(TAG, "status == 1075");
			/*
			mWetfinger = false;
			long diff;
			long seconds;
			do{
				diff=  System.currentTimeMillis() - Wetfingertime;
				seconds = (diff/1000) % 60;
			}while(seconds < 1 );
			mEnrollFrame.setImageResource(R.drawable.background);
			*/
			}
		else if (status == 1083){ //finger detect
			Log.d(TAG, "status == 1083");
            /* For swipe enroll UI */
			mEnrollMap.setImageResource(R.drawable.background);
			Enroll_progress_background.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.VISIBLE);
			imageView1.setVisibility(View.VISIBLE);
			EnrollGif.setVisibility(View.INVISIBLE);
			mHintTitleView.setText("Swipe your finger slowly from top to bottom");
			Enroll_text.setVisibility(View.INVISIBLE);

			/*mWetfinger = true;
			new Handler().postDelayed(new Runnable(){   
			    public void run() {
			    	updatewetfingericon();
			    }   
			}, 100);*/
		}
		
		if(mMode.equals(FPFingerActivity.MODE_SWIPE)){
			return;
		}
		if(FPCommon.STATUS_FINGER_ON == status){ // finger on
			mIsFingerOn = true;
			mStartTime = System.currentTimeMillis();
			mHandler.removeCallbacks(updateTimer);
			mHandler.postDelayed(updateTimer, 1000);			
		}else if(FPCommon.STATUS_FINGER_OFF == status){
			if(mHintTitleView == null || getActivity() == null) return;
			mIsFingerOn = false;
			mHandler.removeCallbacks(updateTimer);
			if(mHintTitleView.getText().equals(getActivity().getString(R.string.remove_your_finger_plz))){
				
				Display display = getActivity().getWindowManager().getDefaultDisplay();
        		Point size = new Point();
        		display.getSize(size);
        		final int width = size.x;						
				
				ObjectAnimator oa = ObjectAnimator.ofFloat(mHintTitleView, "translationX", 0, width);
				oa.setDuration(300);
				oa.addListener(new AnimatorListenerAdapter(){
					@Override
					public void onAnimationEnd(Animator animation){
						mHintTitleView.setText(R.string.place_your_finger);
						ObjectAnimator oa = ObjectAnimator.ofFloat(mHintTitleView, "translationX", width, 0);
						oa.setDuration(300);
						oa.start();
					}
				});
				oa.start();				
			}else if(mHintTitleView.getText().equals(getActivity().getString(R.string.remove_your_finger))){
				updateEnrollTitle(R.string.place_your_finger);
			}
		}else if(FPCommon.STATUS_WAIT_FINGER_ON == status){
			updateEnrollTitle(R.string.place_your_finger);
		}
	}
	
	public void updatewetfingericon(){
		if(mWetfinger){
			mEnrollFrame.setImageResource(R.drawable.partial_wet);
			Wetfingertime=   System.currentTimeMillis();
		}	
	}

	@Override
	public void onSuccess() {		
		Log.d(TAG, "+++ onSuccess +++");
		/* for swipe enroll UI */
		mEnrollGuide.setVisibility(View.INVISIBLE);
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run(){				
				mEnrollProgress.setProgress(mEnrollProgress.getProgress()+5);
				//Enroll_progress_bar.setLevel(Enroll_progress_bar.getLevel() + 30);
				if(mEnrollProgress.getProgress() < 100){
					mHandler.postDelayed(this, 50);
				}else{
					mEnrollStatus.setText(R.string.enroll_success);
					mEnrollOKView.setVisibility(View.VISIBLE);			
					mEnrollOKView.setEnabled(true);			
					mIsEnrolling = false;
				}
			}
		}, 200);			
	}

	@Override
	public void onFail() {		
		Log.d(TAG, "+++ onFail +++");
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run(){				
				mEnrollProgress.setProgress(mEnrollProgress.getProgress()+5);
				if(mEnrollProgress.getProgress() < 50){
					mHandler.postDelayed(this, 200);
				}else{
					mEnrollStatus.setText(R.string.enrol_failed);
					mEnrollOKView.setVisibility(View.VISIBLE);			
					mEnrollOKView.setEnabled(true);
					mIsEnrolling = false;					
				}
			}
		}, 200);
	}
	
	/*
	 *  the callback function is called when getting good image 
	 */
	
	@Override
	public void onProgress() {
		Log.d(TAG, "+++ onProgress +++");		
		
		if(mCount == 4){
			mEnrollAlert.setText(R.string.enroll_alert_msg2);
			mEnrollAlert.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run(){
					//mEnrollAlert.setVisibility(View.GONE);
				}
			}, 2000);
		}
		
		mCount++;		
				
		if(mCount == 1){
			mEnrollHint.setText(R.string.enroll_hint2);
			//mEnrollAlert.setVisibility(View.VISIBLE);
			mEnrollAlert.setText(R.string.enroll_alert_msg);
		}
		
		//mEnrollProgress.setText(String.valueOf(++mCount));
		//mEnrollProgress.postInvalidate();		

		/* For swipe enroll showing redundant image*/
		/*if(mCansaveimage)
			saveMerchandiseImgToSD(mMatchedImage);*/
	}		

	@Override
	public void onStatusCandidate(int status, int[] mapInfo, byte[] map) {
		Log.d(TAG, "+++ onStatusCandidate +++ status="+status);
		
		if((mCount > 3) && (status == STATUS_HIGHLY_SIMILAR)){
			//mEnrollAlert.setVisibility(View.VISIBLE);
			mEnrollAlert.setText(R.string.enroll_alert_msg2);
			mEnrollAlert.setTextColor(getResources().getColor(android.R.color.holo_orange_light));
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run(){
					//mEnrollAlert.setVisibility(View.GONE);
				}
			}, 2000);			
		}
	}
	
	private int DSP_SIZE_W = 768;
	private int DSP_SIZE_H = DSP_SIZE_W;
	private static int MAP_SIZE_W = 256;
	private static int MAP_SIZE_H = MAP_SIZE_W;
	

	
	private boolean invalidMapPos(int pos, byte[] map){
		return (pos < 0) || (pos > (map.length-1));
	}
	private boolean foundPixelEachRow(int row, byte[] map){
		int startPos = MAP_SIZE_H * row + (MAP_SIZE_W/2);
		for (int i = 0; i < (MAP_SIZE_W/2); i++) {
			
			int pos = startPos - i;
			if (invalidMapPos(pos, map)) return false;
			if (map[pos] != 0 ) return true;

			pos = startPos + i;
			if (invalidMapPos(pos, map)) return false;
			if (map[pos] != 0 ) return true;
		}
		return false;
	}

	private boolean foundPixelEachCol(int col, byte[] map){
		int startPos = MAP_SIZE_W * (MAP_SIZE_H/2) + col;
		for (int i = 0; i < (MAP_SIZE_H/2); i++) {
			
			int pos = startPos - (i * MAP_SIZE_W);
			if (invalidMapPos(pos, map)) return false;
			if (map[pos]!=0) return true;
			
			pos = startPos + (i * MAP_SIZE_W);
			if (invalidMapPos(pos, map)) return false;
			if (map[pos]!=0) return true;
		}
		return false;
	}
	
	private int INITIAL_MAP_SIZE = 128;
	private int FIND_STEP_SIZE = 10;
	private int CENTER_POS = MAP_SIZE_H/2;
	private int foundPixelMaxSize(byte[] map){
		int findPos = CENTER_POS; 
		int realSize = INITIAL_MAP_SIZE;
		
		for (int side = 0; side < 4; side++){
			do{
				
				switch(side){
				case 0:
				case 2:
					findPos = CENTER_POS-(realSize /2);
					break;
					
				case 1:
				case 3:
					findPos = CENTER_POS+(realSize /2);
					break;
				}

				if (side < 2){ //for row
					if (!foundPixelEachRow(findPos, map))break;
				}else{ //for column
					if (!foundPixelEachCol(findPos, map)) break;
				}
				
				realSize += FIND_STEP_SIZE; 
			}while(realSize < MAP_SIZE_W);  
		}	
		Log.d(TAG, "final realSize="+realSize);
		if (realSize > MAP_SIZE_W) realSize = MAP_SIZE_W;
		return realSize;
	}

	
	private void reverseMapColor(byte[] map){
		for(int index = 0; index < map.length; index ++) {
			map[index] = (byte) ~map[index];
		}
	}
	private byte[] createPixelMap(int realW, byte[] map){
		if (realW < INITIAL_MAP_SIZE) return null;
		if (realW >= MAP_SIZE_W)	return map;
		
		byte[] newMap = new byte[realW*realW];
		int addX = (MAP_SIZE_W - realW) / 2;
		int addY = (MAP_SIZE_H - realW) / 2;
		int srcStartPos = addY*MAP_SIZE_W+addX;
		
		for (int i=0; i<realW;i++){
			int srcPos = srcStartPos + (i * MAP_SIZE_W);
			int dstPos = i*realW;
			if ((srcPos + realW) > (map.length-1)) break;
			System.arraycopy( map, srcPos , newMap, dstPos, realW );
		}
		return newMap;
	}
	
	@Override
	public void onEnrollMap(int[] mapInfo, byte[] map) {

		Log.d(TAG, "+++ onEnrollMap +++");	
		if ((mapInfo[1]!=MAP_SIZE_W)||(mapInfo[2]!=MAP_SIZE_H)){
			Log.e(TAG, "enrollMap size is incorrect, width="+mapInfo[1]+" height="+mapInfo[2]);
			return;
		}
		reverseMapColor(map);
		int pixelMaxSize = foundPixelMaxSize(map);  
		byte[] pixelMap = createPixelMap(pixelMaxSize, map);
		if (pixelMap == null) return;
		Bitmap imgMap = Bitmap.createBitmap(pixelMaxSize, pixelMaxSize, Bitmap.Config.ALPHA_8);		
		// config flip bitmap
		Matrix matrix = new Matrix();
		matrix.postRotate(180);		
		imgMap.copyPixelsFromBuffer(ByteBuffer.wrap(pixelMap));
		//flip enrollmap
		imgMap = Bitmap.createBitmap(imgMap, 0, 0, imgMap.getWidth(), imgMap.getHeight(), matrix, true);		
		Bitmap imgDisplay = Bitmap.createScaledBitmap(imgMap, DSP_SIZE_W, DSP_SIZE_H, false);
		mEnrollMap.setImageBitmap(imgDisplay);
		mEnrollMap.postInvalidate();	
		mIsCommingEnrollMap = true;	
			
	}		
		
	private void doAbort(){
		mProgressDialog = ProgressDialog
		  .show(getActivity(), "", getString(R.string.dialog_enroll_abort), true, false);			
		mYu.abort();
	}	

	@Override
	public void onKeyDown() {
		if(mIsEnrolling){
			doAbort();			
		}else{
			((FPET310ActivityForSwipeEnroll)getActivity()).doFinish();
		}
	}

	@Override
	public void onGetImg(byte[] img, int width, int height) { // show press thread frame
		Log.d(TAG, "onGetImg"+ width+ height);
		//if(mIsCommingEnrollMap) return;
		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);	
		Log.d(TAG, "onGetImg2"+img.length);
		// reverse color
		for(int index = 0; index < img.length; index ++) {
			img[index] = (byte) ~img[index];
		}		
		Log.d(TAG, "onGetImg3");
		bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(img));
		Log.d(TAG, "onGetImg4");
		// config flip bitmap
		Matrix matrix = new Matrix();
		matrix.postRotate(180);
		Log.d(TAG, "onGetImg5");
		//flip frame
		Bitmap rotatedFrame = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);		
		Bitmap scaledBmp = Bitmap.createScaledBitmap(rotatedFrame, (int)(width*SCALE_RATIO), (int)(height*SCALE_RATIO), false);
		mEnrollFrame.setImageBitmap(scaledBmp);
		mEnrollFrame.postInvalidate();
		Log.d(TAG, "onGetImg6");
		if(!mIsCommingEnrollMap){
			mEnrollMap.setImageBitmap(scaledBmp);
			mEnrollMap.postInvalidate();	
		}		
	}

	@Override
	public void onEnrollMapProgress(int progress) {
		Log.d(TAG, "onEnrollMapProgress "+ progress);
		mEnrollProgress.setProgress(progress);

		/* For swipe enroll UI */
		long SpentTime = System.currentTimeMillis() - mClearMessageStartTime;
		if ((progress > pre_percentage) && (SpentTime > 500)) {
			mEnrollGuide.setText("");
			mClearMessageStartTime = System.currentTimeMillis();
			pre_percentage = progress;
		}
		if (progress <= 80) {Enroll_progress_bar.setLevel(progress * 100 / 80 * 100);}
		else {Enroll_progress_bar.setLevel(10000);}
		if (progress >= 80)Enroll_progress_bar_all_blue.setLevel(progress * 100);
	}
	private void saveMerchandiseImgToSD(byte[] image_buf){
		
		String filename;
		mFilenamecount = String.format("%04d", mImagecount);
		filename = mFilename +"_"+ mFingernumber + "_" + mFilenamecount;
		Log.d(TAG, "filename= " + filename);
		mImagecount++;

		//File file = new File("/sdcard/temp");
		File file = new File("/sdcard/temp/" + mCasename + "/" + mFilename + "/" + mFingernumber +"/swipe_enroll" + "/st");
        if (!file.exists()) {
            file.mkdirs();
        }
        Log.d(TAG, "file path="+ file.getAbsolutePath());
        //File myDrawFile = new File("/sdcard/temp/"+ filename +".bin");
        File myDrawFile = new File(file.getAbsolutePath() + "/" + filename +".bin");
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myDrawFile);
			if(fos!=null){
				fos.write(image_buf);
		        fos.close();
		        Log.d(TAG, "save image filename= " /*+ filename*/ );
		        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			//Toast.makeText(getActivity(), "store file " + filename, Toast.LENGTH_SHORT).show();
		}
	}
		public static boolean checkSDCard() {
            if (Environment.getExternalStorageState().equals(
                    Environment.MEDIA_MOUNTED)) {
                return true;
            }
            return false;
        }


		@Override
		public void onGetMatchedImg(byte[] img, int width, int height) {
			Log.d(TAG, "++++++ onGetMatchedImg ++++++");
			mMatchedImage = img.clone();

			/* for swipe enroll showing redundant image */
			if(mCansaveimage)
				saveMerchandiseImgToSD(mMatchedImage);
			
			Bitmap match_img = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
			for (int index = 0; index < img.length; index ++) {
				img[index] = (byte) ~img[index];
	     	}
			match_img.copyPixelsFromBuffer(ByteBuffer.wrap(img));
					
			mEnrollFrame.setImageBitmap(match_img);
		}
}
