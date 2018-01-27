package vigis.client.api;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import egistec.csa.client.api.Fingerprint.*;
import egistec.csa.client.api.IFingerprint;

import egistec.csa.client.api.Fingerprint;
//import vigis.client.api.IFPAuthService;
//import vigis.client.api.IFPAuthServiceCallback;
import egistec.csa.client.api.IFPAuthService;
import egistec.csa.client.api.IFPAuthServiceCallback;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.util.SparseArray;

public class VigisFingerprint implements IFingerprint {

	/*
	 * Enroll Event
	 */
	public static final int ENROLL_STATUS = 1000;
	public static final int ENROLL_SUCCESS = 1001;
	public static final int ENROLL_FAILED = 1002;
	public static final int ENROLL_FAILURE_CANCELED = 1003;
	public static final int ENROLL_FAULURE_ENROLL_FAILURE_EXCEED_MAX_TRIAL = 1004; // always
																					// can
																					// not
																					// happen
	public static final int ENROLL_BITMAP = 1005;

	/*
	 * Identify Event
	 */
	public static final int IDENTIFY_SUCCESS = 1020;
	public static final int IDENTIFY_FAILED = 1021;
	public static final int IDENTIFY_FAILURE_CANCELED = 1022;
	// public static final int IDENTIFY_FAILURE_TIMEOUT = 1023; // scope3
	public static final int IDENTIFY_FAILURE_NOT_MATCH = 1024;
	public static final int IDENTIFY_FAILURE_BAD_QUALITY = 1025;

	/*
	 * Sensor Event
	 */
	public static final int CAPTURE_READY = 1040;
	public static final int CAPTURE_STARTED = 1041;
	public static final int CAPTURE_COMPLETED = 1042;
	public static final int CAPTURE_FINISHED = 1043;
	// public static final int CAPTURE_FINGER_LEAVE = 1043; //Finger is removed
	// from the sensor
	public static final int CAPTURE_SUCCESS = 1044;
	public static final int CAPTURE_FAILED = 1045;

	/*
	 * Accuracy Level
	 */
	public static final int ACCURACY_LOW = 2000;
	public static final int ACCURACY_REGULAR = 2001;
	public static final int ACCURACY_HIGH = 2002;
	public static final int ACCURACY_VERY_HIGH = 2003;

	/*
	 * Sensor Control
	 */
	public static final int PAUSE_ENROLL = 2010;
	public static final int RESUME_ENRORLL = 2011;

	/*
	 * Sensor Status
	 */
	public static final int SENSOR_OK = 2004;
	public static final int SENSOR_WORKING = 2005;
	public static final int SENSOR_OUT_OF_ORDER = 2006;
	// public static fianl int SENSOR_MALFUNCTIONED = 2007; //in scope3

	/*
	 * Image Quality
	 */
	public static final int QUALITY_GOOD = 0x00000000;
	public static final int QUALITY_FAILED = 0x10000000; // represent badImage
	public static final int QUALITY_OFFSET_TOO_FAR_LEFT = 0x00000001; // not
																		// support
	public static final int QUALITY_OFFSET_TOO_FAR_RIGHT = 0x00000002; // not
																		// support
	public static final int QUALITY_SOMETHING_ON_THE_SENSOR = 0x00000004; // survey
	public static final int QUALITY_WET_FINGER = 0x00000008; // not support
	public static final int QUALITY_NOT_A_FINGER_SWIPE = 0x00000010; // survey
	public static final int QUALITY_PRESSURE_TOO_LIGHT = 0x00000020; // int
																		// scope3
	public static final int QUALITY_PRESSURE_TOO_HARD = 0x00000040; // int
																	// scope3
	public static final int QUALITY_FINGER_TOO_THIN = 0x00000080; // not support
	public static final int QUALITY_DUPLICATED_SCANNED_IMAGE = 0x00000100; // not
																			// support
	public static final int QUALITY_TOO_FAST = 0x00000200; // in scope3
	public static final int QUALITY_TOO_SLOW = 0x00010000; // not support
	public static final int QUALITY_TOO_SHORT = 0x00020000; // not support
	public static final int QUALITY_SKEW_TOO_LARGE = 0x00040000; // not support
	public static final int QUALITY_REVERSE_MOTION = 0x00080000; // not support
	public static final int QUALITY_STICTION = 0x001000000; // not support
	public static final int QUALITY_ONE_HAND_SWIPE = 0x002000000; // not support
	public static final int QUALITY_PARTIAL_TOUCH = 0x010000000;
	public static final int QUALITY_EMPTY_TOUCH = 0x020000000; // not support

