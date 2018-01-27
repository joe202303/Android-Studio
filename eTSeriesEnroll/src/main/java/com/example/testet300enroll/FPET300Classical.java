package com.example.testet300enroll;

import java.util.ArrayList;
import java.util.Arrays;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;


public class FPET300Classical  implements ET300Action{
	private static final String TAG = "EnrollView4";
	private static final int ADD_OVAL_BITMAP_COUNT = 6;
	private static final int ANIMATION_DURATION = 200; // milliseconds
	private static final int DILATION_THICKNESS = 15;
	private static final byte WHITE = -1; 
	private static final byte BLACK = 0; 
	private static final int MAX_ENROLL_COUNT = 12;
	
	private static final int DISPLAYMAX_W = 380;
	private static final int DISPLAYMAX_H = 380;
	private static final int ENROLLMAP_W  = 512;
	private static final int ENROLLMAP_H  = 512;
		
	private FPET300Activity mEnrollActivity;
	private ArrayList<byte[]> mEnrollMapList;	
	private ImageView mEnrollView;
	private ImageView mFingerGuide;	
	private TextView mTextHint;
	private TextView mTextHint2;
	private TextView mEnrollOKView;
	
	private Bitmap mEnrollBackgroundBmp;
	private Bitmap mBeforeDilateBmp;
	private byte[] mFakeEnrollMap;
	private byte[] mLastEnrollMap;
	private byte[] mDilatedEnrollMap;
	
	private int mEnrollCount;
	private int mDilationThickness;
	private boolean mRealEnrollMapCame;
		
	private Bitmap mScaledBaseBitmap;
	private Bitmap mScaledFrontBmp;
	private int[] mScaledFrontPixels = new int[DISPLAYMAX_W*DISPLAYMAX_H];	
	/*
	private int[] animationFrames = { R.drawable.fp_00_200, R.drawable.fp_01_500, R.drawable.fp_02_300,
									  R.drawable.fp_03_200, R.drawable.fp_04_200, R.drawable.fp_05_500, 
									  R.drawable.fp_06_300, R.drawable.fp_07_200, R.drawable.fp_08_200,
									  R.drawable.fp_09_500, R.drawable.fp_10_300, R.drawable.fp_11_200,
									  R.drawable.fp_12_200, R.drawable.fp_13_500, R.drawable.fp_14_300,
									  R.drawable.fp_15_200, };
	
	private int[] mFramedelays = { 200, 500, 300,
								   200, 200, 500,
								   300, 200, 200,
								   500, 300, 200,
								   200, 500, 300,
								   200,};
	*/
	//private FramesSequenceAnimation mAnim; 
	private AnimationDrawable mAnim ; 
	
	public FPET300Classical(final FPET300Activity enrollActivity) {
		mEnrollActivity = enrollActivity;		
	}
		
	@Override
	public int getLayoutID(){
		return R.layout.fp_enroll_classical;
	}
	
	@Override
	public void initial(){		
		//initial parameter
		mEnrollMapList = new ArrayList<byte[]>();
		mEnrollBackgroundBmp = BitmapFactory.decodeResource(mEnrollActivity.getResources(), R.drawable.fp_square_gray);
		Bitmap enrollFrontBmp = BitmapFactory.decodeResource(mEnrollActivity.getResources(), R.drawable.fp_square_green);
		
		mScaledBaseBitmap = Bitmap.createScaledBitmap(mEnrollBackgroundBmp, DISPLAYMAX_W, DISPLAYMAX_H, false);			
		mScaledFrontBmp   = Bitmap.createScaledBitmap(enrollFrontBmp, DISPLAYMAX_W, DISPLAYMAX_H, false);
		mScaledFrontBmp.getPixels(mScaledFrontPixels, 0, DISPLAYMAX_W, 0, 0, DISPLAYMAX_W, DISPLAYMAX_H);
		
		mEnrollView = (ImageView) mEnrollActivity.findViewById(R.id.fingerprint);		
		mTextHint = (TextView) mEnrollActivity.findViewById(R.id.hint_title);	
		mTextHint2 = (TextView) mEnrollActivity.findViewById(R.id.hint_description2);
		mEnrollOKView = (TextView) mEnrollActivity.findViewById(R.id.tv_enroll_OK_test);		
		mFingerGuide = (ImageView) mEnrollActivity.findViewById(R.id.image_guide);
		
		mLastEnrollMap = new byte[ENROLLMAP_W*ENROLLMAP_H];
		Arrays.fill(mLastEnrollMap, WHITE);				
		mDilatedEnrollMap = new byte[ENROLLMAP_H];
		Arrays.fill(mDilatedEnrollMap, WHITE);	
		
		runFingerToouchAnimation();					
		createFakeEnrollMap();		
	}
	
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
	
