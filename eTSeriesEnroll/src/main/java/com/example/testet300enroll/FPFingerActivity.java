package com.example.testet300enroll;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnShowListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import egistec.fingerauth.api.FPAuthListeners;
import egistec.fingerauth.api.FpResDef;
import egistec.fingerauth.api.SettingLib;

public class FPFingerActivity extends Activity implements 
		FPAuthListeners.StatusListener,FPAuthListeners.VerifyListener ,FPAuthListeners.MatchedImageListener {
	
	private static final String TAG = "FPFingerActivity";
	private static final Boolean SAVE_IMAGE = true;
	public static final String mUserID = "SYSTEM";
	public static final int REQ_ENROLL = 2;
	public static final int REQ_LEARNING = 3;
	private static final int MAX_FINGERS = 3;	
	static final int VERIFY_TRANSITION_TIME = 200;
	static final int TIMER_DELAY = 200;
	static final int MAX_VERIFY_COUNT = 100;
	static final int COUNT_STEP = 10;
	
	public static final String MODE_SWIPE = "0";
	public static final String MODE_TOUCH = "1";
	
	
	public static final String ENROLL_ID = "ENROLL_ID";
	public static final String LAYOUT_ID = "LAYOUT_ID";
	
	public static final String PREF_KEY = "ET300Enroll";
	private static final String FINGER_KEY = "FingerName";
	private static final String LAYOUT_KEY = "LAYOUT";
	public static final String FINGER_ID_KEY = "Fingerid";
	private static final HashMap<String, String> mInitialFingerMap = new HashMap<String, String>();
	private static final int ADD_FINGER_POSITION = 0; // index 0 for add finger
	private static final int NO_FINGER_ON_LIST_VIEW = 1;
	
	private static Context mContext;
	private ListView mFingerListView;
	private List<FPFingerInfo> mFpList;
	private FPAdapter mAdapter;
	private SettingLib mYu;
	private FPFingerInfo mClickedFingerInfo;
	private SharedPreferences mPrefs;
	private Button mVerifyBtn;
	public enum Layouts {ET300_CLASSICAL, ET300_TUTORIAL, ET310_CLASSICAL, ET310_TUTORIAL};	
	private Layouts mLayout;
	private boolean mIsVerifying;
	private boolean mFromListView;	
	private LinearLayout mBtnContainer;	
	private boolean mKeyBackPressed;
	private boolean mWetfinger;
	private long mWetFingerTime = 0;
	private Spinner mFingerNumSpinner;
    private String[] mFingerNum = {"1","2","3","4","5","6"};
    private Spinner mDegreeSpinner;
    private String[] mFingerDegree = {"st", "45d", "90d"};
    private Spinner mFingerLimitSpinner;
    private String[] mFingerLimit;
    private Spinner mCaseListSpinner;
    private String[] mCaseList = {	"A", "B", "C", "D", "E", "F", "G", "H", "I", "J",
									"K", "L", "M", "N", "O", "P", "Q", "R", "S", "T",
									"U", "V", "W", "X", "Y", "Z", };
    private ArrayAdapter<String> listAdapter;
    private CheckBox mSaveBox = null;
    private EditText mUserName;
    private String mFileName;
    private static int mFingerNumidx = 0;
    private static int mFingerDegreeidx = 0;
    private static int mFingerLimitidx = 0;
    private static int mCaseidx = 0;
    private int mImageCount=0;
    private String mFileNameount;
    private byte[] mImageData;
    private static String mUserNameForEnroll = "FP1";
    
    private boolean mCanSaveImage = false;
	private TextView mVerifyStatus;
	private ImageView mFingerStatus;
	private ImageView mVerifyImage;
	private TextView mUserIDView;
	private TextView mViewFingerNumber;
	private LinearLayout mCase_linear;
	
	private Handler mHandler;
	class VerifyTask implements Runnable{
		@Override
		public void run(){
			if(mIsReachedLimit){
				mIsVerifying = false;
				mVerifyBtn.setText(R.string.verify);
				return;
			}
			mYu.identify();
			Log.d(TAG, "+++++++++call verify++++++++++++");						
		}
	}
	
	private TextView mVerifyAlert;
	private int mVerifySuccessCount;
	private int mVerifyFailCount;
	
	private boolean mIsReachedLimit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "+++ onCreate +++" + savedInstanceState);

		super.onCreate(savedInstanceState);

		setContentView(R.layout.fp_activity_finger);		
		
		//CheckBox
		mUserName = (EditText)findViewById(R.id.userID);
		mUserName.addTextChangedListener(new TextWatcher() {
			@Override    
			   public void onTextChanged(CharSequence s, int start,
			     int before, int count){
				Log.d(TAG, "");
				mUserNameForEnroll = mUserName.getText().toString();
				Log.d(TAG, "" + mUserNameForEnroll);
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
			}
		});
		
		mSaveBox=(CheckBox)findViewById(R.id.CheckBoxSaveImage);
		if(SAVE_IMAGE)mSaveBox.setChecked(true);
		else mSaveBox.setChecked(false);
		mSaveBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener(){ 
            @Override
            public void onCheckedChanged(CompoundButton buttonView, 
                    boolean isChecked) { 
                if(isChecked){ 
                	mCanSaveImage = true;
                	Toast.makeText(mContext, "Save", Toast.LENGTH_SHORT).show();
                }else{ 
                	mCanSaveImage = false;
                	Toast.makeText(mContext, "Not Save", Toast.LENGTH_SHORT).show();
                } 
            } 
        }); 

		mFingerNumSpinner = (Spinner)findViewById(R.id.SpinnerFingerNumber);
	    listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, mFingerNum);
        listAdapter.setDropDownViewResource(R.layout.myspinner);
	    mFingerNumSpinner.setAdapter(listAdapter);
	    mFingerNumSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int position, long arg3) {
					mFingerNumidx = position ;
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					// TODO Auto-generated method stub
					
				}
	    });
		
	    mDegreeSpinner = (Spinner) findViewById(R.id.FingerDegree);
	    listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, mFingerDegree);
        listAdapter.setDropDownViewResource(R.layout.myspinner);
	    mDegreeSpinner.setAdapter(listAdapter);
	    mDegreeSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1,
					int position, long arg3) {				
				mFingerDegreeidx = position ;
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
	    });

		mFingerLimit = new String[MAX_VERIFY_COUNT / COUNT_STEP + 1];
		mFingerLimit[0] = Integer.toString(1);
		for(int i=1; i <= MAX_VERIFY_COUNT / COUNT_STEP; i++) mFingerLimit[i] = Integer.toString(i*COUNT_STEP);
	    mFingerLimitSpinner = (Spinner) findViewById(R.id.FingerLimit);
	    listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, mFingerLimit);
        listAdapter.setDropDownViewResource(R.layout.myspinner);
	    mFingerLimitSpinner.setAdapter(listAdapter);
	    mFingerLimitSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {				
				mFingerLimitidx = position ;
				Log.d(TAG, "+++ mFingerLimit +++ " + mFingerLimitidx + "position" +position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
	    });
	    
	    mCaseListSpinner = (Spinner) findViewById(R.id.CaseList);
	    listAdapter = new ArrayAdapter<String>(this, R.layout.myspinner, mCaseList);
        listAdapter.setDropDownViewResource(R.layout.myspinner);
	    mCaseListSpinner.setAdapter(listAdapter);
	    mCaseListSpinner.setOnItemSelectedListener(new OnItemSelectedListener(){
			@Override
			public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {				
				mCaseidx = position ;
				Log.d(TAG, "+++ mCasidx +++ " + mFingerLimitidx + "position" +position);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
	    });
	    
		mContext = this;
		getActionBar().setBackgroundDrawable(new ColorDrawable(Color.BLACK));	
		mVerifyBtn = (Button) findViewById(R.id.verify_btn);
		mBtnContainer = (LinearLayout) findViewById(R.id.verify_btn_container);
		mVerifyAlert = (TextView) findViewById(R.id.verify_alert);
		mVerifyStatus = (TextView) findViewById(R.id.verify_status);
		mFingerStatus = (ImageView) findViewById(R.id.fingerprint);
		mVerifyImage = (ImageView) findViewById(R.id.verify_image);
		mVerifyStatus.setText(String.format("total=%d, success=%d, fail=%d", 
		  mVerifySuccessCount+mVerifyFailCount, mVerifySuccessCount, mVerifyFailCount));
			
		mPrefs = this.getSharedPreferences(PREF_KEY, Context.MODE_PRIVATE);	
		//String tmp = mPrefs.getString(FINGER_KEY, "");
		for(int i=0; i<MAX_FINGERS; i++){
			mInitialFingerMap.put(mUserID + "_" + "L"+i, "Finger "+(i+1));
			//mInitialFingerMap.put(mUserID + "_" + ""+append(i), "Finger "+(i+1));
		}		
		mHandler = new Handler(Looper.getMainLooper());
		initFingerListView();
		mPrefs.edit()
		  .putString("touchmode", getString(R.string.touch_mode))
		  .commit();		
		
		mPrefs.edit().putString(FINGER_KEY,  mFpList.toString()).commit();
		
		//save_image
		mUserIDView = (TextView) findViewById(R.id.userIDView);
		mViewFingerNumber = (TextView) findViewById(R.id.ViewFingerNumber);
		mCase_linear = (LinearLayout) findViewById(R.id.case_linear);
		if(SAVE_IMAGE)
		{
			mUserIDView.setVisibility(View.VISIBLE);
			mViewFingerNumber.setVisibility(View.VISIBLE);
			mFingerNumSpinner.setVisibility(View.VISIBLE);
			mDegreeSpinner.setVisibility(View.VISIBLE);
			mUserName.setVisibility(View.VISIBLE);
			mSaveBox.setVisibility(View.VISIBLE);
			mCase_linear.setVisibility(View.VISIBLE);
		}
		else
		{
			mUserIDView.setVisibility(View.INVISIBLE);
			mViewFingerNumber.setVisibility(View.INVISIBLE);
			mFingerNumSpinner.setVisibility(View.INVISIBLE);
			mDegreeSpinner.setVisibility(View.INVISIBLE);
			mUserName.setVisibility(View.INVISIBLE);
			mSaveBox.setVisibility(View.INVISIBLE);
			mCase_linear.setVisibility(View.INVISIBLE);
		}
	}
	
	/*
	private String append(int i){		
		return (i<9) ? "0"+i : String.valueOf(i);
	}
	*/
	@Override
	protected void onResume() {
		Log.d(TAG, "+++++ onResume +++++");
		super.onResume();
		mYu = new SettingLib(this);	
		mYu.setStatusListener(this);
		mYu.setVerifyListener(this);
		mYu.setMatchedImageListener(this);
		onServiceConnected();		
		System.gc();
		mFingerNumSpinner.setSelection(mFingerNumidx);
		mDegreeSpinner.setSelection(mFingerDegreeidx);
		if(mFingerLimitidx == 0) mFingerLimitidx = MAX_VERIFY_COUNT / COUNT_STEP;
		mFingerLimitSpinner.setSelection(mFingerLimitidx);
		mCaseListSpinner.setSelection(mCaseidx);
		mUserName.setText(mUserNameForEnroll);
		Log.d(TAG, "savebox.isChecked() "+mSaveBox.isChecked());
		if(mSaveBox.isChecked()){
			mCanSaveImage = true;
		}
		FPUtil.setBlockHomeKey(FPFingerActivity.this, false);
	}

	@Override
	protected void onPause(){
		Log.d(TAG, "onPause");
		super.onPause();
		mYu.abort();
		try {
			mYu.cleanup();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		mYu = null;
		
		//mYu.unbind();
	}	
    
	//create menu for del and rename
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			      ContextMenu.ContextMenuInfo menuInfo) {
		if (v.getId() == R.id.finger_list) {
			ListView lv = (ListView) v;
			AdapterView.AdapterContextMenuInfo acmi = (AdapterContextMenuInfo) menuInfo;
			mClickedFingerInfo = (FPFingerInfo) lv.getItemAtPosition(acmi.position); // the info which i clicked

			//String touchMode = mPrefs.getString("touchmode", "");
			
			if (ADD_FINGER_POSITION == acmi.position) return;
			
			super.onCreateContextMenu(menu, v, menuInfo);
			menu.setHeaderTitle(mClickedFingerInfo.getAliasName());
			MenuInflater inflater = getMenuInflater();

			inflater.inflate(R.menu.fp_action, menu);
			//menu.add(getResources().getString(R.string.context_menu_del));
			//menu.add(getResources().getString(R.string.context_menu_rename));

			//if(touchMode.equals(MODE_TOUCH)){
			//  menu.add(getResources().getString(R.string.context_menu_learning));
			//}
		}
	}

	@Override
	public boolean onContextItemSelected(final MenuItem item) {
		Log.d(TAG, "onContextItemSelected: " + item.getItemId());
		Log.d(TAG, "getTitle: " + item.getTitle());
		Log.d(TAG, "menuInfo: " + mClickedFingerInfo.getAliasName());
		if (item.getTitle().equals(getResources().getString(R.string.context_menu_del))) {	
			AlertDialog.Builder dialog = FPUtil.getDilog(mContext, R.string.dialog_delete_msg);	
			dialog.setPositiveButton(R.string.dialog_ok,
			  new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
                
				  if (mYu.deleteFeature(mClickedFingerInfo
					 .getEnrollkey())) {
					  mFpList.remove(mClickedFingerInfo);
                      Log.d(TAG,"mFpList remove");
					if(NO_FINGER_ON_LIST_VIEW == mFpList.size()){
					  mPrefs.edit().remove(FINGER_KEY).commit();
						mAdapter.notifyDataSetChanged();
					  return;
					}
					String newFingerStr = mFpList.toString();
					mPrefs.edit().putString(FINGER_KEY, newFingerStr).commit();
					  mAdapter.notifyDataSetChanged();
				  } else {
					Toast.makeText(mContext,
					  R.string.toast_delete_finger_failed,
		  			  Toast.LENGTH_SHORT).show();
				  }
				}
			  }).setNegativeButton(R.string.dialog_cancel,
				 new DialogInterface.OnClickListener() {
				   public void onClick(DialogInterface dialog,int whichButton) {}
				 }).create().show();
		} else if (item.getTitle().equals(getResources().getString(R.string.context_menu_rename))) {
			LayoutInflater factory = LayoutInflater.from(this);
			final View textEntryView = factory.inflate(
					R.layout.fp_alert_dialog_text_entry, null);
			final EditText aliasName = (EditText) textEntryView
					.findViewById(R.id.username_edit);
			aliasName.setText(mClickedFingerInfo.getAliasName());			
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setIconAttribute(android.R.attr.alertDialogIcon);
			builder.setTitle(R.string.dialog_rename_title);
			builder.setView(textEntryView);
			builder.setPositiveButton(R.string.dialog_ok,
				new DialogInterface.OnClickListener() {
				  public void onClick(DialogInterface dialog,
				  	int whichButton) {
				  	// User clicked OK so do some stuff 
				  					
				  	String changedAliasName = aliasName
				  	  .getText().toString();
		   
				  	Log.d(TAG, "changedAlias: "
					  + changedAliasName);
				  	mClickedFingerInfo.setAliasName(changedAliasName);					
				  	String replaceStr =  mFpList.toString();
				  	mPrefs.edit().putString(FINGER_KEY, replaceStr)
				  	  .commit();					  	
					if(mPrefs.getString(FINGER_KEY, "").equals(replaceStr)){
					  mAdapter.notifyDataSetChanged();
					}
					Log.d(TAG, "replace str: " + replaceStr);
				  }
				});
			builder.setNegativeButton(R.string.dialog_cancel,
				new DialogInterface.OnClickListener() {
			  	  public void onClick(DialogInterface dialog,
					int whichButton) {/* User clicked cancel so do some stuff */}
				});
			final AlertDialog altDialog = builder.create();
			altDialog.setOnShowListener(new OnShowListener(){
					@Override
				    public void onShow(DialogInterface dialog) {
						Log.d(TAG, "+++++ onShow +++++");
						altDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
					}
				});	
			aliasName.addTextChangedListener(new TextWatcher(){
				@Override
			    public void beforeTextChanged(CharSequence s, int start,
                  int count, int after){}
                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count){
                	Log.d(TAG, "+++++ onTextChanged s: " + s);   
                	if("".contentEquals(s)){                		
                		altDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                	}else{
                		altDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                	}
                }
                @Override
                public void afterTextChanged(Editable s){}
			});
			altDialog.show();			
		}
		
		/*
		else if(item.getTitle().equals(getResources().getString(R.string.context_menu_learning))){
			Intent intent = new Intent(mContext, FPET310LearningActivity.class);
			intent.putExtra(FINGER_ID_KEY, mClickedFingerInfo.getEnrollkey());
			startActivityForResult(intent, REQ_LEARNING);
		}
		*/
		return super.onContextItemSelected(item);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item){	
		item.setChecked(true);
		switch(item.getItemId()){
			/*
			case R.id.exclusive_checkable_item_1:
				Log.d(TAG, "get menu item_1 callback");				
				mLayout = Layouts.ET300_CLASSICAL;
				mPrefs.edit().putString(LAYOUT_KEY, mLayout.toString()).commit();
				return true;
			case R.id.exclusive_checkable_item_2:
				Log.d(TAG, "get menu item_2 callback");				
				mLayout = Layouts.ET300_TUTORIAL;	
				mPrefs.edit().putString(LAYOUT_KEY, mLayout.toString()).commit();
				return true;
			*/	
			case R.id.exclusive_checkable_item_3:
				Log.d(TAG, "get menu item_3 callback");
				mLayout = Layouts.ET310_CLASSICAL;
				mPrefs.edit().putString(LAYOUT_KEY, mLayout.toString()).commit();
				return true;
			case R.id.exclusive_checkable_item_4:
				Log.d(TAG, "get menu item_4 callback");
				mLayout = Layouts.ET310_TUTORIAL;
				mPrefs.edit().putString(LAYOUT_KEY, mLayout.toString()).commit();
				return true;	
			default:
				break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "+++onCreateOptionsMenu+++");
		//mLayout = Layouts.ET310_CLASSICAL;


		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.fp_finger, menu);

		// set menuItem
		String lastChosenLayout = mPrefs.getString(LAYOUT_KEY, "");
		if(lastChosenLayout.equals(mLayout.ET300_CLASSICAL.toString())){
			mLayout = Layouts.ET300_CLASSICAL;
			//menu.findItem(R.id.exclusive_checkable_item_1).setChecked(true);
		}else if(lastChosenLayout.equals(mLayout.ET300_TUTORIAL.toString())){
			mLayout = Layouts.ET300_TUTORIAL;
			//menu.findItem(R.id.exclusive_checkable_item_2).setChecked(true);
		}else if(lastChosenLayout.equals(mLayout.ET310_CLASSICAL.toString()) || lastChosenLayout.equals("")){
			mLayout = Layouts.ET310_CLASSICAL;
			menu.findItem(R.id.exclusive_checkable_item_3).setChecked(true);
		}else if(lastChosenLayout.equals(mLayout.ET310_TUTORIAL.toString())){
			/* for select swipe enroll mode */
			mLayout = Layouts.ET310_TUTORIAL;
			menu.findItem(R.id.exclusive_checkable_item_4).setChecked(true);
		}

		return true;
	}

	@Override
	public void onBadImage(int status) {
		Log.d(TAG, "++++++++++++ Bad image status=" + status + "+++++++++++++");
		verifyAlertAnimation();
		if(status == 1009){
			notVefiryAnimation(R.string.bad_image, Color.BLUE);
		}else if(status == 1082){
			notVefiryAnimation(R.string.partial_image, Color.BLUE);
		}
		else if(status == FpResDef.FP_RES_WATER_IMG){
			notVefiryAnimation(R.string.water_image, Color.YELLOW);
		}
		else if(status == FpResDef.FP_RES_FAST_IMG){
			notVefiryAnimation(R.string.fast_image, Color.WHITE);
		}
	}

	@Override
	public void onServiceConnected() {
		Log.d(TAG, "onServiceConnected()");			
		updateFingerList(mYu.getEnrollList(mUserID));
		mPrefs.edit().putString(FINGER_KEY,  mFpList.toString()).commit();
		mYu.connectDevice();
	}

	private void initFingerListView(){
		mFingerListView = (ListView) findViewById(R.id.finger_list);
		mFingerListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				Log.d(TAG, "click id: " + id + " position: " + position);
				
				if (ADD_FINGER_POSITION == (int) id) {
					if((MAX_FINGERS+1) == mFpList.size()){  // we can only register 5 fingers
						Toast.makeText(view.getContext(), "can only register "  + MAX_FINGERS + " fingers",
								Toast.LENGTH_SHORT).show();
						return;
						}
					
					//////
					if(mCanSaveImage){
						if(checkSDCard()){
		            		if("".equals(mUserName.getText().toString().trim())){
		            			Toast.makeText(mContext, "NO UserName", Toast.LENGTH_SHORT).show();
		            			return;
		            		}else{
		            			//Toast.makeText(mContext, "Save ", Toast.LENGTH_SHORT).show();
		            			//imagecount = 0 ;
		            			mFileNameount = String.format("%04d", mImageCount);
		            			mUserNameForEnroll =  mUserName.getText().toString();
		            			mFileName = mUserName.getText().toString() +"_"+ mFingerNum[mFingerNumidx] + "_" + mFileNameount  ;           			
		            		}
		            	}else{
		            		Toast.makeText(mContext, "NO SDCARD", Toast.LENGTH_SHORT).show();
		            		return;
		            	}
					}
					//////
					
					if(mIsVerifying){
						mFromListView = true;
						mYu.abort();
						return;
					}else{
						doEnroll();						
					}
				}
			}
		});
		this.registerForContextMenu(mFingerListView);
		mFpList = new FPList(mContext);
		mFpList.add(new FPFingerInfo("AddFinger", mContext));
		mAdapter = new FPAdapter(this, mFpList, mHandler);
		mFingerListView.setAdapter(mAdapter);
	}
	
	private void doEnroll(){
		String enrollKey = findEnrollKey();
		Log.d(TAG, "enrollKey: " + enrollKey);
		Log.d(TAG, "mLayout: " + mLayout.toString());
		Intent intent = null;
		switch(mLayout){
		  case ET300_CLASSICAL:
		  case ET300_TUTORIAL:	  
			 intent = new Intent(FPFingerActivity.this,
			   FPET300Activity.class);			 			 
			 break;
		  case ET310_CLASSICAL:
		  	 intent = new Intent(FPFingerActivity.this,
		 	   FPET310Activity.class);
			  break;
		  case ET310_TUTORIAL:
		  	 /* for select swipe enroll mode */
			 intent = new Intent(FPFingerActivity.this,
			   FPET310ActivityForSwipeEnroll.class);
			 break;
		}
		intent.putExtra(LAYOUT_ID, mLayout.toString());
		intent.putExtra(ENROLL_ID, enrollKey);
		Bundle bundle = new Bundle();
		bundle.putString("casename", mCaseListSpinner.getSelectedItem().toString());
	    bundle.putString("usernameforenroll", mUserNameForEnroll);
	    bundle.putBoolean("cansaveimage", mCanSaveImage);
	    bundle.putInt("imagecount", mImageCount);
	    bundle.putString("fingernumber", mFingerNum[mFingerNumidx]);
	    intent.putExtras(bundle);
		startActivityForResult(intent, REQ_ENROLL);
	}	
	
	private int fpSize(String fpData){
		if(fpData.equals("")){
			return 0;
		}	
		return fpSplitor(fpData).length;
	}
	
	private String[] fpSplitor(String fpData){
		if(fpData.equals("")){
			return null;
		}		
		String[] fingerArray = null;
		if(fpData.contains(";")){
			fingerArray = fpData.split(";");
		}else{
			fingerArray = new String[] {fpData};
		}
		return fingerArray;
	}
	
	private String appendFingerAlias(String[] fingers) {
		
		Map<String, String> fpMap = new HashMap<String,String>();		
		Arrays.sort(fingers);
		String value = "";
		
		if (!mPrefs.getString(FINGER_KEY, "").equals("")) {			
			String[] fingerArray = fpSplitor(mPrefs.getString(FINGER_KEY, ""));
			if(fingerArray == null){
				return value;
			}
			Arrays.sort(fingerArray);
			for (int i = 0; i < fingerArray.length; i++) {
				String fingerName = fingerArray[i];
				String enrollKey = fingerName.substring(0,
				  fingerName.indexOf(":"));
				String showName = fingerName
				  .substring(fingerName.indexOf(":") + 1);				
				fpMap.put(enrollKey, showName);
			}
		}
		
		for (int i = 0; i < fingers.length; i++) {
			String enrollKey = mUserID + "_" + fingers[i];			
			if(null == fpMap.get(enrollKey)){
				value += enrollKey + ":" + mInitialFingerMap.get(enrollKey) + ";";
				fpMap.put(enrollKey, mInitialFingerMap.get(enrollKey));
			}else{
				value += enrollKey + ":" + fpMap.get(enrollKey) + ";";
			}	
		}	
		return value;
	}

	private void updateFingerList(String enrollStr){				
		mFpList.clear();
		mFpList.add(new FPFingerInfo("AddFinger", mContext));
		if(enrollStr == null || enrollStr.equals("")){
		  mAdapter.notifyDataSetChanged();			
		  return;
		}									
		// parse enrollList String
		String[] fingers = fpSplitor(enrollStr);					
		
		if(fingers.length != fpSize(mPrefs.getString(FINGER_KEY, ""))){	
			Log.d(TAG, "+++++ into not sync flow +++++");
			mPrefs.edit().putString(FINGER_KEY, appendFingerAlias(fingers))
			  .commit();	
		}
		
		String fingerData = appendFingerAlias(fingers);
		String[] fingerArray = fingerData.split(";");

		for (int i = 0; i < fingerArray.length; i++) {				
			String fingerWithAlias = fingerArray[i];
			FPFingerInfo info = new FPFingerInfo(fingerWithAlias, mContext);
			mFpList.add(info);
		}
		Log.d(TAG, "fingerData: " + fingerData);
		mAdapter.notifyDataSetChanged();	
	}
	
	private String findEnrollKey() {

		String enrollKey = "";
		String[] comparedKey = new String[] { "L0", "L1", "L2", "L3", "L4" };
		String enrollStr = mYu.getEnrollList(mUserID);
		if (enrollStr != null) {
		    String[] fingers = fpSplitor(enrollStr);				
			Arrays.sort(fingers);
			boolean bIsHaveKey=false;
			for (String cmpKey : comparedKey) {	
				bIsHaveKey=false;
				for(String ff : fingers){
					if(ff.equals(cmpKey)){
						bIsHaveKey = true;
						break;
					}	
				}	
				if(!bIsHaveKey){
					enrollKey = mUserID + "_" + cmpKey;
					break;
				}				
			}
			
		} else {
			enrollKey = mUserID + "_" + comparedKey[0];			
		}
		return enrollKey;
	}

	@Override
	public void onServiceDisConnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void onFingerFetch() {
		Log.d(TAG, "+++ onFingerFetch +++");
	}

	@Override
	public void onFingerImageGetted() {
		Log.d(TAG, "+++ onFingerImageGetted +++");
		mVerifyAlert.setVisibility(View.VISIBLE);
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;				            	
            	
        ObjectAnimator oa = ObjectAnimator.ofFloat(mVerifyAlert, "translationX", 0, (width/2)-(mVerifyAlert.getWidth()/2));
        oa.setDuration(300);
        oa.start();		
	}

	@Override
	public void onUserAbort() {
		Log.d(TAG, "onUserAbort");
		if(mIsVerifying){						
			mIsVerifying = !mIsVerifying;
			mVerifyBtn.setText(R.string.verify);
			if(mFromListView){				
				new Timer().schedule(new TimerTask(){
					@Override
					public void run(){
						doEnroll();
					}
				}, 200);
				mFromListView = false;
			}		
		}
		FPUtil.setBlockHomeKey(FPFingerActivity.this, false);		
	}

	private long mLastDownTime=0;
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) { // BACK button pressed				
			Log.d(TAG, "++++++++++onKeyDown++++++++++++++");
			
			if (!mKeyBackPressed)
			{
				mKeyBackPressed = true;
				mLastDownTime = event.getDownTime();
				Toast.makeText(this,"Please click back again to cancel", 
		                Toast.LENGTH_SHORT).show();
				new Timer().schedule(new TimerTask(){
					@Override
					public void run(){
						mKeyBackPressed = false;
						mLastDownTime = 0;
					}
				}, 500);
				return false;
			}
			mKeyBackPressed = false;
			boolean isDubClick = (event.getDownTime()!=mLastDownTime);
			Log.d(TAG, "onKeyDown ret="+(isDubClick?"true":"false")+" t1"+event.getDownTime()+"t2="+mLastDownTime);
			
			if(mIsVerifying && isDubClick){
				mIsVerifying = !mIsVerifying;
				mYu.abort();
			}
			return super.onKeyDown(keyCode, event);
		}	
		return super.onKeyDown(keyCode, event);
	}	
	
	@Override
	public void onStatus(int status) {
		if(status == 1075){
			Log.d(TAG, "status == 1075");
		/*
			mWetfinger = false;
			long diff;
			long seconds;
			do{
				diff=  System.currentTimeMillis() - mWetFingerTime;
				seconds = (diff/1000) % 60;
			}while(seconds < 1 );
			mFingerStatus.setImageDrawable(getResources().getDrawable( R.drawable.fp_icon_b ));
			*/
			}
		else if (status == 1083){
			Log.d(TAG, "status == 1083");
			/*
			mWetfinger = true;
			new Handler().postDelayed(new Runnable(){   
			    public void run() {
			    	updatewetfingericon();
			    }   
			}, 100);*/
		}
		else if (status == 1074){ // finger remove
			Log.d(TAG, "status == 1074");
			if(mIsVerifying)
			{
				mHandler.postDelayed(new VerifyTask(), 50);
			}
		}
		
	}
	
	public void updatewetfingericon(){
		if(mWetfinger){
			mFingerStatus.setImageDrawable(getResources().getDrawable( R.drawable.fp_icon_b_wet_02 ));
			mWetFingerTime=   System.currentTimeMillis();
		}	
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult");
		Log.d(TAG, "requestCode: " + requestCode);
		Log.d(TAG, "resultCode: " + resultCode);
		mImageCount = data.getIntExtra("index", -1);
	}
	
	public void onVerifyClick(View v) {
		Button btn =  (Button) findViewById(R.id.verify_btn);			
		mIsVerifying=!mIsVerifying;	
		if(mIsVerifying){
		  getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		  FPUtil.setBlockHomeKey(FPFingerActivity.this, true);
		}else{
		  getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		  FPUtil.setBlockHomeKey(FPFingerActivity.this, false);
		}
		mUserName.setEnabled(!mIsVerifying);
		mFingerNumSpinner.setEnabled(!mIsVerifying);
		mDegreeSpinner.setEnabled(!mIsVerifying);
		mSaveBox.setEnabled(!mIsVerifying);
		mFingerLimitSpinner.setEnabled(!mIsVerifying);
		mCaseListSpinner.setEnabled(!mIsVerifying);
		
		if(mIsVerifying){
			mVerifySuccessCount=0; mVerifyFailCount = 0;
            mVerifyStatus.setText(String.format("total=%d, success=%d, fail=%d", 0, mVerifySuccessCount, mVerifyFailCount));
			btn.setText(R.string.abort);
			mIsReachedLimit = false;
			mYu.identify();
		}else{
			mFingerStatus.setImageDrawable(getResources().getDrawable( R.drawable.fp_icon_b ));
			btn.setText(R.string.verify);
			mYu.abort();
		}
		
	}

	@Override
	public void onSuccess() {		
		boolean findCorrectmatchedID = false;
		Log.d(TAG, "++++ on success +++");
		verifyAlertAnimation();			
		Button btn =  (Button) findViewById(R.id.verify_btn);
		btn.setText(R.string.abort);
		final String matchedID = mYu.getMatchedUserID();
		
		////
		if(mCanSaveImage)
			saveMatchedImgToSD(mImageData, true);
		/////
		
		int currentCount;
		mVerifySuccessCount++;		
		currentCount = mVerifySuccessCount + mVerifyFailCount;
		
		if(currentCount == Integer.valueOf(mFingerLimit[mFingerLimitidx])){
			mIsReachedLimit = true;
			showFinishDialog();
		}
		
		if(null == matchedID){
			
			new AlertDialog.Builder(FPFingerActivity.this)
	        .setIconAttribute(android.R.attr.alertDialogIcon)
	        .setTitle(R.string.dialog_match_ok)			
			.setPositiveButton(this.getString(R.string.dialog_ok), new DialogInterface.OnClickListener() {
		          public void onClick(DialogInterface dialog, int whichButton) {
		        	  //mHandler.postDelayed(new VerifyTask(), TIMER_DELAY);
		          }
			}).create().show();
			return;
		}
		
		Log.d(TAG, "matchedID: " + matchedID);		 

		for(FPFingerInfo f : mFpList){
			Log.d(TAG, "getEnrollKey: " + f.getEnrollkey());
			if(matchedID.equals(f.getEnrollkey())){
				f.setFingerMatched();
				findCorrectmatchedID = true;
			}
		
		}
		if(findCorrectmatchedID)
			mAdapter.notifyDataSetChanged();	
		else{
			//mHandler.postDelayed(new VerifyTask(), TIMER_DELAY);
			Toast.makeText(this, "matched ID =" + matchedID, Toast.LENGTH_SHORT).show();
		}	
		mVerifyStatus.setText(String.format("total=%d, success=%d, fail=%d", currentCount, mVerifySuccessCount, mVerifyFailCount));		
	}

	@Override
	public void onFail() {				
		Log.d(TAG, "++++++++++++verify failed+++++++++++++");
		if(mIsVerifying){
		verifyAlertAnimation();
		notVefiryAnimation(R.string.not_match, Color.RED);
		
		int currentCount;
		mVerifyFailCount++;
		currentCount = mVerifySuccessCount + mVerifyFailCount;	
		if(currentCount == Integer.valueOf(mFingerLimit[mFingerLimitidx])){
			mIsReachedLimit = true;
			showFinishDialog();		
		}		
		//mHandler.postDelayed(new VerifyTask(), TIMER_DELAY);
		////
		if(mCanSaveImage)
			 saveMatchedImgToSD(mImageData, false);
		/////	
		mVerifyStatus.setText(String.format("total=%d, success=%d, fail=%d", currentCount, mVerifySuccessCount, mVerifyFailCount));
		}
	}

	private void showFinishDialog(){
		String ui_alert = "success count=" + mVerifySuccessCount + "\n" + "fail count=" + mVerifyFailCount;
		FPUtil.setBlockHomeKey(FPFingerActivity.this, false);
		AlertDialog.Builder dialog = new AlertDialog.Builder(mContext)
		   							.setIconAttribute(android.R.attr.alertDialogIcon)
		   							.setTitle("verify finish");
		dialog.setMessage(ui_alert);
		dialog.setCancelable(false);
		dialog.setPositiveButton(R.string.dialog_ok,
		  new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				mUserName.setEnabled(true);
				mFingerNumSpinner.setEnabled(true);
				mDegreeSpinner.setEnabled(true);
				mSaveBox.setEnabled(true);
				mFingerLimitSpinner.setEnabled(true);
				mCaseListSpinner.setEnabled(true);
            	mVerifyFailCount=0;
            	mVerifySuccessCount=0;
            	mVerifyStatus.setText(String.format("total=%d, success=%d, fail=%d", 
				mVerifySuccessCount+mVerifyFailCount, mVerifySuccessCount, mVerifyFailCount));
			}
		  }).create().show();
	}
	
	private void verifyAlertAnimation(){		
		Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int width = size.x;	
		
  		ObjectAnimator oa = ObjectAnimator.ofFloat(mVerifyAlert, "translationX", (width/2)-(mVerifyAlert.getWidth()/2), 0);
		oa.setDuration(300);
		oa.addListener(new AnimatorListenerAdapter() {
            public void onAnimationEnd(Animator animation) {
            	mVerifyAlert.setVisibility(View.INVISIBLE);
        	}
        });
		oa.start();		
	}


	
	private void notVefiryAnimation(int idRes, int color){
		mVerifyBtn.setText(idRes);
		final TransitionDrawable td =
	       new TransitionDrawable(new Drawable[] {
	       new ColorDrawable(android.R.color.transparent),
	       new ColorDrawable(color),  
	    });
			
		mBtnContainer.setBackground(td);
	    td.startTransition(VERIFY_TRANSITION_TIME);
	    mHandler.postDelayed(new Runnable(){
        	@Override
        	public void run(){
        		td.reverseTransition(VERIFY_TRANSITION_TIME);
        		mVerifyBtn.setText(R.string.abort);
        	}
        }, TIMER_DELAY);	   
	}
	
	@Override
	protected void onDestroy(){
		Log.d(TAG, "+++++ on destroy +++++");
		super.onDestroy();		
		mYu = null;
		System.gc();
	}
	public static boolean checkSDCard() {
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

	private void saveMatchedImgToSD(byte[] image_buf, boolean result){
		String CaseName = mCaseListSpinner.getSelectedItem().toString();
		mFileNameount = String.format("%04d", mImageCount);
		mFileName = mUserName.getText().toString() +"_"+ mFingerNum[mFingerNumidx] + "_" + mFileNameount;
		if(result){
			mFileName += "_P";
		}else{
			mFileName += "_F";
		}
		Log.d(TAG, "filename= " + mFileName);
		Log.d(TAG, "fingerDegree= " + mFingerDegreeidx);
		Log.d(TAG, "CaseList = " + CaseName);
		File file = null;
		
		
		if(mFingerDegreeidx == 0){
			file = new File("/sdcard/temp/" + CaseName + "/" + mUserName.getText().toString() + "/" + mFingerNum[mFingerNumidx] +"/verify" + "/st");
		}else if(mFingerDegreeidx == 1){
			file = new File("/sdcard/temp/" + CaseName + "/" + mUserName.getText().toString() + "/" + mFingerNum[mFingerNumidx] +"/verify" + "/45d");
		}else if(mFingerDegreeidx == 2){
			file = new File("/sdcard/temp/" + CaseName + "/" + mUserName.getText().toString() + "/" + mFingerNum[mFingerNumidx] +"/verify" + "/90d");
		}		    
        if (!file.exists()) {
            file.mkdirs();
        }
        
        File myDrawFile = new File(file.getAbsolutePath()+ "/" + mFileName +".bin");
        FileOutputStream fos = null;
		try {
			fos = new FileOutputStream(myDrawFile);
			if(fos!=null){
				fos.write(image_buf);
		        fos.close();
		        Log.d(TAG, "save image filename= " + mFileName);
		        }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally{
			mVerifyBtn.setText(mVerifyBtn.getText()+"  "+mImageCount);
			//Toast.makeText(this, "store file " + filename, Toast.LENGTH_SHORT).show();
		}
		mImageCount++;
	}

	
	@Override
	public void onGetMatchedImg(byte[] img, int width, int height) {
		Log.d(TAG, "+++++ onGetMatchedImg +++++");
		mImageData = img.clone();
		
		Bitmap match_img = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);
		for (int index = 0; index < img.length; index ++) {
			img[index] = (byte) ~img[index];
     	}
		match_img.copyPixelsFromBuffer(ByteBuffer.wrap(img));
				
		mVerifyImage.setImageBitmap(match_img);
	}
}