	/*
	 * public static final int QUALITY_GOOD = 0x00000000; public static final
	 * int QUALITY_TOO_FAST = 0x10000000; // in scope3 public static final int
	 * QUALITY_TOO_SLOW = 2022; // not support public static final int
	 * QUALITY_TOO_SHORT = 2023; // not support public static final int
	 * QUALITY_SKEW_TOO_LARGE = 2024; // not support public static final int
	 * QUALITY_OFFSET_TOO_FAR_LEFT = 2025; // not support public static final
	 * int QUALITY_OFFSET_TOO_FAR_RIGHT = 2026; // not support public static
	 * final int QUALITY_REVERSE_MOTION = 2027; // not support public static
	 * final int QUALITY_SOMETHING_ON_THE_SENSOR = 2028; // survey public static
	 * final int QUALITY_WET_FINGER = 2029; // not support public static final
	 * int QUALITY_NOT_A_FINGER_SWIPE = 2030; // survey public static final int
	 * QUALITY_BAD_SWIPE = 2031; // not support public static final int
	 * QUALITY_STICTION = 2032; // not support public static final int
	 * QUALITY_ONE_HAND_SWIPE = 2033; // not support public static final int
	 * QUALITY_PRESSURE_TOO_LIGHT = 2034; // int scope3 public static final int
	 * QUALITY_PRESSURE_TOO_HARD = 2035; // int scope3 public static final int
	 * QUALITY_FINGER_TOO_THIN = 2036; // not support public static final int
	 * QUALITY_PARTIAL_TOUCH = 2037; // in scope3 public static final int
	 * QUALITY_EMPTY_TOUCH = 2038; // not support public static final int
	 * QUALITY_FAILED = 2039; //represent badImage public static final int
	 * QUALITY_SAME_FINGER = 2040; // not support
	 */

	/*
	 * Result
	 */
	public static final int RESULT_OK = 0;
	public static final int RESULT_FAILED = -1;
	public static final int RESULT_CANCELED = -2;
	// public static final int RESULT_SENSOR_ERROR = -3; //scope3
	public static final int RESULT_NO_AUTHORITY = -4;

	// public static final int SERVICE_REMOTEEXCEPTION = -30;
	// public static final int SERVICE_EXCEPTION = -31;

	public static final String TAG = "FpCsaClientLib_VigisFingerprint";

	private static VigisFingerprint mInstance;
	private static Context mContext;
	private IFPAuthService mFPAuthService;

	private Map<String, Integer> fingerIndexMap;
	private SparseArray<String> indexMapToFinger;
	private static final String FPID_PREFIX = "EGISFPID";
	private static final String PASSOWORD_PREFIX = "EGISPWD";
	private static final String PASSWORD = "egistec";

	private boolean mIsEnrollSessionOpen;
	private boolean mIsFingerOrPWValidate;

	private String mEnrollUserId;
	private int mEnrollIndex;

	private boolean mIsFilterOn; // use this flag to filter some unwanted event

	public static final int OP_TYPE_ENROLL = 101;
	public static final int OP_TYPE_VERIFY = 102;

	private static final int MAP_W = 256;
	private static final int MAP_H = 256;

	private FingerprintEventListener mFingerprintEventListener;

	public static VigisFingerprint create(Context context) {
		Log.d(TAG, "create()");
		mContext = context;
		if (mInstance == null) {
			Log.d(TAG, "new VigisFingerprint()");
			mInstance = new VigisFingerprint();
		}
		return mInstance;
	}

	private VigisFingerprint() {
		unbind();
		bind();
		fingerIndexMap = new HashMap<String, Integer>();
		fingerIndexMap.put("R1", 1);
		fingerIndexMap.put("R2", 2);
		fingerIndexMap.put("R3", 3);
		fingerIndexMap.put("R4", 4);
		fingerIndexMap.put("R5", 5);
		fingerIndexMap.put("L1", 6);
		fingerIndexMap.put("L2", 7);
		fingerIndexMap.put("L3", 8);
		fingerIndexMap.put("L4", 9);
		fingerIndexMap.put("L5", 10);
		indexMapToFinger = new SparseArray<String>();
		indexMapToFinger.put(1, "R1");
		indexMapToFinger.put(2, "R2");
		indexMapToFinger.put(3, "R3");
		indexMapToFinger.put(4, "R4");
		indexMapToFinger.put(5, "R5");
		indexMapToFinger.put(6, "L1");
		indexMapToFinger.put(7, "L2");
		indexMapToFinger.put(8, "L3");
		indexMapToFinger.put(9, "L4");
		indexMapToFinger.put(10, "L5");
	}