	private void runFingerToouchAnimation(){
		
		mFingerGuide.setBackgroundResource(R.drawable.fp_animation_guide);
		
		mAnim = (AnimationDrawable) mFingerGuide.getBackground();
		mAnim.start();
		
		/*
		mAnim = new FramesSequenceAnimation(mFingerGuide, 
                animationFrames,
                mFramedelays);
		mAnim.setOneShot(false);	
		mAnim.setCallback(new FramesSequenceAnimation.OnAnimationStoppedListener(){
			@Override
			public void AnimationStopped(){
				Log.d(TAG, "+++AnimationStopped+++");
				//mNextBtn.setText("continue");
			}
		});
		mAnim.start();
		*/		
	}
	
	private void setEnrollOKAnimationParameter(AnimationDrawable animEnrollOK) {		
		animEnrollOK.setOneShot(true);				
		mEnrollView.setImageDrawable(animEnrollOK);
		animEnrollOK.setCallback(new FPAnimationDrawableCallback(animEnrollOK, mEnrollView){
			 
			@Override
			 public void onAnimationComplete(){
				 mEnrollOKView.setVisibility(View.VISIBLE);
				 mEnrollOKView.setEnabled(true);
				 mTextHint.setTextColor(Color.WHITE);
				 mTextHint2.setVisibility(View.GONE);
				 mEnrollActivity.showEnrollOK();
			 }
		});			
	}
	
	private void addEnrollOKAnimationBitmap(AnimationDrawable animEnrollOK) {		 		
		//initial
		FPOvalMaskFactory ovalFactory = new FPOvalMaskFactory();
		ovalFactory.setDiff(FPOvalMaskFactory.ENROLL_OK_DIFF);		
		int w = getCurrentEnrollBmp().getWidth();
		int h = getCurrentEnrollBmp().getHeight();
				
		//add oval bmp
		for(int i=0; i<ADD_OVAL_BITMAP_COUNT; i++){			
			Bitmap progressBmp = FPUtil.composeBitmap(w, h, getCurrentEnrollBmp(), ovalFactory.drawOval(w, h), mScaledFrontPixels.clone());
			BitmapDrawable bitmapDrawable = new BitmapDrawable(mEnrollActivity.getResources(), progressBmp);
			animEnrollOK.addFrame(bitmapDrawable, ANIMATION_DURATION);			
		}
		
	}

	@Override
	public void stopEnrollGuideAnimation(){
		if(null == mAnim){
			mFingerGuide.setImageBitmap(null);
			return;
		}
		mAnim.stop();	
		mFingerGuide.setImageBitmap(null);
		
		Drawable drawable = mFingerGuide.getDrawable();
		if(drawable == null) return;		
		((BitmapDrawable)drawable).setCallback(null);
		
	}

	@Override
	public void showEnrollOK() {
		
		final AnimationDrawable animEnrollOK = new AnimationDrawable();
		addEnrollOKAnimationBitmap(animEnrollOK);
		setEnrollOKAnimationParameter(animEnrollOK);
		animEnrollOK.start();
			
	}
	
	private void showEnrollMap(byte[] enrollMap){		
		if(enrollMap == null){
			Log.e(TAG, "the enrollMap is null");
			return;		
		}		
		if(mEnrollCount == 0)
			System.arraycopy(enrollMap, 0, mLastEnrollMap, 0, enrollMap.length);			
		else		
			mDilationThickness += DILATION_THICKNESS;					
			
		mBeforeDilateBmp = getCurrentEnrollBmp(); // for bad image recover use			
		Bitmap maskBmp = dilation(enrollMap, mDilationThickness);			
		Bitmap enrollBmp = FPUtil.composeBitmap(DISPLAYMAX_W, DISPLAYMAX_H,
											  mScaledBaseBitmap, maskBmp, 
											  mScaledFrontPixels.clone());

		setEnrollBmpToView(enrollBmp);		
		maskBmp.recycle();
	}		

	@Override
	public void showBadProgress(){
		Log.d(TAG, "showBadprogress");

		mTextHint.setText("Bad Image");
		mTextHint.setTextColor(Color.GREEN);	
		mTextHint.postInvalidate();

		mEnrollCount--;	
		if(mDilationThickness > 0)
			mDilationThickness -= DILATION_THICKNESS;		
														
		setEnrollBmpToView(mBeforeDilateBmp);	 
	}

	private void setEnrollBmpToView(Bitmap bmp){
		mEnrollView.setImageBitmap(bmp);
	  	mEnrollView.postInvalidate();	  
	}	
	
	private Bitmap getCurrentEnrollBmp(){
		
		BitmapDrawable drawable = (BitmapDrawable)mEnrollView.getDrawable();
		if(drawable == null) return mEnrollBackgroundBmp;					
		return drawable.getBitmap();
	}
	
