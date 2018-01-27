package com.example.testet300enroll;

import java.nio.ByteBuffer;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NavUtils;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import egistec.fingerauth.api.FPAuthListeners;
import egistec.fingerauth.api.SettingLib;

public class FPET310LearningFragment extends Fragment implements ET310Action,
  FPAuthListeners.StatusListener, FPAuthListeners.VerifyLearningListener, 
    FPAuthListeners.ThreadImageListener{
	
	public static final String TAG = "FPET310LearningFragment";
	private static final int DUP_VERIFY_MAX = 30;	
	private static final int LEARNING_MAX = 8;
	private static final int TIMER_DELAY = 200;
	private static final int LEARNING_THRESHOLD = 80;
	private String mEnrollID;
	private SettingLib mYu;
	private int dupCnt, learningCnt;
	private TextView mTxtLearningStatus;
	private ImageView mEnrollFrame;
	private ImageView mImgLearningStatus;
	private Button mEnrollOKView;		
	private ProgressDialog mProgressDialog;
	private boolean mIsLearning;
	
	private boolean mIsFingerOn;
	private long mStartTime;
	private TextView mHintTitleView;
	private Handler mHandler = new Handler(Looper.getMainLooper());
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
	
	public FPET310LearningFragment(String enrollID) {
		mEnrollID = enrollID;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_learning_fpet310,
				container, false);
		mYu = new SettingLib(getActivity());
		mTxtLearningStatus = (TextView) rootView.findViewById(R.id.txt_learning_status);
		mEnrollFrame = (ImageView) rootView.findViewById(R.id.enroll_frame);
		mImgLearningStatus = (ImageView) rootView.findViewById(R.id.learning_status);
		mEnrollOKView = (Button) rootView.findViewById(R.id.tv_enroll_OK);
		mHintTitleView = (TextView) rootView.findViewById(R.id.hint_title);
		mEnrollOKView.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View v) {				
				Intent returnIntend = new Intent(); 				
				getActivity().setResult(0, returnIntend);
				getActivity().finish();				
			}
		});
		return rootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		mYu.setVerifyLearningListener(this);
		mYu.setStatusListener(this);	
		mYu.setThreadImageListener(this);
		//mYu.bind();
	}
	
	@Override 
	public void onPause(){
		super.onPause();
		mYu.abort();    				
		//mYu.unbind();
	}	

	@Override
	public void LearningScore(final int score) {
		Log.d(TAG, "learning score="+score);
		/*
		 *  Enroll Learning implementation
		 */
		//show UI
		if(score <= LEARNING_THRESHOLD){
			learningCnt++;
			updateLearningUI(R.string.learning_success, 
			  R.drawable.learning_2);
			mYu.learning();
		}else{
			dupCnt++;
			updateLearningUI(R.string.learning_adjust, 
			  R.drawable.adjust_finger_position);
		}	
		
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run(){
				if((dupCnt+learningCnt) == DUP_VERIFY_MAX || learningCnt == LEARNING_MAX){
					doExit();										
				}else{	
				    updateLearningUI(0, 0);			  
				    mYu.learningIdentify(mEnrollID);
				    mIsLearning=true;
				}				
			}
		}, TIMER_DELAY);			
	}

	private void doExit(){
		mIsLearning=false;
		mTxtLearningStatus.setText("");
		mTxtLearningStatus.postInvalidate();
		mImgLearningStatus.setImageBitmap(null);
		mImgLearningStatus.postInvalidate();
		mEnrollOKView.setVisibility(View.VISIBLE);			
		mEnrollOKView.setEnabled(true);	
	}

	@Override
	public void onBadImage(int status) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onServiceConnected() {
		Log.d(TAG, "+++++ onServiceConnected +++++");
		mHandler.postDelayed(new Runnable(){
			@Override
			public void run(){
				mYu.connectDevice();
				mYu.learningIdentify(mEnrollID);
				mIsLearning=true;
			}
		}, TIMER_DELAY);	
	}

	@Override
	public void onServiceDisConnected() {
		// TODO Auto-generated method stub
		
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
		updateEnrollTitle(R.string.remove_your_finger);		
	}

	@Override
	public void onUserAbort() {
		Log.d(TAG, "++++ onUserAbort +++++");
		
		if(mProgressDialog == null){
			if(getActivity() instanceof FPET310Activity){
			  ((FPET310Activity)getActivity()).doFinish();
			}else if(getActivity() instanceof FPET310LearningActivity){
			  ((FPET310LearningActivity)getActivity()).doFinish();
			}
			return;
		}		
		if(mProgressDialog.isShowing()){
			mProgressDialog.dismiss();
			NavUtils.navigateUpFromSameTask(getActivity());					
		}		
	}

	@Override
	public void onStatus(int status) {
		Log.d(TAG, "onStatus status="+status);
		
		if(FPCommon.STATUS_FINGER_ON == status){
			mIsFingerOn = true;
			mStartTime = System.currentTimeMillis();
			mHandler.removeCallbacks(updateTimer);
			mHandler.postDelayed(updateTimer, 1000);
		}else if(FPCommon.STATUS_FINGER_OFF == status){
			mIsFingerOn = false;		
			mHandler.removeCallbacks(updateTimer);
			
			if(mHintTitleView == null || getActivity() == null) return;
			
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
		
	private void updateEnrollTitle(int resId){
		mHintTitleView.setText(resId);
		if(resId == R.string.place_your_finger){
			mHintTitleView.setTextColor(Color.WHITE);
		}else if(resId == R.string.remove_your_finger){
			mHintTitleView.setTextColor(Color.GREEN);
		}
		mHintTitleView.postInvalidate();
	}
	
	private void updateLearningUI(int txtRes, int imgRes){
		
		if(txtRes == 0 && imgRes == 0){
			mTxtLearningStatus.setText("");
			mImgLearningStatus.setImageBitmap(null);
		}else{		
			mTxtLearningStatus.setText(txtRes);
			mImgLearningStatus.setImageResource(imgRes);
		}
		mTxtLearningStatus.postInvalidate();
		mImgLearningStatus.postInvalidate();
	}
	
	private void doAbort() {
		mProgressDialog = ProgressDialog
		  .show(getActivity(), "", "Learning aborting...", true, false);			
		if(!mYu.abort()){
			if(mProgressDialog.isShowing()){
				mProgressDialog.dismiss();
				NavUtils.navigateUpFromSameTask(getActivity());					
			}	
		}
	}

	@Override
	public void onKeyDown() {		
		if(mIsLearning){
		  doAbort();
		}else{
			NavUtils.navigateUpFromSameTask(getActivity());		
		}
	}

	@Override
	public void onGetImg(byte[] img, int width, int height) {
		Log.d(TAG, "onGetImg width="+width + " height="+height);		
		Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
		// reverse color
		for(int index = 0; index < img.length; index ++) {
			img[index] = (byte) ~img[index];
		}		
		bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(img));		
		mEnrollFrame.setImageBitmap(Bitmap.createScaledBitmap(bitmap, 
		  mEnrollFrame.getWidth(), mEnrollFrame.getHeight(), false));
		mEnrollFrame.postInvalidate();		
	}
}