	protected ServiceConnection mFPAuthConnection = new ServiceConnection() {
		public void onServiceConnected(ComponentName className, IBinder service) {
			Log.d(TAG, "onServiceConnected()");
			mFPAuthService = IFPAuthService.Stub.asInterface(service);
			try {
				mFPAuthService.registerCallback(mFPAuthServiceCallback);
			} catch (RemoteException e) {
				Log.d(TAG, "registerCallback fail, e =" + e.toString());
			}
		}

		public void onServiceDisconnected(ComponentName className) {
			Log.d(TAG, "onServiceDisConnected()");
			mFPAuthService = null;
		}

	};

	protected IFPAuthServiceCallback mFPAuthServiceCallback = new IFPAuthServiceCallback.Stub() {
		public void postMessage(int what, int arg1, int arg2) {
			mHandler.obtainMessage(what, arg1, arg2).sendToTarget();
		}
	};

	public void bind() {
		Log.d(TAG, "bind()");
		new Thread() {
			@Override
			public void run() {
				if (!(mContext.bindService(new Intent(IFPAuthService.class.getName()), mFPAuthConnection, Context.BIND_AUTO_CREATE)))
					Log.e(TAG, "bindService fail");
			}
		}.start();
	}

	private void unbind() {
		Log.d(TAG, "unbind()");

		if (mFPAuthService == null)
			return;
		try {
			mFPAuthService.unregisterCallback(mFPAuthServiceCallback);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		mContext.unbindService(mFPAuthConnection);
	}

	public void setEventListener(FingerprintEventListener l) {
		Log.d(TAG, "setEventListener()");
		mFingerprintEventListener = l;
	}

	/*
	 * information
	 */
	public String getVersion() {
		Log.d(TAG, "getVersion()");
		checkServiceConnected();

		try {
			return mFPAuthService.getVersion();
		} catch (RemoteException e) {
			Log.e(TAG, "getVersion() RemoteException:" + e.getMessage());
			return "RESULT_CANCELED";
		} catch (Exception e) {
			Log.e(TAG, "getVersion() Exception:" + e.getMessage());
			return "RESULT_CANCELED";
		}
	}

	public int getSensorStatus() {
		Log.d(TAG, "getSensorStatus()");
		checkServiceConnected();

		boolean ret;
		try {
			ret = mFPAuthService.IsSensorWorking();
			if (ret) {
				return SENSOR_WORKING;
			}
			ret = mFPAuthService.connectDevice();
			if (ret) {
				return SENSOR_OK;
			} else {
				return SENSOR_OUT_OF_ORDER;
			}
		} catch (RemoteException e) {
			Log.e(TAG, "getSensorStatus() RemoteException:" + e.getMessage());
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "getSensorStatus() Exception:" + e.getMessage());
			return RESULT_CANCELED;
		}
	}

	public int[] getFingerprintIndexList(String userId) {
		Log.d(TAG, "getFingerprintindexList() userId = " + userId);
		checkServiceConnected();

		try {
			String enrollList = mFPAuthService.getEnrollList(userId);
			Log.d(TAG, "enrollList=" + enrollList);
			if (enrollList == null || enrollList.equals("")) {
				return null;
			}
			String[] fingers = fpSplitor(enrollList);
			Arrays.sort(fingers);
			Log.d(TAG, "fingers=" + Arrays.toString(fingers));
			int[] fingerIndex = new int[fingers.length];
			for (int i = 0; i < fingerIndex.length; i++) {
				fingerIndex[i] = fingerIndexMap.get(fingers[i]);
			}
			return fingerIndex;
		} catch (RemoteException e) {
			Log.e(TAG, "getFingerprintindexList() RemoteException:" + e.getMessage());
			return null;
		} catch (Exception e) {
			Log.e(TAG, "getFingerprintindexList() Exception:" + e.getMessage());
			return null;
		}
	}

	private String[] fpSplitor(String fpData) {
		Log.d(TAG, "fpSplitor fpData = " + fpData);
		if (fpData.equals("")) {
			return null;
		}
		String[] fingerArray = null;
		if (fpData.contains(";")) {
			fingerArray = fpData.split(";");
		} else {
			fingerArray = new String[] { fpData };
		}
		return fingerArray;
	}

	public byte[] getFingerprintId(String userId, int index) {
		Log.d(TAG, "getFingerprintId() userId = " + userId + ", index = " + index);
		checkServiceConnected();

		/*
		 * the fingerprintId use key=fpid_userId_index to read/store data
		 */
		try {

			if ((userId == null) || userId.equals("") || (indexMapToFinger.get(index) == null)) {
				Log.e(TAG, "input parameter error");
				return null;
			}
			return mFPAuthService.DataRead(FPID_PREFIX + ";" + userId + ";" + indexMapToFinger.get(index), PASSWORD).getBytes();
		} catch (RemoteException e) {
			Log.e(TAG, "getFingerprintId() RemoteException:" + e.getMessage());
			return null;
		} catch (Exception e) {
			Log.e(TAG, "getFingerprintId() Exception:" + e.getMessage());
			return null;
		}
	}