	@Override
	public synchronized void updateUI(int status){	
		Log.d(TAG, "updateUI");
		if(mEnrollCount > (MAX_ENROLL_COUNT - 1)) return;	
		
		if(status == ET300Action.SHOW_PLACE_FINGER){		
			mTextHint.setText(R.string.place_your_finger);
			mTextHint.setTextColor(Color.WHITE);	
		}else if(status == ET300Action.SHOW_REMOVE_FINGER){
			mTextHint.setText(R.string.remove_your_finger);
			mTextHint.setTextColor(Color.GREEN);	
		}
		mTextHint.postInvalidate();
		
		if(!mFingerGuide.isShown()){
			mAnim.stop();
		}		
							
	}

	private boolean mIsShowMaping;
	@Override
	public void addEnrollMap(byte[] map, int w, int h) {
		Log.d(TAG, "+++++addEnrollMap++++++++ real enrollMap come in");	
		mEnrollMapList.add(map);
		if (mIsShowMaping) return;
		mIsShowMaping = true; 
		mRealEnrollMapCame = true;
		while(mEnrollMapList.size()>0){
			byte[] eMap = mEnrollMapList.remove(0);
			FPUtil.mirrorContent(eMap);	
			System.arraycopy(eMap, 0, mLastEnrollMap, 0, eMap.length);	
			showEnrollMap(eMap);
		}
		mIsShowMaping = false; 
	}
		

    private Bitmap dilation(byte[] img, int thickness){
    	//Log.d(TAG, "dilation4    w: " + w + "h: " + h + "  thickness: " + thickness);    	
    	int newW = DISPLAYMAX_W+(2 * thickness);
    	int newH = DISPLAYMAX_H+(2 * thickness);  	    	
    	    	
    	Bitmap mask = FPUtil.createMask(ENROLLMAP_H, ENROLLMAP_W, img); 
    	Bitmap scaleBmp = Bitmap.createScaledBitmap(mask, newW, newH, false);   

    	return (Bitmap.createBitmap(scaleBmp, thickness, thickness, DISPLAYMAX_W, DISPLAYMAX_H)); //drop scaleBmp
    }
    
    @Override
	public synchronized void goodProgress(){
		Log.d(TAG, "goodProgress~~~~~~~~~~~~~~~~~~~~~~~~~~");
		
		if(mIsShowMaping) return;					
				
		if( mRealEnrollMapCame || isSetFakeEnrollMapToView()){
			Log.d(TAG, "++++++from mLastEnrollMap+++++++++++");
			showEnrollMap(mLastEnrollMap);					
			return;
		}
				
		Log.d(TAG, "+++++++++++from mFakeEnrollMap++++++++++");
		showEnrollMap(mFakeEnrollMap);
	}		
	
    @Override
    public void addEnrollProgress(){
    	mEnrollCount++;		    	
    }
        
	private boolean isSetFakeEnrollMapToView(){
		return (mEnrollCount > 0);
	}

	@Override
	public void runFingerAnimation() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void holdFinger() {
		
	}

	@Override
	public void removeFinger() {
		// TODO Auto-generated method stub
		
	}

}

/*
private void dilation(byte[] img, int w, int h, int thickness){
int x, y,i,k, idx= w, line,j;   
for(y= thickness;y<h- thickness;y++, idx += w)
   for(x= thickness;x<w- thickness;x++)
      if (img[x+idx]==0){
              line = idx+x-w*thickness;
          for(k=0;k< thickness*2+1;k++, line+=w){
              for (j=-thickness;j<= thickness;j++){
                      if(img[line+j] == -1) 
                          img[line+j] = (byte) 1; // mark as candidate
              }
          }                               
      }
    
for(i=0;i<w*h;i++){
  if(img[i]==1){        	  
     img[i]=0;  
  }   
}     

}       
	
private void dilation2(byte[] img, byte[] dst, int w, int h, int thickness){
byte[] PATTERN = new byte[512];
Arrays.fill(PATTERN, (byte) -1);

int x, y,i,k, idx= w, line,j, fw=thickness*2+1; 
System.arraycopy(img,0,dst,0,w*h);
for(y= thickness;y<h- thickness;y++, idx += w){
    for(x= thickness;x<w- thickness;x++){
        if (img[x+idx]==0){
            line = idx+x-(w+1)*thickness;
            for(k=0;k< thickness*2+1;k++, line+=w){
               System.arraycopy(PATTERN, 0, dst, line, fw);                              
            }
        }                               
    }
                            
}                                 
                   
}      

private void dilation3(byte[] img, int w, int h, int thickness){
Log.d(TAG, "dilation3");
System.arraycopy(img, 0, mDilatedEnrollMap, w, h);     
int x, y,k, idx= w* thickness, line, fw=thickness*2+1;
                      
for(y= thickness;y<h- thickness;y++, idx += w){
    for(x= idx+thickness;x<idx+w- thickness;x++){
    	if (img[x]==0){     // 0:black     
            line = x-(w+1)*thickness;
            for(k=0;k< fw;k++, line+=w){    
            	Arrays.fill(mDilatedEnrollMap, line,line+fw, (byte)0);                     
            }
        }                               
    }
                     
}                        

}   
*/