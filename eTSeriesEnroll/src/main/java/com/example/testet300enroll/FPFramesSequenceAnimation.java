package com.example.testet300enroll;

import java.lang.ref.SoftReference;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Handler;
import android.widget.ImageView;

public class FPFramesSequenceAnimation {
	private int[] mFrames; // animation frames
	private int[] mFrameDelays; 
	private int mIndex; // current frame
	private boolean mShouldRun; // true if the animation should continue running. Used to stop the animation
	private boolean mIsRunning; // true if the animation currently running. prevents starting the animation twice
	private SoftReference<ImageView> mSoftReferenceImageView; // Used to prevent holding ImageView when it should be dead.
	private Handler mHandler;
	private int mDelayMillis;
	private OnAnimationStoppedListener mOnAnimationStoppedListener;

	private Bitmap mBitmap = null;
	private BitmapFactory.Options mBitmapOptions;
	private boolean mIsOneShot;
	        		
	public interface OnAnimationStoppedListener {
		public void AnimationStopped();
	}
	
	//public FramesSequenceAnimation(ImageView imageView, int[] frames, int fps) {
	public FPFramesSequenceAnimation(ImageView imageView, int[] frames, int[] delays){
		mHandler = new Handler();
		mFrames = frames;
		mFrameDelays = delays;
		mIndex = -1;
		mSoftReferenceImageView = new SoftReference<ImageView>(imageView);
		mShouldRun = false;
		mIsRunning = false;
		//mDelayMillis = 1000 / fps;
		mDelayMillis = delays[0];
		imageView.setImageResource(mFrames[0]);

		// use in place bitmap to save GC work (when animation images are the same size & type)
		if (Build.VERSION.SDK_INT >= 11) {
			Bitmap bmp = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
			int width = bmp.getWidth();
			int height = bmp.getHeight();
			Bitmap.Config config = bmp.getConfig();
			mBitmap = Bitmap.createBitmap(width, height, config);
			mBitmapOptions = new BitmapFactory.Options();
			// setup bitmap reuse options. 
			mBitmapOptions.inBitmap = mBitmap;
			mBitmapOptions.inMutable = true;
			mBitmapOptions.inSampleSize = 1;
		}
	}

	public void setCallback(OnAnimationStoppedListener l){
		mOnAnimationStoppedListener = l;
	}

	private int getNextFrame() {    	    	    	

		if((mIndex+1) == mFrames.length && mIsOneShot) stop();
		
		mIndex++;
		
		if (mIndex == mFrames.length){
			mIndex = 0;
			mDelayMillis = mFrameDelays[0];
		}else
			mDelayMillis = mFrameDelays[mIndex];

		return mFrames[mIndex];
	}
	
	/**
	 * Starts the animation
	 */
	public synchronized void start() {
		mShouldRun = true;
		if (mIsRunning)
			return;

		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				ImageView imageView = mSoftReferenceImageView.get();
				if (!mShouldRun || imageView == null) { // first time come in
					mIsRunning = false;
					if (mOnAnimationStoppedListener != null) {
						mOnAnimationStoppedListener.AnimationStopped();
					}
					return;
				}

				mIsRunning = true;
				mHandler.postDelayed(this, mDelayMillis);

				if (imageView.isShown()) {
					int imageRes = getNextFrame();
					if (mBitmap != null) { // so Build.VERSION.SDK_INT >= 11
						Bitmap bitmap = null;
						try {
							bitmap = BitmapFactory.decodeResource(imageView.getResources(), imageRes, mBitmapOptions);
						} catch (Exception e) {
							e.printStackTrace();
						}
						if (bitmap != null) {
							imageView.setImageBitmap(bitmap);
						} else {
							imageView.setImageResource(imageRes);
							mBitmap.recycle();
							mBitmap = null;
						}
					} else {
						imageView.setImageResource(imageRes);
					}
				}

			}
		};

		mHandler.post(runnable);
	}

	/**
	 * Stops the animation
	 */
	public synchronized void stop() {
		mShouldRun = false;
	}

	public void setOneShot(boolean param){
		mIsOneShot = param;
	}    	
}
