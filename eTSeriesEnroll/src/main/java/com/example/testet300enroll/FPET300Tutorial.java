package com.example.testet300enroll;

import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class FPET300Tutorial implements ET300Action{
	private static final String TAG = "FPET300Tutorial";
	//private static final int ADD_OVAL_BITMAP_COUNT = 6;
	//private static final int ANIMATION_DURATION = 200; // milliseconds
	//private static final int DILATION_THICKNESS = 10;
	//private static final byte WHITE = -1; 
	//private static final byte BLACK = 0; 
	//private static final int MAX_ENROLL_COUNT = 12;

	//private static final int DISPLAYMAX_W = 380;
	//private static final int DISPLAYMAX_H = 380;
	//private static final int ENROLLMAP_W  = 512;
	//private static final int ENROLLMAP_H  = 512;
	
	private FPET300Activity mEnrollActivity;
	//private ArrayList<byte[]> mEnrollMapList;	
	//private ImageView mEnrollView;
	private ImageView mFingerGuide;	
	private TextView mTextHint;
	private TextView mTextHint2;
	private TextView mEnrollOKView;
	
	//private Bitmap mBeforeDilateBmp;
	//private Bitmap mEnrollBackgroundBmp;
	//private Bitmap mEnrollFrontBmp;
	//private byte[] mFakeEnrollMap; 	
	//private byte[] mLastEnrollMap;
	//private byte[] mDilatedEnrollMap;

	//private boolean mRealEnrollMapCame;
	//private int mEnrollCount;
	//private int mDilationThickness;
		
	private int mAnimCount;
	private AnimationDrawable mAnimFrame; 

	//private Bitmap mScaledBaseBitmap;
	//private Bitmap mScaledFrontBmp;
	//private int[] mScaledFrontPixels = new int[DISPLAYMAX_W*DISPLAYMAX_H];
	
	
	public FPET300Tutorial(final FPET300Activity enrollActivity){
		mEnrollActivity = enrollActivity;
	}
	
	@Override
	public int getLayoutID(){
		return R.layout.fp_enroll_tutorial;
	}
	
	@Override
	public void initial(){		
		//initial parameter
		//mEnrollMapList = new ArrayList<byte[]>();
		//mEnrollBackgroundBmp = BitmapFactory.decodeResource(mEnrollActivity.getResources(), R.drawable.fp_square_gray);
		//Bitmap enrollFrontBmp = BitmapFactory.decodeResource(mEnrollActivity.getResources(), R.drawable.fp_square_green);
		
		//mScaledBaseBitmap = Bitmap.createScaledBitmap(mEnrollBackgroundBmp, DISPLAYMAX_W, DISPLAYMAX_H, false);			
		//mScaledFrontBmp   = Bitmap.createScaledBitmap(enrollFrontBmp, DISPLAYMAX_W, DISPLAYMAX_H, false);
		//mScaledFrontBmp.getPixels(mScaledFrontPixels, 0, DISPLAYMAX_W, 0, 0, DISPLAYMAX_W, DISPLAYMAX_H);
		
		//mEnrollView = (ImageView) mEnrollActivity.findViewById(R.id.fingerprint);		
		mTextHint = (TextView) mEnrollActivity.findViewById(R.id.hint_title);	
		mTextHint2 = (TextView) mEnrollActivity.findViewById(R.id.hint_description2);
		mEnrollOKView = (TextView) mEnrollActivity.findViewById(R.id.tv_enroll_OK_test);		
		mFingerGuide = (ImageView) mEnrollActivity.findViewById(R.id.image_guide);
		/*
		mLastEnrollMap = new byte[ENROLLMAP_W*ENROLLMAP_H];
		Arrays.fill(mLastEnrollMap, WHITE);				
		mDilatedEnrollMap = new byte[ENROLLMAP_H];
		Arrays.fill(mDilatedEnrollMap, WHITE);			
		*/
		//runFingerAnimation(R.drawable.animation_center);					
		//createFakeEnrollMap();		
	}
	/*
	private void createFakeEnrollMap(){
		
		mFakeEnrollMap = new byte[ENROLLMAP_W*ENROLLMAP_H];
		Arrays.fill(mFakeEnrollMap, WHITE);				
			
		//draw a 128*128 rect mask in 512*512
		int idx;
		int thickness = 191;
		for(int y=thickness; y<thickness+128 ; y++){
			idx = ENROLLMAP_W*y;
			for(int x=0 ; x<128 ; x++)				
				mFakeEnrollMap[idx+thickness+x] = BLACK; // 0:black					 
		}
		
	}
	*/
	/*
	private void setEnrollOKAnimationParameter(AnimationDrawable animEnrollOK) {		
		animEnrollOK.setOneShot(true);				
		
		mEnrollView.setImageDrawable(animEnrollOK);
		
		animEnrollOK.setCallback(new AnimationDrawableCallback(animEnrollOK, mEnrollView){
			 
			@Override
			 public void onAnimationComplete(){
				 mEnrollOKView.setVisibility(View.VISIBLE);
				 mEnrollOKView.setEnabled(true);
				 mEnrollActivity.showEnrollOK();
			 }
		});
		 			
	}
	*/
	/*
	private void addEnrollOKAnimationBitmap(AnimationDrawable animEnrollOK) {
		
		//initial
		OvalMaskFactory ovalFactory = new OvalMaskFactory();
		ovalFactory.setDiff(OvalMaskFactory.ENROLL_OK_DIFF);		
		
		int w = getCurrentEnrollBmp().getWidth();
		//int h = getCurrentEnrollBmp().getHeight();
				
		//add oval bmp
		for(int i=0; i<ADD_OVAL_BITMAP_COUNT; i++){			
			Bitmap progressBmp = Util.composeBitmap(w, h, getCurrentEnrollBmp(), ovalFactory.drawOval(w, h), mScaledFrontPixels.clone());
			BitmapDrawable bitmapDrawable = new BitmapDrawable(mEnrollActivity.getResources(), progressBmp);
			animEnrollOK.addFrame(bitmapDrawable, ANIMATION_DURATION);			
		}
		
	}
	*/
	@Override
	public void runFingerAnimation(){
		
		if(mAnimFrame != null){
			mAnimFrame.stop();
		}		
		if(mAnimCount < 4){
			mFingerGuide.setBackgroundResource(R.drawable.fp_animation_center);
		}else{					
			switch(mAnimCount%4){
				case 0:		
					if(4 == mAnimCount){
						mFingerGuide.setBackgroundResource(R.drawable.fp_animation_right);
					}else{
						mFingerGuide.setBackgroundResource(R.drawable.fp_animation_right2);
					}	
					break;
				case 1:
					mFingerGuide.setBackgroundResource(R.drawable.fp_animation_up);
					break;
				case 2:
					mFingerGuide.setBackgroundResource(R.drawable.fp_animation_left);
					break;
				case 3:
					mFingerGuide.setBackgroundResource(R.drawable.fp_animation_down);
					break;				
			}
		}
					
		mAnimFrame = (AnimationDrawable) mFingerGuide.getBackground();
		mAnimFrame.start();		
		
		//mAnimCount++;
	}	
	
	@Override
	public void showEnrollOK() {
		/*
		final AnimationDrawable animEnrollOK = new AnimationDrawable();
		addEnrollOKAnimationBitmap(animEnrollOK);
		setEnrollOKAnimationParameter(animEnrollOK);
		animEnrollOK.start();
		*/		
		mEnrollOKView.setVisibility(View.VISIBLE);
		mTextHint.setTextColor(Color.WHITE);
		mTextHint2.setVisibility(View.GONE);
		mEnrollOKView.setEnabled(true);
		mEnrollActivity.showEnrollOK();		
		mFingerGuide.setBackgroundResource(R.drawable.fp_complete);		
		mFingerGuide.setClickable(true);
		mFingerGuide.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {				
				mEnrollActivity.doFinish();
			}
		});
	}
	/*
	private void showEnrollMap(byte[] enrollMap){
		if(enrollMap == null){
			Log.e(TAG, "the enrollMap is null");
			return;		
		}

		if(mEnrollCount == 0)
			System.arraycopy(enrollMap, 0, mLastEnrollMap, 0, enrollMap.length);			
		else		
			mDilationThickness += DILATION_THICKNESS;					
				
		//mBeforeDilateBmp = getCurrentEnrollBmp(); // for bad image recover use				
		Bitmap maskBmp = dilation(enrollMap, ENROLLMAP_W, ENROLLMAP_H, mDilationThickness);	
		Bitmap enrollBmp = Util.composeBitmap(DISPLAYMAX_W, DISPLAYMAX_H,
				  							  mScaledBaseBitmap, maskBmp, 
				  							  mScaledFrontPixels.clone());
		setEnrollBmpToView(enrollBmp);	
		maskBmp.recycle();
	}		
	*/
	@Override
	public void showBadProgress(){
		Log.d(TAG, "showBadprogress");
		
		mTextHint.setText("Bad Image");
		mTextHint.setTextColor(Color.RED);	
		
		//mEnrollCount--;	
		//if(mDilationThickness > 0)
		//	mDilationThickness -= DILATION_THICKNESS;		
																
		//setEnrollBmpToView(mBeforeDilateBmp);	  		
	}
	/*
	private void setEnrollBmpToView(Bitmap bmp){
		
		mEnrollView.setImageBitmap(bmp);
	  	mEnrollView.postInvalidate();
	  	
	}	
	*/
	/*
	private Bitmap getCurrentEnrollBmp(){
		
		BitmapDrawable drawable = (BitmapDrawable)mEnrollView.getDrawable();
		if(drawable == null) return mEnrollBackgroundBmp;					
		return drawable.getBitmap();
	}
	*/
	@Override
	public synchronized void updateUI(int status){	
		Log.d(TAG, "updateUI");				
		//if(mEnrollCount > (MAX_ENROLL_COUNT - 1)) return;	
		
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
		//runFingerAnimation();		
		
		//if(!mFingerGuide.isShown()){
		//	mAnimFrame.stop();
		//}		
		//moveFingerAnimation();							
	}

	//private boolean mIsShowMaping;	
	@Override
	public void addEnrollMap(byte[] map, int w, int h) {		
		Log.d(TAG, "+++++addEnrollMap++++++++ real enrollMap come in");	
		/*
		mEnrollMapList.add(map);
		if (mIsShowMaping) return;
		mIsShowMaping = true; 
		mRealEnrollMapCame = true;
		while(mEnrollMapList.size()>0){
			byte[] eMap = mEnrollMapList.remove(0);		
			Util.mirrorContenrt(eMap);			
			System.arraycopy(eMap, 0, mLastEnrollMap, 0, eMap.length);	
			showEnrollMap(eMap);
		}
		mIsShowMaping = false;
		*/ 
	}
	/*	
    private Bitmap dilation(byte[] img, int w, int h, int thickness){
    	//Log.d(TAG, "dilation4    w: " + w + "h: " + h + "  thickness: " + thickness);    	
    	int newW = DISPLAYMAX_W+(2 * thickness);
    	int newH = DISPLAYMAX_H+(2 * thickness);  	    	
    	    	
    	Bitmap mask = Util.createMask(ENROLLMAP_H, ENROLLMAP_W, img); 
    	Bitmap scaleBmp = Bitmap.createScaledBitmap(mask, newW, newH, false);   

    	return (Bitmap.createBitmap(scaleBmp, thickness, thickness, DISPLAYMAX_W, DISPLAYMAX_H)); //drop scaleBmp
    }
    */
    @Override
	public synchronized void goodProgress(){
		Log.d(TAG, "goodProgress~~~~~~~~~~~~~~~~~~~~~~~~~~");
		/*
		if(mIsShowMaping) return;					
				
		if( mRealEnrollMapCame || isSetFakeEnrollMapToView()){
			Log.d(TAG, "++++++from mLastEnrollMap+++++++++++");
			showEnrollMap(mLastEnrollMap);					
			return;
		}
				
		Log.d(TAG, "+++++++++++from mFakeEnrollMap++++++++++");
		showEnrollMap(mFakeEnrollMap);
		*/
	}		
	
    @Override
    public void addEnrollProgress(){
    	//mEnrollCount++;		
    	mAnimCount++;
    }
    /*
	private boolean isSetFakeEnrollMapToView(){
		return (mEnrollCount > 0);
	}
	*/
	@Override
	public void stopEnrollGuideAnimation() {
		if(null == mAnimFrame) return;
		mAnimFrame.stop();		
	}
	
	@Override
	public void holdFinger(){
				
		updateUI(ET300Action.SHOW_HOLD_FINGER);
		if(mAnimCount <4){
			mFingerGuide.setBackgroundResource(R.drawable.fp_m_p_2);				
		}else{				
			switch(mAnimCount%4){
				case 0:		
					mFingerGuide.setBackgroundResource(R.drawable.fp_r_p_2);
					break;
				case 1:
					mFingerGuide.setBackgroundResource(R.drawable.fp_t_p_2);
					break;
				case 2:
					mFingerGuide.setBackgroundResource(R.drawable.fp_l_p_2);
					break;
				case 3:
					mFingerGuide.setBackgroundResource(R.drawable.fp_d_p_2);
					break;
			}
		}
		mFingerGuide.postInvalidate();		
	}

	@Override
	public void removeFinger() {	
		/*
		if(mAnimCount <4){
			mFingerGuide.setBackgroundResource(R.drawable.fp_m_up);
		}else{				
			switch(mAnimCount%4){
				case 0:		
					mFingerGuide.setBackgroundResource(R.drawable.fp_r_up);
					break;
				case 1:
					mFingerGuide.setBackgroundResource(R.drawable.fp_t_up);
					break;
				case 2:
					mFingerGuide.setBackgroundResource(R.drawable.fp_l_up);
					break;
				case 3:
					mFingerGuide.setBackgroundResource(R.drawable.fp_d_up);
					break;
			}
		}
		mFingerGuide.postInvalidate();
		*/			
	}
	
}
