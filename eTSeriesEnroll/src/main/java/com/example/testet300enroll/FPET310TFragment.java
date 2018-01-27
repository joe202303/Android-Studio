package com.example.testet300enroll;

import java.util.Timer;
import java.util.TimerTask;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import egistec.fingerauth.api.FPAuthListeners;
import egistec.fingerauth.api.SettingLib;

public class FPET310TFragment extends Fragment implements ET310Action,
  FPAuthListeners.EnrollListener, FPAuthListeners.StatusListener,
  FPAuthListeners.VerifyLearningListener{

	public static final String TAG = "FPET310TFragment";
	private static final int LEARNING_THRESHOLD = 80;
	private SettingLib mYu;
	private String mEnrollID;
	private boolean mIsEnrolling;
	private ProgressDialog mProgressDialog;
	private ImageView mFingerGuide;	
	private TextView mTextHint;
	//private TextView mTextHint2;
	private TextView mEnrollOKView;
	private int mAnimCount;
	private AnimationDrawable mAnimFrame; 
	private boolean mIsFingerOn;
	private long mStartTime;
	private Handler mHandler = new Handler(Looper.getMainLooper());
	
	private boolean mIsBadImage;
	private Object mACKToken = new Object();
	private static final int TIMER_DELAY = 1500;
	private static final int WAIT_OBJECT_DELAY = 2000;
	
	private static final int DUP_VERIFY_MAX = 30;	
	private static final int LEARNING_MAX = 8;
	private boolean mIsLearning;
	private int dupCnt, learningCnt;
	
	//private boolean mEnrollResult;
	private String mPhase;
	
	private Runnable updateTimer = new Runnable() {		
	    public void run() {		    	   
		    Long spentTime = System.currentTimeMillis() - mStartTime;
		    Long seconds = (spentTime/1000) % 60;
		    Log.d(TAG, "seconds: " + seconds);
		            
		    if((5 == seconds) && mIsFingerOn){			            	
		    	Display display = getActivity().getWindowManager().getDefaultDisplay();
		        Point size = new Point();
		        display.getSize(size);
		        final int width = size.x;				            	
		            	
		        ObjectAnimator oa = ObjectAnimator.ofFloat(mTextHint, "translationX", 0, width);
		        oa.setDuration(300);
		        oa.addListener(new AnimatorListenerAdapter() {
		        	@Override
		            public void onAnimationEnd(Animator animation) {
		        	    // do something when the animation is done
		        		mTextHint.setText(getString(R.string.remove_your_finger_plz));
		        		ObjectAnimator oa = ObjectAnimator.ofFloat(mTextHint, "translationX", width, 0);
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
	
	public void setEnrollId(String enrollID) {
		mEnrollID = enrollID;
	}
	/*
	public FPET310TFragment(String enrollID) {		
		mEnrollID = enrollID;					
	}	
	*/
	public void setPhase(String phase){
		mPhase = phase;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_tutorial_fpet310,
				container, false);		
		
		mTextHint = (TextView) rootView.findViewById(R.id.hint_title);	
		//mTextHint2 = (TextView) rootView.findViewById(R.id.hint_description2);
		mEnrollOKView = (TextView) rootView.findViewById(R.id.tv_enroll_OK);		
		mFingerGuide = (ImageView) rootView.findViewById(R.id.image_guide);
		mYu = new SettingLib(getActivity());
		mEnrollOKView.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {
				if(!mPhase.equals("learning")){
					/*
					if(mEnrollResult){
						FPET310TFragment fragment = new FPET310TFragment(mEnrollID);
						fragment.setPhase("learning");
						getFragmentManager().beginTransaction()
						.replace(R.id.container, fragment)
						.commit(); 
					}else{
					*/
						Intent returnIntend = new Intent(); 				
						getActivity().setResult(0, returnIntend);
						getActivity().finish();				
					//}
				}else{				
				  Intent returnIntend = new Intent(); 				
				  getActivity().setResult(0, returnIntend);
				  getActivity().finish();						
				}	
			}
		});
		return rootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();	
		mYu.setEnrollListener(this);
		mYu.setStatusListener(this);
		mYu.setVerifyLearningListener(this);
		//mYu.bind();
	}	
	
	@Override
	public void onPause(){		
		super.onPause();
		mYu.abort();    
		//mYu.unbind();
	}

	@Override
	public void onStop() {
		super.onStop();
		if(mAnimFrame != null){
		  mAnimFrame.stop();	
		}
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
		  ((FPET310Activity)getActivity()).doFinish();
		}
	}

	@Override
	public void onBadImage(int status) {
		mTextHint.setText(R.string.bad_image);
		mTextHint.setTextColor(Color.RED);
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
	public void onServiceConnected() {
		Log.d(TAG, "+++ onServiceConnected +++");		
		if(mPhase.equals("enroll")){
		  captureEnroll(mEnrollID);	
		}else if(mPhase.equals("learning")){
			//learning process
			mHandler.postDelayed(new Runnable(){
				@Override
				public void run(){					
					mYu.learningIdentify(mEnrollID);
					mIsLearning=true;
					Log.d(TAG, "+++++++++ call learningIdentify ++++++++++++");
				}
			}, 400);
		}
	}

	private void captureEnroll(String enrollKey){
		mYu.connectDevice();
		mYu.enroll(enrollKey);
		mIsEnrolling = true;
	}	
	
	@Override
	public void onServiceDisConnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onFingerFetch() {
		Log.d(TAG, "+++++ onFingerFetch +++++");
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
			updateUI(ET300Action.SHOW_REMOVE_FINGER);
		}else{
			updateUI(ET300Action.SHOW_PLACE_FINGER);
		}
	}

	@Override
	public void onFingerImageGetted() {	
		Log.d(TAG, "+++++ onFingerImageGetted +++++");
		mAnimCount++;
		updateUI(ET300Action.SHOW_REMOVE_FINGER);
	}

	@Override
	public void onUserAbort() {
		mIsEnrolling=false;
		if(mProgressDialog == null){
			((FPET310Activity)getActivity()).doFinish();
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
		if(FPCommon.STATUS_FINGER_ON == status){ // finger on
			mIsFingerOn = true;
			mStartTime = System.currentTimeMillis();						
			mHandler.removeCallbacks(updateTimer);
			mHandler.postDelayed(updateTimer, 1000);
			holdFinger();			
		}else if(FPCommon.STATUS_FINGER_OFF == status){
			mIsFingerOn = false;
			mHandler.removeCallbacks(updateTimer);
			if(mTextHint.getText().equals(getString(R.string.remove_your_finger_plz))){
				
				Display display = getActivity().getWindowManager().getDefaultDisplay();
        		Point size = new Point();
        		display.getSize(size);
        		final int width = size.x;
        		//final int height = size.y;						
				
				ObjectAnimator oa = ObjectAnimator.ofFloat(mTextHint, "translationX", 0, width);
				oa.setDuration(300);
				oa.addListener(new AnimatorListenerAdapter(){
					@Override
					public void onAnimationEnd(Animator animation){
						mTextHint.setText(R.string.place_your_finger);
						ObjectAnimator oa = ObjectAnimator.ofFloat(mTextHint, "translationX", width, 0);
						oa.setDuration(300);
						oa.start();
					}
				});
				oa.start();
				
			}
		}else if(FPCommon.STATUS_WAIT_FINGER_ON == status){
			updateUI(ET310Action.SHOW_PLACE_FINGER);
			if(mPhase.equals("enroll")){
			  runEnrollAnimation();		
			}else{
			  runLearningAnimation();
			}
		}
	}

	private void doExit(){
		mIsEnrolling=false;
		mIsLearning=false;
		//mImgLearningStatus.setImageBitmap(null);
		//mImgLearningStatus.postInvalidate();
		mEnrollOKView.setVisibility(View.VISIBLE);			
		mEnrollOKView.setEnabled(true);	
		mFingerGuide.setBackgroundResource(R.drawable.fp_310_continue);
		updateUI(ET300Action.SHOW_PLACE_FINGER);
	}	
	
	private void updateUI(int status){
		if(status == ET300Action.SHOW_PLACE_FINGER){		
			mTextHint.setText(R.string.place_your_finger);
			mTextHint.setTextColor(Color.WHITE);	
		}else if(status == ET300Action.SHOW_REMOVE_FINGER){
			mTextHint.setText(R.string.remove_your_finger);
			mTextHint.setTextColor(Color.GREEN);	
		}else if(status == ET300Action.SHOW_HOLD_FINGER){
			mTextHint.setText(R.string.hold_your_finger);
			mTextHint.setTextColor(Color.WHITE);
		}
		mTextHint.postInvalidate();		
	}	
	
	private void runEnrollAnimation(){
		Log.d(TAG, "+++++ runFingerAnimation +++++");
		if(mAnimFrame != null){
			mAnimFrame.stop();
		}		
				
		if(mAnimCount < 4){			
		  mFingerGuide.setBackgroundResource(R.drawable.fp_animation_center_310);			
		}else{					
			switch(mAnimCount%4){
				case 0:
				case 1:
					mFingerGuide.setBackgroundResource(R.drawable.fp_animation_up_310);
					break;
				case 2:
				case 3:
					mFingerGuide.setBackgroundResource(R.drawable.fp_animation_down_310);
					break;
			
			}
		}
		mAnimFrame = (AnimationDrawable) mFingerGuide.getBackground();
		mAnimFrame.start();	
	}
	
	private void holdFinger(){
		updateUI(ET300Action.SHOW_HOLD_FINGER);
		if(mPhase.equals("enroll")){
			if(mAnimCount <4){
				mFingerGuide.setBackgroundResource(R.drawable.fp_310_01_02_p);				
			}else{
				switch(mAnimCount%4){
					case 0:
					case 1:
						mFingerGuide.setBackgroundResource(R.drawable.fp_310_up_p);
						break;					
					case 2:
					case 3:
						mFingerGuide.setBackgroundResource(R.drawable.fp_310_down_p);
						break;
				}
			}
		}else{
			switch(learningCnt%5){
				case 0:		
					mFingerGuide.setBackgroundResource(R.drawable.fp_310_01_02_p);
					break;
				case 1:
					mFingerGuide.setBackgroundResource(R.drawable.fp_310_02_02_p);
					break;
				case 2:
					mFingerGuide.setBackgroundResource(R.drawable.fp_310_03_02_p);
					break;
				case 3:
					mFingerGuide.setBackgroundResource(R.drawable.fp_310_04_02_p);
					break;
				case 4:
					mFingerGuide.setBackgroundResource(R.drawable.fp_310_05_02_p);
					break;	
				default:
					break;	
		}			
		}
		mFingerGuide.postInvalidate();		
	}
	
	@Override
	public void onSuccess() {
		Log.d(TAG, "+++++ onSuccess +++++");
		//mEnrollResult = true;
		doExit();	  		 		 		 
	}

	@Override
	public void onFail() {
		Log.d(TAG, "+++ onFail +++");		
		//mEnrollStatus.setText(R.string.enrol_failed);
		mEnrollOKView.setVisibility(View.VISIBLE);			
		mEnrollOKView.setEnabled(true);
		mFingerGuide.setBackgroundResource(R.drawable.fp_310_fail);
		//mEnrollResult = false;
		mIsEnrolling = false;		
		updateUI(ET300Action.SHOW_PLACE_FINGER);
		//mEnrollResult = false;		
	}

	@Override
	public void onProgress() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void LearningScore(int score) {		
		Log.d(TAG, "learning score="+score);
				
		/*
		 * 
		 * Enroll learning implementation
		 */
		
		//show UI
		if(score <= LEARNING_THRESHOLD){
			learningCnt++;
			mYu.learning();
		}else{
			dupCnt++;						
		}	
		
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run(){
				if((dupCnt+learningCnt) == DUP_VERIFY_MAX || learningCnt == LEARNING_MAX){
					doExit();										
				}else{					   			 
				    mYu.learningIdentify(mEnrollID);
				    mIsLearning=true;
				}				
			}
		}, 200);		
	}	
	
	private void runLearningAnimation(){
		if(mAnimFrame != null){
			mAnimFrame.stop();
		}			
		switch(learningCnt%5){
			case 0:
				mFingerGuide.setBackgroundResource(R.drawable.fp_animation_learn_center);
				break;
			case 1:
				mFingerGuide.setBackgroundResource(R.drawable.fp_animation_learn_right_south);
				break;
			case 2:
				mFingerGuide.setBackgroundResource(R.drawable.fp_animation_learn_right);
				break;
			case 3:				
				mFingerGuide.setBackgroundResource(R.drawable.fp_animation_learn_left_south);
				break;
			case 4:
				mFingerGuide.setBackgroundResource(R.drawable.fp_animation_learn_left);
				break;
		}		
		mAnimFrame = (AnimationDrawable) mFingerGuide.getBackground();
		mAnimFrame.start();	
	}
}
