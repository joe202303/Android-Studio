package com.example.testet300enroll;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

public class FPET310LearningActivity extends Activity {

	public static final String TAG = "FPET310LearningActivity";
	private String mFid;
	private ET310Action mFragment;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_learning);
		
		mFid = getIntent().getStringExtra(FPFingerActivity.FINGER_ID_KEY);
		Log.d(TAG, "FINGER_ID_KEY=" + mFid);
		if (savedInstanceState == null) {
			getFragmentManager().beginTransaction()
			  .add(R.id.container, new FPET310LearningFragment(mFid)).commit();
		}
		setupActionBar();	
	}

	/**
	 * Set up the {@link android.app.ActionBar}.
	 */
	private void setupActionBar() {
		getActionBar().hide();
	}
	
	@Override
	protected void onResume() {
		super.onResume();		
		Log.d(TAG, "onResume");	
		mFragment = (ET310Action) getFragmentManager()
		  .findFragmentById(R.id.container);
	} 
	
    @Override
    public void onPause() {
        Log.d(TAG, "onPause");
    	super.onPause();    	
    }
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.learning, menu);
		return true;
	}

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
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // BACK button pressed	
			mFragment.onKeyDown();		
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	void doFinish(){
		Intent returnIntend =new Intent(); 				
		setResult(0, returnIntend);
		finish();
	}	

}
