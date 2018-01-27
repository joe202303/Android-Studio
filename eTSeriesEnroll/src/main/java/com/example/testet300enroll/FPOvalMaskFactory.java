package com.example.testet300enroll;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

public class FPOvalMaskFactory{
	
	public static final float ENROLL_OK_DIFF = 30.0f;
	private Bitmap mCurrentBmp;
	private float mXr;
	private float mYr;								
	private float mXdiff=0.7f;	// increase ratio
	
	public FPOvalMaskFactory(){
		this.mXr = 20*2;
		this.mYr = 25*2;
	}	
	
	public void setDiff(float xdiff){
		this.mXdiff = xdiff;
	}
	
	public Bitmap drawOval(int w, int h){
				
		int xc = w/2;
		int yc = h/2;
		
		Bitmap drawBmp = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);		
		Canvas cv = new Canvas(drawBmp);
		Paint p = new Paint();
		p.setAntiAlias(true);
		p.setColor(Color.BLACK);
		p.setStyle(Paint.Style.FILL); 
				
		mXr += mXdiff;
		mYr += mXdiff*3/2;
		
		cv.drawOval(new RectF(xc-mXr, yc-mYr, xc+mXr, yc+mYr), p);	
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();
		
		mCurrentBmp = drawBmp;
		
		return drawBmp;	
	}	
	
	public Bitmap getMask(){
		return mCurrentBmp;
	}		
	
	public void restoreOvalBase(int count, int animCount){
		float xdiff = 0.4f;
		if(0==count){
			for(int i=0 ; i<10 ; i++){
				mXr -= xdiff;
				mYr -= xdiff*3/2;		
			}
		}else{
			for(int i=0 ; i<animCount ; i++){
				mXr -= xdiff;
				mYr -= xdiff*3/2;		
			}
		}
	}
		
	public void reset(){
		this.mXr = 20;
		this.mYr = 25; 
	}
	
	
}
