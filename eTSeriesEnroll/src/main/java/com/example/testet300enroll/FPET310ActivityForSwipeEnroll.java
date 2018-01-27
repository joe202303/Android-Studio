package com.example.testet300enroll;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.testet300enroll.FPFingerActivity.Layouts;

interface ET310ActionForSwipeEnroll{
	
	int SHOW_PLACE_FINGER  = 1;
	int SHOW_REMOVE_FINGER = 2;
	int SHOW_HOLD_FINGER   = 3;
	
	void onKeyDown();
}

public class FPET310ActivityForSwipeEnroll extends Activity {

	private static final String TAG = "FPET310ActivityForSwipeEnroll";
	//private ET310Action mFragment;
	private boolean mKeyBackPressed;
	
	public String casename;
	public String filename;
	public Boolean cansaveimage;
	public int imagecount;
	public String fingernumber;
	public String filenamecount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);		
		setContentView(R.layout.activity_fpet310);
				
		Bundle bundle = this.getIntent().getExtras();
		casename = bundle.getString("casename");
		filename = bundle.getString("usernameforenroll");
	    cansaveimage = bundle.getBoolean("cansaveimage");
	    imagecount = bundle.getInt("imagecount");
	    fingernumber = bundle.getString("fingernumber");
	    getIntent().putExtra("filename", filename);
	    getIntent().putExtra("imagecount", imagecount);

	    Log.d(TAG, "filename="+filename);
	    Log.d(TAG, "cansaveimage="+cansaveimage);
	    Log.d(TAG, "imagecount="+imagecount);
	    Log.d(TAG, "fingernumber="+fingernumber);
	    
		if (savedInstanceState == null) {
			setEnrollView(getIntent()
			  .getStringExtra(FPFingerActivity.ENROLL_ID));									
		}
		setupActionBar();
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().hide();
	}
	
	private void setEnrollView(String enrollID){
		/*
		String enrollLayout = getIntent().getStringExtra(FPFingerActivity.LAYOUT_ID);
		if(enrollLayout.equals(Layouts.ET310_CLASSICAL.toString())){
			Log.d(TAG, "setEnrollView " + enrollLayout);
			FPET310CFragment fr = new FPET310CFragment();
			fr.setEnrollId(enrollID);
			getFragmentManager().beginTransaction().add(R.id.container, fr).commit();
		}else if(enrollLayout.equals(Layouts.ET310_TUTORIAL.toString())){
			FPET310TFragment fragment = new FPET310TFragment();
			fragment.setEnrollId(enrollID);
			fragment.setPhase("enroll");
			getFragmentManager().beginTransaction()
			  .add(R.id.container, fragment).commit();
		}
		*/
		//FPET310CFragment fr = new FPET310CFragment();
		FPET310CFragmentForSwipeEnroll fr = new FPET310CFragmentForSwipeEnroll(casename, filename, fingernumber, cansaveimage, imagecount);
		fr.setEnrollId(enrollID);
		getFragmentManager().beginTransaction().add(R.id.container, fr).commit();
		
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	public void onPause() {	   
	  	super.onPause();   	  	
	}	
	
	/*
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fpet310, menu);
		return true;
	}
	*/
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}	
		
	void doFinish(){
		Intent returnIntend =new Intent(); 				
		setResult(0, returnIntend);
		finish();
	}	
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		Log.d(TAG, "keyCode="+keyCode);
		if (keyCode == KeyEvent.KEYCODE_BACK) { // BACK button pressed
			if (!mKeyBackPressed)
			{
				mKeyBackPressed = true;
				Toast.makeText(this,"Please click back again to cancel", 
		                Toast.LENGTH_SHORT).show();
				new Timer().schedule(new TimerTask(){
					@Override
					public void run(){
						mKeyBackPressed = false;
					}
				}, 500);
				return false;
			}
			mKeyBackPressed = false;
		
			ET310ActionForSwipeEnroll fragment = (ET310ActionForSwipeEnroll) getFragmentManager()
					  .findFragmentById(R.id.container);
			fragment.onKeyDown();
			return true;
		}		
		return super.onKeyDown(keyCode, event);
	}		
}
