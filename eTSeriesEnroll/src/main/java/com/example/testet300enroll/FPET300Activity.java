package com.example.testet300enroll;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.testet300enroll.FPFingerActivity.Layouts;

import egistec.fingerauth.api.FPAuthListeners;
import egistec.fingerauth.api.FpResDef;
import egistec.fingerauth.api.SettingLib;

interface ET300Action {		
	public static final int SHOW_PLACE_FINGER  = 1;
	public static final int SHOW_REMOVE_FINGER = 2;
	public static final int SHOW_HOLD_FINGER   = 3;
	
	public 	int getLayoutID();
	public void initial();
	public void showEnrollOK();
	public void showBadProgress();
	public void updateUI(int status);
	public void addEnrollMap(byte[] map, int w, int h);
	public void goodProgress();
	public void addEnrollProgress();
	public void stopEnrollGuideAnimation();	
	public void runFingerAnimation();
	public void holdFinger();
	public void removeFinger();
}


public class FPET300Activity extends Activity implements FPAuthListeners.EnrollListener, 
  FPAuthListeners.StatusListener, FPAuthListeners.TinyEnrollListener{
	private static final String TAG = "FPET300Activity";	
	private static final int ENROLL_MAP_H = 512;
	private static final int ENROLL_MAP_W = 512;

	private TextView mHintTitleView;
	private TextView mHintDescriptionView;
	//private TextView mEnrollOKView;
	
	private boolean mIsEnrolling=false;
	private SettingLib mYu;
	
	private ProgressDialog mProgressDialog;
	private TextView mEnrollOKView;
	private String mEnrollID;
	private String mEnrollLayout;
		
	private ET300Action mEa;
	private Handler mHandler = new Handler();
	private long mStartTime;
	private boolean mIsFingerOn;
	private boolean mIsBadImage;
	
	private Object mACKToken = new Object();
	private static final int TIMER_DELAY = 1500;
	private static final int WAIT_OBJECT_DELAY = 2000;
	private boolean mKeyBackPressed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
									
		setEnrollView();
	
		mEnrollOKView = (TextView) findViewById(R.id.tv_enroll_OK_test);

		mYu = new SettingLib(this);		

		setupActionBar();
		
		mHintTitleView =  (TextView) findViewById(R.id.hint_title);
		mHintDescriptionView =  (TextView) findViewById(R.id.hint_description);	
		mEnrollID =  getIntent().getStringExtra(FPFingerActivity.ENROLL_ID);		
		Log.d(TAG, "mEnrollLayout: " + mEnrollLayout);		
	}    
	
    @Override
    public void onStart() {
    	super.onStart();
        Log.d(TAG, "onStart");
    }
	
	@Override
	protected void onResume() {
		super.onResume();		
		Log.d(TAG, "onResume");		
		mYu.setEnrollListener(this);
		mYu.setTinyEnrollListener(this);
		mYu.setStatusListener(this);
		//mYu.bind();		
	} 

    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
    	super.onPause();    	
    	mYu.abort(); 
    	//mYu.unbind();
    }
	
    @Override
    public void onStop() {
        Log.d(TAG, "onStop");     
    	super.onStop();
    	
    	mEa.stopEnrollGuideAnimation();		
		mYu = null;
		mEa = null;		
    }	
          
    
	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {

		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setDisplayUseLogoEnabled(true);
		getActionBar().setTitle("");
		getActionBar().hide();

	}
	
	private void setEnrollView(){
		mEnrollLayout = getIntent().getStringExtra(FPFingerActivity.LAYOUT_ID);		
		if(mEnrollLayout.equals(Layouts.ET300_CLASSICAL.toString())){
			Log.d(TAG, "+++ ET300_CLASSICAL +++");
			mEa =(ET300Action) new FPET300Classical(FPET300Activity.this);
		}else if(mEnrollLayout.equals(Layouts.ET300_TUTORIAL.toString())){
			Log.d(TAG, "+++ ET300_TUTORIAL +++");
			mEa =(ET300Action) new FPET300Tutorial(FPET300Activity.this);		
		}		
		setContentView(mEa.getLayoutID());			
		mEa.initial();						
	}
			
	private void doNewThreadAbort() {
		new Thread(){
			@Override
			public void run(){
				runOnUiThread(new Runnable(){
					@Override
					public void run(){
						doAbort();
					}
				});
			}
		}.start();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	int item_id = item.getItemId();
    	switch (item_id){
    		case android.R.id.home:
    			doNewThreadAbort();
    			break;
    		default: return false;
    	}
    	return true;
    }	

	public void showEnrollOK(){
		Log.d(TAG, "showEnrollOK");
		
		mHintTitleView.setText(R.string.enroll_OK_title);
		mHintDescriptionView.setText(R.string.enroll_OK);
				
		if(null == findViewById(R.id.progress_container)) return;
		
		ViewGroup ll =null;
		if(findViewById(R.id.progress_container) instanceof LinearLayout){
			ll = (LinearLayout)findViewById(R.id.progress_container);	
			((LinearLayout)ll).setGravity(Gravity.CENTER);
		}else if(findViewById(R.id.progress_container) instanceof RelativeLayout){
			ll = (RelativeLayout)findViewById(R.id.progress_container);
		}		
		//findViewById(R.id.tv_enroll_OK_test).setVisibility(View.VISIBLE);
	}
	
	private void showEnrollReset(){
		if(null != mEnrollOKView){
			mEnrollOKView.setVisibility(View.INVISIBLE);
			mEnrollOKView.setEnabled(false);
		}
		mHintTitleView.setText(R.string.place_your_finger);
		mHintDescriptionView.setText(R.string.enroll_hint);			
		
	}
	
	void doFinish(){
		 Intent returnIntend =new Intent(); 				
		 setResult(0, returnIntend);
		 finish();
	}

	public void onEnrollOKClick(View v) {
		doFinish();
	}
	
	private void captureEnroll(String enrollKey){
		showEnrollReset();
		mYu.connectDevice();
		mYu.enroll(enrollKey);
		mIsEnrolling = true;
		Log.d(TAG, "captureEnroll enrollKey: " + enrollKey);
	}
	
	private void doAbort(){
		mProgressDialog = ProgressDialog.show(FPET300Activity.this, "", getString(R.string.dialog_enroll_abort), true, false);
		mYu.abort();
	}
	
	@Override
	public void onBadImage(int status) {
		Log.d(TAG, "onBadImage()");		
		handleBadImage();
	}

	private void handleBadImage(){
		mEa.showBadProgress();		
		
		mIsBadImage = true;
		
		new Timer().schedule(new TimerTask(){
			@Override
			public void run(){
				synchronized(mACKToken){
					mACKToken.notify();
				}
			}
		}, TIMER_DELAY);
	}	
	
	@Override
	public void onFingerFetch() {
		Log.d(TAG, "onFingerFetch()");
		
		if(mIsBadImage){			
		  synchronized (mACKToken) {
	        try {
		  		  mACKToken.wait(WAIT_OBJECT_DELAY);
		  	} catch (InterruptedException e) {
		  	  // TODO Auto-generated catch block
		  	  e.printStackTrace();
		  	} 
	      } 
		}
		
		if(mIsFingerOn){			
			mEa.updateUI(ET300Action.SHOW_REMOVE_FINGER);
		}else{
			mEa.updateUI(ET300Action.SHOW_PLACE_FINGER);
		}
	}
	
	@Override
	public void onFingerImageGetted() {
		Log.d(TAG, "onFingerImageGetted()");
				
		if(((ImageView)findViewById(R.id.image_guide)).isShown()){

			//final View showView = (RelativeLayout) findViewById(R.id.container);
			View showView2=null;
			if(findViewById(R.id.progress_container) instanceof LinearLayout){
				final View showView = (RelativeLayout) findViewById(R.id.container);
				showView2 = (LinearLayout) findViewById(R.id.progress_container);
				showView.setAlpha(0f);
			    showView.setVisibility(View.VISIBLE);
			    
			    if(showView2!=null)
			    	showView2.setVisibility(View.VISIBLE);		    
			    
			    showView.animate()
	            .alpha(1f)
	            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
	            .setListener(null);
			}else if(findViewById(R.id.progress_container) instanceof RelativeLayout){
				showView2 = (RelativeLayout) findViewById(R.id.progress_container);
				   if(showView2!=null)
				    	showView2.setVisibility(View.VISIBLE);		
			}
					
			if(mEa instanceof FPET300Classical){
				final View hideView = (ImageView) findViewById(R.id.image_guide);
				
			    hideView.animate()
	            .alpha(0f)
	            .setDuration(getResources().getInteger(android.R.integer.config_shortAnimTime))
	            .setListener(new AnimatorListenerAdapter() {
	                @Override
	                public void onAnimationEnd(Animator animation) {
	                    hideView.setVisibility(View.GONE); 	                   
	                }
	            });
	            
			}						
		}	
		mEa.addEnrollProgress();
		mEa.updateUI(ET300Action.SHOW_REMOVE_FINGER);

	}

	@Override
	public void onUserAbort() {
		Log.d(TAG, "onUserAbort()");		
		if(mProgressDialog == null){
			doFinish();
			return;
		}
		
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
			NavUtils.navigateUpFromSameTask(FPET300Activity.this);
		}
	}

	private Runnable updateTimer = new Runnable() {		
	    public void run() {		    	   
		    Long spentTime = System.currentTimeMillis() - mStartTime;
		    Long seconds = (spentTime/1000) % 60;
		    Log.d(TAG, "seconds: " + seconds);
		            
		    if((5 == seconds) && mIsFingerOn){			            	
		    	Display display = getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);
		        final int width = size.x;				            	
		            	
		        ObjectAnimator oa = ObjectAnimator.ofFloat(mHintTitleView, "translationX", 0, width);
		        oa.setDuration(300);
		        oa.addListener(new AnimatorListenerAdapter() {
		        	@Override
		            public void onAnimationEnd(Animator animation) {
		        	    // do something when the animation is done
		        		mHintTitleView.setText(getString(R.string.remove_your_finger_plz));
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
	
	
	@Override
	public void onStatus(int status) {
		//Log.d(TAG, "onStatus()");
		
		Log.d(TAG, "onStatus: " + status);
		
		if(FPCommon.STATUS_FINGER_ON == status){ // finger on
			mIsFingerOn = true;
			mStartTime = System.currentTimeMillis();						
			mHandler.removeCallbacks(updateTimer);
			mHandler.postDelayed(updateTimer, 1000);
			mEa.goodProgress();
			mEa.stopEnrollGuideAnimation();
			mEa.holdFinger();
		}else if(FPCommon.STATUS_FINGER_OFF == status){ // finger off
			mIsFingerOn = false;
			mHandler.removeCallbacks(updateTimer);
			if(mHintTitleView.getText().equals(getString(R.string.remove_your_finger_plz))){
				
				Display display = getWindowManager().getDefaultDisplay();
        		Point size = new Point();
        		display.getSize(size);
        		final int width = size.x;
        		//final int height = size.y;						
				
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
				
			}
			//mEa.runFingerAnimation();	
			//mEa.removeFinger();
		}else if(FPCommon.STATUS_WAIT_FINGER_ON == status){ //wait for finger on
			mEa.updateUI(ET300Action.SHOW_PLACE_FINGER);
			mEa.runFingerAnimation();
		}
	
	}

	@Override
	public void onSuccess() {		
		Log.d(TAG, "onEnrollSuccess()");
		mIsEnrolling = false;
	}
	private void dialogAndFinish(String message){
        new AlertDialog.Builder(this)
		.setIcon(android.R.drawable.ic_dialog_alert)
		.setMessage(message)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            	doFinish();
            };
		})
		.create()
		.show();
}
	@Override
	public void onFail() {
		Log.d(TAG, "onEnrollFail()");
		mIsEnrolling = false;
		dialogAndFinish("Can not scan the finger, please try another finger");
	}

	@Override
	public void onProgress() {
		Log.d(TAG, "onProgress()");				
	}

	@Override
	public void onStatusCandidate(int status, int[] mapInfo, byte[] map) {
		Log.d(TAG,"onStatusCandidate status="+status+" idx="+mapInfo[0]);
		if (status == FpResDef.TINY_STATUS_BEFORE_GENERALIZE){
			mEa.showEnrollOK();
		}else if(status == FpResDef.TINY_STATUS_ADD_CANDIDATE){ // good image
			//mEa.goodProgress();
			mIsBadImage=false;
		}else if(status == FpResDef.TINY_STATUS_DUPLICATED_CANDIDATE){		
			handleBadImage();
		}	
	}
    
	@Override
	public void onEnrollMap(int[] mapInfo, byte[] map) {
		Log.d(TAG,"onEnrollMap idx="+mapInfo[0]+" w="+mapInfo[1]+" h="+mapInfo[2]+" map="+map[0]);		
		if(mapInfo[1] != ENROLL_MAP_W && mapInfo[2] != ENROLL_MAP_H) return;				
		mEa.addEnrollMap(map, mapInfo[1], mapInfo[2]);
	}		
	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		
		if (keyCode == KeyEvent.KEYCODE_BACK) { // BACK button pressed	
			if (mIsEnrolling) { 
			  doAbort();
			  mIsEnrolling = false;
			}else{
			  doFinish();
			}
			return true;
		}	
		return super.onKeyDown(keyCode, event);
	}	
	
	@Override
	public void onServiceConnected() {
		Log.d(TAG, "onServiceConnected");			
		captureEnroll(mEnrollID);
	}

	@Override
	public void onServiceDisConnected() {
		Log.d(TAG, "onServiceDisConnected");
	}
	
	@Override
	protected void onDestroy (){	
		Log.d(TAG, "+++++++++++++++onDestroy+++++++++++++++++");
		System.gc();
		super.onDestroy();		
	}
}