	public String[] getUserIdList() {
		Log.d(TAG, "getUserIdList()");
		checkServiceConnected();

		try {
			return mFPAuthService.getUserIdList();
		} catch (RemoteException e) {
			Log.e(TAG, "getUserIdList() RemoteException:" + e.getMessage());
			return null;
		} catch (Exception e) {
			Log.e(TAG, "getUserIdList() Exception:" + e.getMessage());
			return null;
		}
	}

	public int getEnrollRepeatCount() {
		Log.d(TAG, "getEnrollRepeatCount()");
		checkServiceConnected();

		try {
			int[] enrollStatus = mFPAuthService.getEnrollStatus();
			if ((enrollStatus[0] == 0) && (enrollStatus[1] == 0) && (enrollStatus[2] == 0))
				return -1;
			return enrollStatus[0] + enrollStatus[1];
		} catch (RemoteException e) {
			Log.e(TAG, "getEnrollRepeatCount() RemoteException:" + e.getMessage());
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "getEnrollRepeatCount() Exception:" + e.getMessage());
			return RESULT_CANCELED;
		}
	}

	public String getSensorInfo() {
		Log.d(TAG, "getSensorInfo()");
		checkServiceConnected();

		try {
			return mFPAuthService.getSensorInfo();
		} catch (RemoteException e) {
			Log.e(TAG, "getSensorInfo() RemoteException:" + e.getMessage());
			e.printStackTrace();
			return "RESULT_CANCELED";
		} catch (Exception e) {
			Log.e(TAG, "getSensorInfo() Exception:" + e.getMessage());
			e.printStackTrace();
			return "RESULT_CANCELED";
		}
	}

	/*
	 * Setting
	 */
	public int setAccuracyLevel(int level) {
		Log.d(TAG, "setAccuracyLevel() level = " + level);
		checkServiceConnected();
		try {
			return mFPAuthService.setAccuracyLevel(level);
		} catch (RemoteException e) {
			Log.e(TAG, "setAccuracyLevel() RemoteException:" + e.getMessage());
			e.printStackTrace();
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "setAccuracyLevel() Exception:" + e.getMessage());
			e.printStackTrace();
			return RESULT_CANCELED;
		}
	}

	public int setEnrollSession(boolean flag) {
		Log.d(TAG, "setEnrollSession() flag = " + flag);

		mIsEnrollSessionOpen = flag;
		mIsFingerOrPWValidate = false;

		if (!mIsEnrollSessionOpen) {
			if (!hasPasswd(mEnrollUserId) && hasFinger(mEnrollUserId)) {
				try {
					int[] fIdxList = getFingerprintIndexList(mEnrollUserId);
					for (int idx : fIdxList) {
						String egisEnrollId = mEnrollUserId + ";" + indexMapToFinger.get(idx);
						if (!mFPAuthService.deleteFeature(egisEnrollId)) { // delete
																			// finger
							Log.e(TAG, "deleteFeature error, idx=" + idx);
							// return RESULT_FAILED;
						}
						mFPAuthService.DataDelete(FPID_PREFIX + ";" + mEnrollUserId + ";" + indexMapToFinger.get(idx), PASSWORD);
					}
				} catch (RemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			mEnrollUserId = null;
		}

		return RESULT_OK;
	}

	public int setPassword(String userId, byte[] pwdHash) {
		Log.d(TAG, "setPassword() userId = " + userId);

		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "no open enrollSession");
			return RESULT_NO_AUTHORITY;
		}

		checkServiceConnected();

		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;
		}

		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "no validate finger");
			return RESULT_NO_AUTHORITY;
		}

		boolean ret = false;
		try {
			ret = mFPAuthService.DataSet(PASSOWORD_PREFIX + ";" + userId, new String(pwdHash, "UTF-8"), PASSWORD);
			if (ret) {
				return RESULT_OK;
			} else {
				return RESULT_FAILED;
			}
		} catch (RemoteException e) {
			Log.e(TAG, "setPassword() RemoteException:" + e.getMessage());
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "setPassword() Exception:" + e.getMessage());
			return RESULT_CANCELED;
		}
	}

	public int verifyPassword(String userId, byte[] pwdHash) {
		Log.d(TAG, "verifyPassword() userId = " + userId);
		checkServiceConnected();

		try {
			String dbPwd = mFPAuthService.DataRead(PASSOWORD_PREFIX + ";" + userId, PASSWORD);
			if (dbPwd == null) {
				return RESULT_FAILED;
			}
			String keyInPw = new String(pwdHash, "UTF-8");
			if (keyInPw.equals(dbPwd)) {
				mIsFingerOrPWValidate = true;
				return RESULT_OK;
			} else {
				return RESULT_FAILED;
			}
		} catch (RemoteException e) {
			Log.e(TAG, "verifyPassword() RemoteException:" + e.getMessage());
			e.printStackTrace();
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "verifyPassword() Exception:" + e.getMessage());
			e.printStackTrace();
			return RESULT_CANCELED;
		}
	}

	/*
	 * Operation
	 */
	public int identify(String userId) {
		Log.d(TAG, "identify() userId = " + userId);

		if ((userId == null) || userId.equals("")) {
			return RESULT_FAILED;
		}
		checkServiceConnected();
		mIsFilterOn = false;
		boolean ret = false;
		try {
			ret = mFPAuthService.identify(userId);
			if (ret)
				return RESULT_OK;
			else
				return RESULT_FAILED;
		} catch (RemoteException e) {
			Log.e(TAG, "identify() RemoteException:" + e.getMessage());
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "identify() Exception:" + e.getMessage());
			return RESULT_CANCELED;
		}
	}

	public int enroll(String userId, int index) {
		Log.d(TAG, "enroll() userId = " + userId + ", index = " + index);

		if ((userId == null) || userId.equals("") || (indexMapToFinger.get(index) == null)) {
			Log.e(TAG, "user input error");
			return RESULT_FAILED;
		}

		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "no open enrollSession");
			return RESULT_NO_AUTHORITY;
		}

		checkServiceConnected();

		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;
		}

		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "no validate finger or password");
			return RESULT_NO_AUTHORITY;
		}
		mIsFilterOn = false;
		String egisId = userId + ";" + indexMapToFinger.get(index);
		Log.d(TAG, "egisId=" + egisId);
		boolean ret = false;
		try {
			ret = mFPAuthService.enroll(egisId);
			if (ret) {
				mEnrollUserId = userId;
				mEnrollIndex = index;
				return RESULT_OK;
			} else
				return RESULT_FAILED;
		} catch (RemoteException e) {
			Log.e(TAG, "enroll() RemoteException:" + e.getMessage());
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "enroll() Exception:" + e.getMessage());
			return RESULT_CANCELED;
		}
	}

	public int swipeEnroll(String userId, int index) {
		Log.d(TAG, "swipeEnroll() userId = " + userId + ", index = " + index);

		if ((userId == null) || userId.equals("") || (indexMapToFinger.get(index) == null)) {
			Log.e(TAG, "user input error");
			return RESULT_FAILED;
		}

		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "no open enrollSession");
			return RESULT_NO_AUTHORITY;
		}

		checkServiceConnected();

		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;
		}

		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "no validate finger or password");
			return RESULT_NO_AUTHORITY;
		}
		mIsFilterOn = false;
		String egisId = userId + ";" + indexMapToFinger.get(index);
		Log.d(TAG, "egisId=" + egisId);
		boolean ret = false;
		try {
			ret = mFPAuthService.enroll(egisId);
			if (ret) {
				mEnrollUserId = userId;
				mEnrollIndex = index;
				return RESULT_OK;
			} else
				return RESULT_FAILED;
		} catch (RemoteException e) {
			Log.e(TAG, "swipeEnroll() RemoteException:" + e.getMessage());
			return RESULT_CANCELED;
		} catch (Exception e) {
			Log.e(TAG, "swipeEnroll() Exception:" + e.getMessage());
			return RESULT_CANCELED;
		}
	}

	private boolean hasFinger(String userId) {
		String enrollList = null;
		try {
			enrollList = mFPAuthService.getEnrollList(userId);
			Log.d(TAG, "enrollList=" + enrollList);
		} catch (RemoteException e1) {
			e1.printStackTrace();
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return (enrollList != null && !enrollList.equals(""));
	}

	private boolean hasPasswd(String userId) {
		Log.d(TAG, "hasPasswd() userId = " + userId);
		try {
			if (mFPAuthService.DataRead(PASSOWORD_PREFIX + ";" + userId, PASSWORD) == null) {
				Log.d(TAG, "DataRead false");
				return false;
			}
			return true;
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return false;
	}

	public int remove(String userId, int index) {
		Log.d(TAG, "remove userId = " + userId + "index = " + index);

		if ((userId == null) || userId.equals("") || (indexMapToFinger.get(index) == null)) {
			Log.e(TAG, "user input error");
			return RESULT_FAILED;
		}

		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "no open enrollSession");
			return RESULT_NO_AUTHORITY;
		}

		checkServiceConnected();

		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;
		}

		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "no validate finger or password");
			return RESULT_NO_AUTHORITY;
		}

		if (!hasFinger(userId)) {
			Log.e(TAG, "no finger");
			return RESULT_FAILED;
		}

		try {
			String egisEnrollId = userId + ";" + indexMapToFinger.get(index);
			if (!mFPAuthService.deleteFeature(egisEnrollId)) { // delete finger
				Log.e(TAG, "deleteFeature error");
				return RESULT_FAILED;
			}
			/*
			 * delete fid
			 */
			String enrollList = null;
			mFPAuthService.DataDelete(FPID_PREFIX + ";" + userId + ";" + indexMapToFinger.get(index), PASSWORD);
			enrollList = mFPAuthService.getEnrollList(userId);
			Log.d(TAG, "enrollList=" + enrollList);
			if (enrollList == null || enrollList.equals("")) {
				Log.d(TAG, "+++++ no finger, so delete password");
				mFPAuthService.DataDelete(PASSOWORD_PREFIX + ";" + userId, PASSWORD);
			}
			return RESULT_OK;
		} catch (RemoteException e) {
			Log.e(TAG, "remove() RemoteException:" + e.getMessage());
			e.printStackTrace();
			return RESULT_FAILED;
		} catch (Exception e) {
			Log.e(TAG, "remove() Exception:" + e.getMessage());
			return RESULT_FAILED;
		}
	}

	public int request(int status) {
		Log.d(TAG, "request(), status = " + status);

		if (status != PAUSE_ENROLL && status != RESUME_ENRORLL) {
			Log.e(TAG, "input parameter error status=" + status);
			return RESULT_FAILED;
		}

		checkServiceConnected();

		int ret = 0;
		try {
			ret = mFPAuthService.sensorControl(status);
		} catch (RemoteException e) {
			Log.e(TAG, "request() RemoteException:" + e.getMessage());
			e.printStackTrace();
			return RESULT_FAILED;
		} catch (Exception e) {
			Log.e(TAG, "request() Exception:" + e.getMessage());
			return RESULT_FAILED;
		}
		return ret;
	}

	/*
	 * Cleanup
	 */
	public int cancel() {
		Log.d(TAG, "cancel()");
		checkServiceConnected();

		boolean ret = false;
		try {
			ret = mFPAuthService.abort();
			if (ret) {
				return RESULT_OK;
			} else {
				return RESULT_FAILED;
			}
		} catch (RemoteException e) {
			Log.e(TAG, "cancel() RemoteException:" + e.getMessage());
			return RESULT_FAILED;
		} catch (Exception e) {
			Log.e(TAG, "cancel() Exception:" + e.getMessage());
			return RESULT_FAILED;
		}
	}

	private void checkServiceConnected() {
		boolean ret = false;
		try {
			mFPAuthService.checkServiceException();
		} catch (RemoteException e) {
			e.printStackTrace();
			Log.e(TAG, "checkServiceConnected() Exception:" + e.getMessage());
			ret = true;
		} catch (Exception e) {
			Log.e(TAG, "checkServiceConnected() Exception:" + e.getMessage());
			ret = true;
		} finally {
			if (ret) {
				bind();
			}
		}
	}

	private void notifyOnEnrollStatus() {
		if (mFingerprintEventListener != null) {
			int[] enrollInfo = null;
			try {
				enrollInfo = mFPAuthService.getEnrollStatus();
			} catch (RemoteException e) {
				Log.e(TAG, "abort() RemoteException:" + e.getMessage());
			} catch (Exception e) {
				Log.e(TAG, "abort() Exception:" + e.getMessage());
			}
			EnrollStatus enrollStatus = new EnrollStatus();
			enrollStatus.successTrial = enrollInfo[0];
			enrollStatus.badTrial = enrollInfo[1];
			enrollStatus.totalTrial = enrollInfo[0] + enrollInfo[1];
			enrollStatus.progress = enrollInfo[2];
			mFingerprintEventListener.onFingerprintEvent(ENROLL_STATUS, enrollStatus);
		}
	}

	private void notifyOnEnrollMap() {
		if (mFingerprintEventListener != null) {
			try {

				EnrollBitmap enrollMap = new EnrollBitmap();
				int[] mapInfo = mFPAuthService.getTinyMapInfo();
				Log.d(TAG, "mapInfo[1]=" + mapInfo[1] + " mapInfo[2]=" + mapInfo[2]);
				if (mapInfo[1] != MAP_W && mapInfo[2] != MAP_H)
					return;

				byte[] map = mFPAuthService.getTinyMap();
				for (int index = 0; index < map.length; index++) {
					map[index] = (byte) ((map[index] == -1) ? 0 : 255);
				}
				Bitmap bitmap = Bitmap.createBitmap(mapInfo[1], mapInfo[2], Bitmap.Config.ALPHA_8);
				bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(map));
				enrollMap.enrollMap = bitmap;
				mFingerprintEventListener.onFingerprintEvent(ENROLL_BITMAP, enrollMap);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void notifyOnBadImage(int quality) {
		try {
			if (mFingerprintEventListener != null) {
				byte[] rawData = null;
				int[] rawDataInfo = null;
				try {
					rawData = mFPAuthService.getMatchedImg();
					rawDataInfo = mFPAuthService.getMatchedImgInfo();
					FingerprintBitmap fpBitmap = new FingerprintBitmap();
					fpBitmap.width = rawDataInfo[0];
					fpBitmap.height = rawDataInfo[1];
					fpBitmap.quality = quality;
					fpBitmap.bitmap = Bitmap.createBitmap(rawDataInfo[0], rawDataInfo[1], Bitmap.Config.ALPHA_8);
					for (int index = 0; index < rawData.length; index++) {
						rawData[index] = (byte) ~rawData[index];
					}
					fpBitmap.bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
					Log.d(TAG, "rawData len=" + rawData.length);
					mFingerprintEventListener.onFingerprintEvent(CAPTURE_FAILED, fpBitmap);
				} catch (RemoteException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					Log.e(TAG, "doOnGetRawData get null pointer");
					e.printStackTrace();
				}
			}
			/*
			 * if(mFPAuthService.getOperationType() == OP_TYPE_ENROLL){
			 * notifyOnEnrollStatus(); }else
			 */if (mFPAuthService.getOperationType() == OP_TYPE_VERIFY) {
				mIsFilterOn = true;
				IdentifyResult identifyResult = new IdentifyResult();
				identifyResult.index = -1;
				identifyResult.result = IDENTIFY_FAILURE_BAD_QUALITY;
				mFingerprintEventListener.onFingerprintEvent(IDENTIFY_FAILED, identifyResult);
				mFPAuthService.abort();
			}
		} catch (RemoteException e1) {
			e1.printStackTrace();
		}
	}

	private final Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case FpResDef.FP_RESULT: {
				switch (msg.arg1) {
				case FpResDef.FP_RES_ENROLL_COUNT: {
					Log.d(TAG, "FpResDef.FP_RES_ENROLL_COUNT");
					notifyOnEnrollStatus();
					break;
				}
				case FpResDef.FP_RES_ENROLL_OK:
					Log.d(TAG, "FpResDef.FP_RES_ENROLL_OK");

					/*
					 * Generate a fid for this fingerprint
					 */
					try {
						mFPAuthService.DataSet(FPID_PREFIX + ";" + mEnrollUserId + ";" + indexMapToFinger.get(mEnrollIndex), UUID.randomUUID().toString(), PASSWORD);
					} catch (RemoteException e1) {
						e1.printStackTrace();
					}
					if (mFingerprintEventListener != null) {
						mFingerprintEventListener.onFingerprintEvent(ENROLL_SUCCESS, null);
					}
					// mEnrollUserId = null;
					mEnrollIndex = 0;
					break;
				case FpResDef.FP_RES_ENROLL_FAIL:
					Log.d(TAG, "FpResDef.FP_RES_ENROLL_FAIL");

					if (mFingerprintEventListener != null) {
						mFingerprintEventListener.onFingerprintEvent(ENROLL_FAILED, null);
					}
					// mEnrollUserId = null;
					mEnrollIndex = 0;
					break;
				case FpResDef.FP_RES_MATCHED_OK:
					Log.d(TAG, "FpResDef.FP_RES_MATCHED_OK");

					mIsFingerOrPWValidate = true;
					if (mFingerprintEventListener != null) {
						IdentifyResult identifyResult = new IdentifyResult();
						String egisId;
						try {
							egisId = mFPAuthService.getMatchedUserID();
							String[] idWithIndex = egisId.split(";");
							Log.d(TAG, "user=" + idWithIndex[0] + " index=" + idWithIndex[1]);
							identifyResult.index = fingerIndexMap.get(idWithIndex[1]);
							identifyResult.result = IDENTIFY_SUCCESS;
							mFingerprintEventListener.onFingerprintEvent(IDENTIFY_SUCCESS, identifyResult);
						} catch (RemoteException e) {
							e.printStackTrace();
						}
					}
					break;
				case FpResDef.FP_RES_MATCHED_FAIL:
					Log.d(TAG, "FpResDef.FP_RES_MATCHED_FAIL");

					if (mFingerprintEventListener != null) {
						IdentifyResult identifyResult = new IdentifyResult();
						identifyResult.index = -1;
						identifyResult.result = IDENTIFY_FAILURE_NOT_MATCH;
						mFingerprintEventListener.onFingerprintEvent(IDENTIFY_FAILED, identifyResult);
					}
					break;
				case FpResDef.FP_RES_GETTING_IMAGE:

					break;
				case FpResDef.FP_RES_FINGER_WAIT_FPON:
					Log.d(TAG, "FpResDef.FP_RES_FINGER_WAIT_FPON");
					if (mFingerprintEventListener != null) {
						if (mIsFilterOn)
							break;
						mFingerprintEventListener.onFingerprintEvent(CAPTURE_READY, null);
					}
					break;
				case FpResDef.FP_RES_FINGER_DETECTED:
					Log.d(TAG, "FpResDef.FP_RES_FINGER_DETECTED");

					if (mFingerprintEventListener != null) {
						mFingerprintEventListener.onFingerprintEvent(CAPTURE_STARTED, null);
					}
					break;
				case FpResDef.FP_RES_FINGER_REMOVED:
					Log.d(TAG, "FpResDef.FP_RES_FINGER_REMOVED");

					if (mFingerprintEventListener != null) {
						if (mIsFilterOn || Fingerprint.m_abort)
							break;
						mFingerprintEventListener.onFingerprintEvent(CAPTURE_FINISHED, null);
					}
					break;
				case FpResDef.FP_RES_GETTED_GOOD_IMAGE:
					Log.d(TAG, "FpResDef.FP_RES_GETTED_GOOD_IMAGE");

					// doOnGetRawData(QUALITY_GOOD);
					if (mFingerprintEventListener != null) {
						byte[] rawData = null;
						int[] rawDataInfo = null;
						try {
							rawData = mFPAuthService.getMatchedImg();
							Log.d(TAG, "rawData len=" + rawData.length);
							rawDataInfo = mFPAuthService.getMatchedImgInfo();
							FingerprintBitmap fpBitmap = new FingerprintBitmap();
							fpBitmap.width = rawDataInfo[0];
							fpBitmap.height = rawDataInfo[1];
							fpBitmap.quality = QUALITY_GOOD;
							fpBitmap.bitmap = Bitmap.createBitmap(rawDataInfo[0], rawDataInfo[1], Bitmap.Config.ALPHA_8);
							for (int index = 0; index < rawData.length; index++) {
								rawData[index] = (byte) ~rawData[index];
							}
							fpBitmap.bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
							// mFingerprintEventListener.onFingerprintEvnet(CAPTURE_COMPLETED,
							// fpBitmap);
							mFingerprintEventListener.onFingerprintEvent(CAPTURE_SUCCESS, fpBitmap);
						} catch (RemoteException e) {
							e.printStackTrace();
						} catch (NullPointerException e) {
							Log.e(TAG, "doOnGetRawData get null pointer");
							e.printStackTrace();
						}
					}
					break;
				case FpResDef.FP_RES_GETTED_BAD_IMAGE:
					Log.d(TAG, "FpResDef.FP_RES_GETTED_BAD_IMAGE");
					notifyOnBadImage(QUALITY_FAILED);
					break;
				case FpResDef.FP_RES_PARTIAL_IMG:
					Log.d(TAG, "FpResDef.FP_RES_PARTIAL_IMG");
					notifyOnBadImage(QUALITY_PARTIAL_TOUCH);
					break;
				case FpResDef.FP_RES_ABORT_OK:
					Log.d(TAG, "FpResDef.FP_RES_ABORT_OK");

					try {
						if (mIsFilterOn) {
							// mIsFilterOn = false;
							break;
						}
						switch (mFPAuthService.getOperationType()) {
						case OP_TYPE_ENROLL:
							mFingerprintEventListener.onFingerprintEvent(ENROLL_FAILURE_CANCELED, null);
							break;
						case OP_TYPE_VERIFY:
							IdentifyResult identifyResult = new IdentifyResult();
							identifyResult.result = IDENTIFY_FAILURE_CANCELED;
							mFingerprintEventListener.onFingerprintEvent(IDENTIFY_FAILED, identifyResult);
							break;
						}
					} catch (RemoteException e) {
						e.printStackTrace();
					}
					break;
				}
			}
			case FpResDef.TINY_STATUS: {
				switch (msg.arg1) {
				case FpResDef.TINY_STATUS_ENROLL_MAP:
					Log.d(TAG, "FpResDef.TINY_STATUS_ENROLL_MAP");
					notifyOnEnrollMap();
					break;
				case FpResDef.TINY_STATUS_HIGHLY_SIMILAR:
					Log.d(TAG, "FpResDef.TINY_STATUS_HIGHLY_SIMILAR");
					break;
				}
			}
			}
		}
	};

	@Override
	public int request(int status, Object obj) {
		return 0;
	}

	@Override
	public int verifySensorState(int cmd, int sId, int opt, int logOpt, int uId) {
		return 0;
	}

	@Override
	public int cleanup() {
		return 0;
	}

	@Override
	public int enableSensorDevice(boolean enable) {
		return 0;
	}

	@Override
	public int eeprom_test(int cmd, int addr, int value) {
		return 0;
	}
}
