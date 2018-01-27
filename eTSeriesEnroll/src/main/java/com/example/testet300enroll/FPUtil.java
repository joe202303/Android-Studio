package com.example.testet300enroll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.Arrays;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.IBinder;
import android.util.Log;
import android.view.KeyEvent;

public class FPUtil {
	
	public static final String TAG = "Util";
	private static int[] mPicPixels = new int[512*512];		
	private static int[] mMaskPixels = new int[380*380];
	
	public static Bitmap createMask(int mapW, int mapH, byte[] enrollMap){
		Bitmap mask = Bitmap.createBitmap(mapW, mapH, Bitmap.Config.ARGB_8888);
		for(int i = 0; i < enrollMap.length; i++){
			mPicPixels[i] = (enrollMap[i] == 0)? Color.BLACK:0;
		}
		mask.setPixels(mPicPixels, 0, mapW, 0, 0, mapW, mapH);
		return mask;
	}	
			
	public static Bitmap composeBitmap(int w, int h, Bitmap scaledBaseBmp, Bitmap maskBitmap, int[] scaledFrontPixels){
		
		int[] frontPixels = maskFrontBmp(w, h, maskBitmap, scaledFrontPixels);			
		return composeBitmap(w, h, scaledBaseBmp, frontPixels);
	}	
	
	private static int[] maskFrontBmp(int w, int h, Bitmap maskBitmap, int[] frontPixels){

		Arrays.fill(mMaskPixels, 0);
		maskBitmap.getPixels(mMaskPixels, 0, w, 0, 0, w, h);

		int idx=0;
		for(int value : mMaskPixels){
			if(0x00000000 == value){
				frontPixels[idx] = value;
			}
			idx++;
		}
		return frontPixels;
	}
	
	private static Bitmap composeBitmap(int w, int h, Bitmap baseBitmap, int[] resultPixels){
		
		Bitmap composedBitmap = baseBitmap.copy(Bitmap.Config.ARGB_8888, true);
		Canvas cv = new Canvas(composedBitmap);
		cv.drawBitmap(resultPixels, 0, w, 0, 0, w, h, true, new Paint());
		cv.save(Canvas.ALL_SAVE_FLAG);
		cv.restore();	
		return composedBitmap;
	}
	
	public static int[] bmpToPixels(Bitmap bitmap, int w, int h){    	
		
    	int[] maskPixels = new int[w*h];
    	bitmap.getPixels(maskPixels, 0, w, 0, 0, w, h);
    	return maskPixels;
	}	
	
	public static void countArrayContent(int[] array){
		int[] counter = new int[array.length];
		boolean bFlag=false;
		for(int i=0 ; i<array.length; i++){
			if(counter[i] != Color.BLACK && counter[i] != 0){
				bFlag=true;
				Log.d(TAG, "content["+i+"]= " + counter[i]);
			}	
		}
		
		if(!bFlag){
			Log.d(TAG, "++++++++++++++++++not into log++++++++++++++++");
		}		
	}
	
	public static void countArrayContent(byte[] array){
		
		int[] counter = new int[array.length];
		for(int i=0 ; i<array.length; i++){
			int index = array[i];
			
			if(index == -1)
				index = array.length-1;
			
			if(index < 0)
				index = -index;
			
			counter[index]++;			
		}
		Log.d(TAG, "first: " + counter[0]);
		Log.d(TAG, "last: " + counter[array.length-1]);
		for(int j=1 ; j<counter.length-1; j++){
			if (counter[j] > 0) Log.d(TAG, "counter["+j+"]= " + counter[j]);	
		}
		
	}
		
	public static AlertDialog.Builder getDilog(Context context, int title){
		return new AlertDialog.Builder(context)
			   .setIconAttribute(android.R.attr.alertDialogIcon)
			   .setTitle(title);			   			
	}
	
	public static void mirrorContent(byte[] map){		
		
		for (int k = 0; k < map.length/2; k++) {
	        byte temp = map[k];
	        map[k] = map[map.length-(1+k)];
	        map[map.length-(1+k)] = temp;
	    }		
		
		/*
		int y, x, diff;
		int w = 512;
		for (y=0; y<w; y++) {
			diff=0;
			int idx = y*w;
			for(x=idx; x< (idx+(idx+w))/2; x++){				
				byte temp = map[x];
			    map[x] = map[((y+1)*w)-(1+diff)];
			    map[((y+1)*w)-(1+diff)] = temp;
			    diff++;
			}			
		}
		*/						
	}	
	
	public static void saveRawDataToHex(File file, String fileName, byte[] bytes){		
		int cnt=0;
		FileOutputStream f=null;
		PrintWriter pw=null;
		final int line = 100;
		try {
			f = new FileOutputStream(new File(file, fileName));
			pw = new PrintWriter(f);
			for(byte b : bytes){				  				 
			  pw.print(String.format("%02X", b&0xff) + " ");
			  if(cnt%line == 0)
				pw.print("\n"); 
			  cnt++;
			}
		} catch (FileNotFoundException e) {			
			e.printStackTrace();
		}finally{
			 pw.flush();
		     pw.close();
		     try {
				f.close();
			} catch (IOException e) {				
				e.printStackTrace();
			}
		}	   
	}
	
	public static void setBlockHomeKey(Activity activity, boolean bBlock) {
        try {
            Class<?> classServiceManager = Class.forName("android.os.ServiceManager");
            Class<?> classWindowManager = Class.forName("android.view.IWindowManager");
            Class<?> classWindowManagerStub = Class.forName("android.view.IWindowManager$Stub");

            Method method = classServiceManager.getMethod("getService", new Class[] { String.class });
            IBinder serviceManagerBinder = (IBinder)method.invoke(classServiceManager, new Object[] { Context.WINDOW_SERVICE });

            method = classWindowManagerStub.getMethod("asInterface", new Class[] { IBinder.class });
            Object windowManager = method.invoke(classWindowManagerStub, new Object[] { serviceManagerBinder });

            method = classWindowManager.getMethod("requestSystemKeyEvent", new Class[] { Integer.TYPE, ComponentName.class, Boolean.TYPE });
            method.invoke(windowManager, new Object[] { KeyEvent.KEYCODE_HOME, activity.getComponentName(), bBlock });
        } catch (Throwable t) {
        }
    }		
}
