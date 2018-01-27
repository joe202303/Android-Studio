package egis.finger.host;
  
import java.util.Arrays;
import java.io.File;
import android.content.Context;
import android.os.Handler;
import android.util.Log;
import egis.client.api.EgisFingerprint;
import egis.client.api.FpResDef;
import egis.finger.host.CipherManager.CipherType;

public class FPNativeBase {
	protected static final String TAG = "FpCsaClientLib_FPNativeBase";

	private static Context mContext;

	/* DB operation */
	CipherManager cipherManager;
	protected static FingerUtil mFingerUtil;
	protected static String mEnrollUserID = "Empty@";
	protected static Handler mApHandler;
	protected static String mEnrollListID;
	protected static String mMatchedID;
	protected static int lastErrCode;
	public static boolean isSaveDataSet = false;

	private enum AccuracyLevel {ACCURACY_LOW, ACCURACY_REGULAR, ACCURACY_HIGH,
		ACCURACY_VERY_HIGH};

	protected static int mMapH;
    protected static int mMapW;
    protected static int mMapIdx;
    protected static byte[] mMap;

    protected static byte[] mFPVerifyImg;
	protected static int mWidthImg;
	protected static int mHeightImg;

    protected static byte[] mFPMatchedImg;
	protected static int mMatchedWidthImg;
	protected static int mMatchedHeightImg;

	protected static int mOperationType = 0;
	public static final int OP_TYPE_ENROLL = 101;
	public static final int OP_TYPE_VERIFY = 102;

	protected static int mEnrollProgress = 0;
	private static int mSuccessTrial = 0;
	private static int mBadTrial = 0;

	/* Return code of main_control.h in SDK */
	protected static final int VKX_RESULT_SUCCESS = 0;
	protected static final int VKX_RESULT_ENROLL_FAIL = -1001;

	/* Callback event of API-JNI.c in SDK */
	protected static final int EVENT_ENROLL_PROGRESS = 1;
	protected static final int EVENT_STATUS = 2;
	protected static final int EVENT_GET_IMAGE = 3;
	protected static final int EVENT_GET_FEATURE = 4;
	protected static final int EVENT_GET_BASE64_FEATURE = 5;
	protected static final int EVENT_GET_VERIFY_IMG = 7;
	protected static final int EVENT_ENROLL_MAP_PROGRESS = 8;
	protected static final int EVENT_ENROLL_CANDIDATE_COUNT = 9;
	protected static final int EVENT_GET_MATCHED_IMAGE = 10;
	protected static final int EVENT_GET_VERIFY_RESULT = 11;
	protected static final int EVENT_ENROLL_LEARNING	 = 12;

	/* Status code of main_control.h in SDK */
	protected static final int STATUS_SENSOR_OPEN = 1;
    protected static final int STATUS_SENSOR_CLOSE = 2;
    protected static final int STATUS_IMAGE_FETCH = 3;
    protected static final int STATUS_IMAGE_READY = 4;
    protected static final int STATUS_IMAGE_BAD = 5;
    protected static final int STATUS_FEATURE_LOW = 6;
    protected static final int STATUS_OPERATION_BEGIN = 7;
    protected static final int STATUS_OPERATION_END = 8;
    protected static final int STATUS_IMAGE_FETCHING = 9;
    protected static final int STATUS_FINGER_DETECTED = 10;
    protected static final int STATUS_FINGER_REMOVED = 11;
    protected static final int STATUS_SWIPE_TOO_FAST = 12;
    protected static final int STATUS_SWIPE_TOO_SLOW = 13;
    protected static final int STATUS_SWIPE_TOO_SHORT = 14;
    protected static final int STATUS_SWIPE_TOO_SKEWED = 15;
    protected static final int STATUS_SWIPE_TOO_LEFT = 16;
    protected static final int STATUS_SWIPE_TOO_RIGHT = 17;
    protected static final int STATUS_SENSOR_UNPLUG = 18;
    protected static final int STATUS_USER_TOO_FAR = 19;
    protected static final int STATUS_USER_TOO_CLOSE = 20;
    protected static final int STATUS_LUX_TOO_LOWER = 21;
    protected static final int STATUS_LUX_TOO_HIGHER = 22;
    protected static final int STATUS_FINGER_TOUCH = 23;
    protected static final int STATUS_FINGER_REMOVE = 24;
    protected static final int STATUS_SWIPE_TOO_WET = 25;
    protected static final int STATUS_SWIPE_TOO_DRY = 26;
    protected static final int STATUS_SENSOR_TIMEOUT = 27;
    protected static final int STATUS_USER_ABORT = 28;
    protected static final int STATUS_GET_IMAGE_FAIL = 29;
    protected static final int STATUS_SWIPE_IMAGE_BAD = 30;
    protected static final int STATUS_DIRTY_IMAGE = 31;
    protected static final int STATUS_TARGET_SENSOR_NOT_FOUND = 32;
    protected static final int STATUS_IMAGE_SMALL = 33;
    protected static final int STATUS_ENROLL_MAP = 34;
    protected static final int STATUS_SELECT_CANDIDATE = 35;
    protected static final int STATUS_DELETED_CANDIDATE 	= 36;
    protected static final int STATUS_DUPLICATED_CANDIDATE = 37;
    protected static final int STATUS_REMOVE_CANDIDATE = 38;
    protected static final int STATUS_MOVE_CANDIDATE = 39;
    protected static final int STATUS_SELECT_IMAGE = 40;
    protected static final int STATUS_ADD_CANDIDATE = 41;
    protected static final int STATUS_BEFORE_GENERALIZE = 42;
    protected static final int STATUS_WAIT_FPON = 44;
    protected static final int STATUS_IMAGE_PARTIAL = 46;
    protected static final int STATUS_IMAGE_WATER = 47;
	protected static final int STATUS_TEST_ENROLL_OK = 60;
	protected static final int STATUS_TEST_ENROLL_FAIL = 61;
	protected static final int STATUS_TEST_VERIFY_OK = 62;
	protected static final int STATUS_TEST_VERIFY_FAIL = 63;

    /* EEPROM status of sensor_write_eeprom.h in SDK */
    protected static final int EEPROM_STATUS_OPERATION_END	= 101;

    /* Operation of main_control.c in SDK, but this lib only by pass it. */
	public static final int PAUSE_ENROLL = 2010;
	public static final int RESUME_ENRORLL = 2011;
 	public static final int SENSOR_TEST_NORMALSCAN_COMMAND = 100103;
	public static final int SENSOR_TEST_SNR_ORG_COMMAND = 100106;
	public static final int SENSOR_TEST_SNR_FINAL_COMMAND = 100107;

    /* Callback event of sensor test status in SDK */
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START =
			0x11000003;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END = 0x11000004;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_PUT = 0x11000005;
	public static final int EVT_ERROR = 2020;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_START = 2031;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_END = 2032;

	/* Native API */
	public native int Init();
	protected native int EnrollFinger();
	protected native int SwipeEnroll();
	public native int SetAccuracyLevel(int level);
	public native int NativeSensorControl(int request, int interrupt_timeout);
	public native int Cleanup();
	protected native static int AbortOperation();
	protected native int VerifyAllTemplate(byte[][] features, int[] matchRtn);
	protected native String GetVersion();
	protected native int GetSensorStatus();
	protected native String GetSensorInfo();
	protected native int SensorDeviceEnable(boolean enable);
	protected native int EepromTest(int cmd, int address, int value);
	protected native int TestEnrollmentAndVerification(int isEnroll);

	String getLibPath(Context context) {
		String packagePath = "/data/data/" +
				context.getApplicationContext().getPackageName() +
				"/lib/libEgisDevice.so";
		String systemLibPath = "/system/lib/libEgisDevice.so";
		String systemLib64Path = "/system/lib64/libEgisDevice.so";

		File file = new File(packagePath);
		if (file.exists() == true)
			return packagePath;

		file = new File(systemLib64Path);
		if (file.exists() == true)
			return systemLib64Path;

		file = new File(systemLibPath);
		if (file.exists() == true)
			return systemLibPath;

		return null;
	}
	
	String getTZLibPath(Context context) {
		String packagePath = "/data/data/" +
				context.getApplicationContext().getPackageName() +
				"/lib/libEgisDeviceTZ.so";
		String systemLibPath = "/system/lib/libEgisDeviceTZ.so";
		String systemLib64Path = "/system/lib64/libEgisDeviceTZ.so";

		File file = new File(packagePath);
		if (file.exists() == true)
			return packagePath;

		file = new File(systemLib64Path);
		if (file.exists() == true)
			return systemLib64Path;

		file = new File(systemLibPath);
		if (file.exists() == true)
			return systemLibPath;

		return null;
	}

	public FPNativeBase(Handler handle, Context context) {
		final byte[] key = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xa, 0xb,
				0xc, 0xd, 0xe, 0xf};
		final byte[] iv = {0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 0xa, 0xb,
				0xc, 0xd, 0xe, 0xf};

		mApHandler = handle;
		mContext = context;

		mFingerUtil = new FingerUtil(mApHandler, mContext);

		cipherManager = new CipherManager(CipherType.AES);
		cipherManager.init(key, iv);

		String libPath = getLibPath(context);
		Log.d(TAG, "FPNativeBase(), path = " + libPath);

		String libPathTZ = getTZLibPath(context);
		Log.d(TAG, "FPNativeBase(), TZ path = " + libPathTZ);

	   	if (libPath != null && libPathTZ != null) {
	   		System.load(libPathTZ);
    		System.load(libPath);
			Init();
    	}
    }
	
	private static void resetEnrollStatus() {
		Log.d(TAG, "resetEnrollStatus()");
		mEnrollProgress = mSuccessTrial = mBadTrial = 0;
		mFingerUtil.mFeature = null;
    }
	
	public String getVersion() {
        String version = GetVersion();
        Log.d(TAG, "getVersion(), " + version);
		return version;
    }

	public int getSensorStatus() {
		int data = GetSensorStatus();
		Log.d(TAG, "getSensorStatus(), " + data);
		return data;
    }

	public String getEnrollList(String userID) {
		String uid = (userID.contentEquals("*")) ? "*" : "*" + userID;
		mEnrollListID = mFingerUtil.getEnrollListFromDB(uid);
    	mApHandler.obtainMessage(FpResDef.FP_RESULT,
                FpResDef.FP_RES_FINGER_LIST, -1).sendToTarget();
    	Log.d(TAG, "getEnrollList(), userID = " + userID + ", mEnrollListID = "
                + mEnrollListID);
    	return mEnrollListID;
	}
	
	public String dataRead(String id, String pwd) {
		Log.d(TAG, "dataRead(), id = " + id + ", pwd = " + pwd);
		if (cipherManager == null) {
			Log.e(TAG, "dataRead(), ciphermanager == null");
			lastErrCode = FpResDef.SEVC_ERR_CM_NULL;
			return null;
		}
		if (id == null) {
			Log.e(TAG, "dataRead(), id == null");
			lastErrCode = FpResDef.SEVC_ERR_ID_NULL;
			return null;
		}
		if (pwd == null) {
			Log.e(TAG, "dataRead(), pwd == null");
			lastErrCode = FpResDef.SEVC_ERR_PWD_NULL;
			return null;
		}
		
		if (mFingerUtil.dataRead(id) == null) {
			Log.e(TAG, "dataRead(), requested data does not exist");
			return null;
		}
		
		if (cipherManager.setKey(pwd) == false) {
			Log.e(TAG, "dataRead(),  setKey fail");
			return null;
		}
		
		String decryptionData = cipherManager.decryptData(
				mFingerUtil.dataRead(id));
		if (decryptionData == null) {
			Log.e(TAG, "dataRead(),  decryptData fail");
			return null;
		}
		
		if (cipherManager.validateData(decryptionData) == false) {
			Log.e(TAG, "dataRead(), password is incorrect !");
			lastErrCode = FpResDef.SEVC_ERR_PWD_INCORRECT;
			return null;
		}

		decryptionData = cipherManager.unpackageData(decryptionData);
		Log.d(TAG, "dataRead(), data = " + decryptionData);
		return decryptionData;
	}
	
	public String[] getUserIdList() {
		String[] data = mFingerUtil.getUserIdList();
		Log.d(TAG, "getUserIdList(), " + Arrays.toString(data));
		return data;
	}
	
	public int[] getEnrollStatus() {
		int[] enrollStatus = new int[3];
		enrollStatus[0] = mSuccessTrial;
		enrollStatus[1] = mBadTrial;
		enrollStatus[2] = mEnrollProgress;
		Log.d(TAG, "getEnrollStatus() SuccessTrial = " + enrollStatus[0]
				+ ", BadTrial=" + enrollStatus[1] + ", EnrollProgress = "
				+ enrollStatus[2]);
		return enrollStatus;
	}
	
	public String getSensorInfo() {
		String data = GetSensorInfo();
		Log.d(TAG, "getSensorInfo(), " + data);
		return data;
    }
	
	public int sensorDeviceEnable(boolean enable) {
		return SensorDeviceEnable(enable);
	}
	
	public int eepromTest(int cmd, int address, int value) {
		return EepromTest(cmd, address, value);
	}
	
	public int setAccuracyLevel(int accuracyLevel) {
		Log.d(TAG, "setAccuracyLevel(), " + accuracyLevel);
        if (accuracyLevel < EgisFingerprint.ACCURACY_LOW) {
            accuracyLevel = EgisFingerprint.ACCURACY_LOW;
        } else if (accuracyLevel > EgisFingerprint.ACCURACY_VERY_HIGH) {
            accuracyLevel = EgisFingerprint.ACCURACY_VERY_HIGH;
        }
        return SetAccuracyLevel(accuracyLevel);
    }

	public boolean dataSet(String id, String value, String password) {
		Log.d(TAG, "dataSet(), id =  " + id + " value = " + value +
				" password = " + password);
		if (cipherManager == null) {
			Log.e(TAG, "dataSet(), ciphermanager == null");
			lastErrCode = FpResDef.SEVC_ERR_CM_NULL;
			return false;
		}
		if (id == null) {
			Log.e(TAG, "dataSet(), id == null");
			lastErrCode = FpResDef.SEVC_ERR_ID_NULL;
			return false;
		}
		if (value == null) {
			Log.e(TAG, "dataSet(), value == null");
			lastErrCode = FpResDef.SEVC_ERR_VALUE_NULL;
			return false;
		}
		if (password == null) {
			Log.e(TAG, "dataSet password == null");
			lastErrCode = FpResDef.SEVC_ERR_PWD_NULL;
			return false;
		}
		
		if (cipherManager.setKey(password) == false) {
			Log.e(TAG, "dataSet(), setKey fail");
			return false;
		}
		String encryptionData = cipherManager.encryptData(
				cipherManager.packageData(value));
		if (encryptionData == null) {
			Log.e(TAG, "dataSet(), encryptData fail");
			return false;
		}
		return mFingerUtil.dataSet(id, encryptionData);
	}

	public boolean identify(String userId) {
		try {
    		mOperationType = OP_TYPE_VERIFY;
			// matchRtn[0] = ID, matchRtn[1] = maxScore
    		int[] matchRtn = new int[] {-1, 0};
    		byte[][] allFeature = mFingerUtil.getAllFeature(userId);
    		return verifyAll(allFeature, matchRtn);
    	} catch(Exception e) {
    		Log.e(TAG, "identify(), exception = " + e.getMessage());
    		return false;
    	}
	}
	
	public boolean verifyAll(byte[][] allEnrollTemplate, int[] matchRtn) {
    	try {
    		int res = VerifyAllTemplate(allEnrollTemplate.clone(), matchRtn);
    		if (res == VKX_RESULT_SUCCESS) {
    			return true;
    		} else {
    			return false;
    		}
    	} catch(Exception e) {
    		Log.e(TAG, "verifyAll() exception = " + e.getMessage());
    		return false;
    	}  
    }
	
	public boolean enroll(String userID) {
		Log.d(TAG, "enroll(), " + userID);
		if (userID == null) {
			Log.e(TAG, "enroll(), userID == null");
			return false;
		}
		
    	mEnrollUserID = "*" + userID;
    	mOperationType = OP_TYPE_ENROLL;
    	Log.d(TAG, "mEnrollUserID = " + mEnrollUserID);
    	
    	try {
			int ret = EnrollFinger();
    		if (ret == VKX_RESULT_SUCCESS) {
	    		resetEnrollStatus();
	        	return true;
	    	}
	    	Log.e(TAG, "enroll(), fail " + ret);
			return false;
    	} catch(Exception e) {
    		Log.e(TAG, "enroll() exception = " + e.getMessage());
    		return false;
    	}
	}

	public boolean swipeEnroll(String userID) {
		Log.d(TAG, "swipeEnroll(), " + userID);
		if (userID == null) {
			Log.e(TAG, "swipeEnroll(), userID == null");
			return false;
		}

		mEnrollUserID = "*" + userID;
		mOperationType = OP_TYPE_ENROLL;
		Log.d(TAG, "mEnrollUserID = " + mEnrollUserID);

		try {
			int ret = SwipeEnroll();
			if (ret == VKX_RESULT_SUCCESS) {
				resetEnrollStatus();
				return true;
			}
			Log.e(TAG, "swipeEnroll(), fail " + ret);
			return false;
		} catch(Exception e) {
			Log.e(TAG, "swipeEnroll() exception = " + e.getMessage());
			return false;
		}
	}
	
	public boolean deleteFeature(String userID) {
		Log.d(TAG, "deleteFeature(), " + userID);
		if (mFingerUtil == null) {
			Log.d(TAG, "deleteFeature(), FingerUtil == null");
			lastErrCode = FpResDef.NB_ERR_FINGER_UTIL_NULL;
			return false; 
		}
		if (userID == null) {
			Log.e(TAG, "deleteFeature(), userID == null");
			lastErrCode = FpResDef.NB_ERR_USER_ID_NULL;
			return false;
		}
		if (userID.length() == 0) {
			Log.d(TAG, "deleteFeature(), userID length==0");
			lastErrCode = FpResDef.NB_ERR_USER_ID_LEN_ZERO;
			return false;
		}

		String uid = userID.contentEquals("*") ? "*" : "*" + userID;
		boolean res = mFingerUtil.deleteFromDB(uid);
		if (res) {
			mApHandler.obtainMessage(FpResDef.FP_RESULT,
					FpResDef.FP_RES_DELETE_OK, -1).sendToTarget();
		} else {
			mApHandler.obtainMessage(FpResDef.FP_RESULT,
					FpResDef.FP_RES_DELETE_FAIL, -1).sendToTarget();
		}
		return res;
	}
	
	public boolean dataDelete(String id, String password) {
		Log.d(TAG, "dataDelete(), id = " + id + " password = " + password);
		if (cipherManager == null) {
			Log.e(TAG, "dataDelete(), ciphermanager == null");
			lastErrCode = FpResDef.SEVC_ERR_CM_NULL;
			return false;
		}
		if (id == null) {
			Log.e(TAG, "dataDelete(), id == null");
			lastErrCode = FpResDef.SEVC_ERR_ID_NULL;
			return false;
		}
		if (password == null) {
			Log.e(TAG, "dataDelete(), password == null");
			lastErrCode = FpResDef.SEVC_ERR_PWD_NULL;
			return false;
		}
		
		if (mFingerUtil.dataRead(id) == null) {
			Log.e(TAG, "dataDelete(), requested data does not exist");
			return false;
		}
		
		if (cipherManager.setKey(password) == false) {
			Log.e(TAG, "dataDelete(), setKey fail");
			return false;
		}
		
		String decryptionData = cipherManager.decryptData(
				mFingerUtil.dataRead(id));
		
		if (decryptionData == null) {
			Log.e(TAG, "dataDelete(), decryptData fail");
			lastErrCode = FpResDef.SEVC_ERR_VALUE_NULL;
			return false;
		}
		
		if (cipherManager.validateData(decryptionData) == false) {
			Log.e(TAG, "dataDelete(), password is incorrect");
			lastErrCode = FpResDef.SEVC_ERR_VALUE_NULL;
			return false;
		}
		return mFingerUtil.dataDelete(id);
    }
	
	public int sensorControl(int request, int interrupt_timeout)
	{
		Log.d(TAG, "sensorControl(), " + request + ", " + interrupt_timeout);
		try {
			return NativeSensorControl(request, interrupt_timeout);
		} catch(Exception e) {
    		Log.e(TAG, "sensorControl(), exception = " + e.getMessage());
    		return -1;
    	}
	}
	
	public boolean abort() {
    	Log.d(TAG, "abort()");
		int ret = AbortOperation();
       	if (ret == VKX_RESULT_SUCCESS) {
    		resetEnrollStatus();
    		return true;
    	}
    	Log.e(TAG, "abort(),  fail " + ret);
    	return false;
    }
	
	public int cleanup()
	{
		Log.d(TAG, "cleanup()");
		try {
			return Cleanup();
		}
		catch(Exception e) 	{
    		Log.e(TAG, "cleanup(), exception = " + e.getMessage());
    		return -1;
    	}
	}

	public boolean testEnrollmentAndVerification(boolean isEnroll) {
		int isEnrollTmp;
		Log.d(TAG, "testEnrollmentAndVerification(), " + isEnroll);
		if (isEnroll) {
            isEnrollTmp = 1;
            mOperationType = OP_TYPE_ENROLL;
        } else {
            isEnrollTmp = 0;
            mOperationType = OP_TYPE_VERIFY;
        }

		try {
			int ret = TestEnrollmentAndVerification(isEnrollTmp);
			if (ret == VKX_RESULT_SUCCESS) {
				if (isEnroll)
					resetEnrollStatus();
				return true;
			}
			Log.e(TAG, "testEnrollmentAndVerification(), " + ret);
			return false;
		} catch(Exception e) {
			return false;
		}

	}

	protected static void doGetEnrollStatus(int enrollStatus) {
    	Log.d(TAG, "doGetEnrollStatus(), " + enrollStatus);
   		mApHandler.obtainMessage(FpResDef.TINY_STATUS, FpResDef.TINY_STATUS +
				enrollStatus, -1).sendToTarget();
    }
	
	public int[] getTinyMapInfo() {
		int[] info = new int[3];
		info[0] = mMapIdx;
		info[1] = mMapW;
		info[2] = mMapH;
		Log.d(TAG, "getTinyMapInfo(), MapIdx = " + info[0] + ", MapW = " +
				info[1] + ", MapH = "+ info[2]);
		return info;
	}
	
	public byte[] getTinyMap() {
		if (mMap == null) {
			Log.e(TAG, "getTinyMap(), mMap == null");
			return null;
		}
		return mMap.clone();
	}
	
	public byte[] getMatchedImg() {
		return mFPMatchedImg.clone();
	}
	
	public int[] getMatchedImgInfo() {
		int[] info = new int[2];
		info[0] = mMatchedWidthImg;
		info[1] = mMatchedHeightImg;
		Log.d(TAG, "getMatchedImgInfo() MatchedWidthImg = " + info[0] +
				" MatchedHeightImg = " + info[1]);
		return info;
	}
	
	public int getOperationType() {
		Log.d(TAG, "getOperationType(), " + mOperationType);
    	return mOperationType;
    }
	
	public String getMatchedUserID() {
		Log.d(TAG, "getMatchedUserID(), " + mMatchedID);
		return mMatchedID;
	}
	
	public static boolean identify_result(int matchIdx, int maxScore) {
		Log.d(TAG, "identify_result(), matchIdx = " + matchIdx +
				", maxScore = " +  maxScore);

		if (matchIdx != -1) {
			matchIdx = FingerUtil.fileDBMap[matchIdx];
			mMatchedID = mFingerUtil.getMatchedID(matchIdx);
			postFpResultStatus(FpResDef.FP_RES_MATCHED_OK);
		} else {
			postFpResultStatus(FpResDef.FP_RES_MATCHED_FAIL);
		}
		return true;
	}
	
	public static void postFpResultStatus(int status) {
	    mApHandler.obtainMessage(FpResDef.FP_RESULT, status, -1)
				.sendToTarget();
	}
	
	protected static void doGetImage(byte[] image, int width, int height) {
		 Log.d(TAG, "doGetImage(), donothing");
	}
	
	protected static void doGetMatchedImage (byte[] image, int width, int height) {
    	Log.d(TAG, "doGetMatchedImage(), width =" + width + ", height = " +
				height);
    	mFPMatchedImg = image.clone();
    	mMatchedWidthImg = width;
    	mMatchedHeightImg = height;
    }
	
	protected static void doGetStatus(int status) {
		Log.d(TAG, "doGetStatus status = " + status);
    	switch (status) {
			case STATUS_IMAGE_BAD:
			case STATUS_FEATURE_LOW:
			case STATUS_IMAGE_SMALL:
				mBadTrial++;
				Log.d(TAG, "doGetStatus(), mBadTrial = " + mBadTrial);
				postFpResultStatus(FpResDef.FP_RES_GETTED_BAD_IMAGE);
				break;
			case STATUS_IMAGE_PARTIAL:
				postFpResultStatus(FpResDef.FP_RES_PARTIAL_IMG);
				break;
			case STATUS_SWIPE_TOO_WET:
				postFpResultStatus(FpResDef.FP_RES_WET_IMG);
				break;
			case STATUS_IMAGE_WATER:
				postFpResultStatus(FpResDef.FP_RES_WATER_IMG);
				break;
			case STATUS_SWIPE_TOO_FAST:
				postFpResultStatus(FpResDef.FP_RES_FAST_IMG);
				break;
			case VKX_RESULT_ENROLL_FAIL:
				postFpResultStatus(FpResDef.FP_RES_ENROLL_FAIL);
				break;
			case STATUS_WAIT_FPON:
			    postFpResultStatus(FpResDef.FP_RES_FINGER_WAIT_FPON);
                break;
			case STATUS_FINGER_DETECTED:
				postFpResultStatus(FpResDef.FP_RES_FINGER_DETECTED);
				break;
			case STATUS_FINGER_REMOVED:
				postFpResultStatus(FpResDef.FP_RES_FINGER_REMOVED);
				break;
			case STATUS_USER_ABORT:
				postFpResultStatus(FpResDef.FP_RES_ABORT_OK);
				break;
			case STATUS_TEST_ENROLL_OK:
				postFpResultStatus(FpResDef.FP_RES_TEST_ENROLL_OK);
				break;
			case STATUS_TEST_ENROLL_FAIL:
				postFpResultStatus(FpResDef.FP_RES_TEST_ENROLL_FAIL);
				break;
			case STATUS_TEST_VERIFY_OK:
				postFpResultStatus(FpResDef.FP_RES_TEST_MATCHED_OK);
				break;
			case STATUS_TEST_VERIFY_FAIL:
				postFpResultStatus(FpResDef.FP_RES_TEST_MATCHED_FAIL);
				break;
			case FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START:
				postFpResultStatus(FpResDef.FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START);
				break;
			case FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END:
				postFpResultStatus(FpResDef.FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END);
				break;
			case FACTORY_TEST_EVT_SNSR_TEST_PUT:
				postFpResultStatus(FpResDef.FACTORY_TEST_EVT_SNSR_TEST_PUT);
				break;
			case FACTORY_WRITE_EEPROM_SCRIPT_START:
				postFpResultStatus(FpResDef.FACTORY_WRITE_EEPROM_SCRIPT_START);
				break;
			case FACTORY_WRITE_EEPROM_SCRIPT_END:
				postFpResultStatus(FpResDef.FACTORY_WRITE_EEPROM_SCRIPT_END);
				break;
			case EVT_ERROR:
				postFpResultStatus(FpResDef.EVT_ERROR);
				break;
			case EEPROM_STATUS_OPERATION_END:
				postFpResultStatus(FpResDef.EEPROM_STATUS_OPERATION_END);
				break;
			default:
    	}
	}
	
	protected static void doEnrollProgress(int progress) {
    	Log.d(TAG, "doEnrollProgress(), " + progress);
    	mEnrollProgress = progress;    	
    	postFpResultStatus(FpResDef.FP_RES_ENROLL_MAP_PROGRESS);
    }
	
	protected static void doGetFeature(byte[] feature, int size) {
	    Log.d(TAG, "doGetFeature(), size = " + size + ", mEnrollUserID = " +
				mEnrollUserID);
		if (feature == null) {
			Log.e(TAG, "doGetFeature(), feature = null");
		} else {
			Log.d(TAG, "doGetFeature(), feature[0] = " + feature[0]);
		}
	    mFingerUtil.mFeature = feature;
	    mFingerUtil.mFeatureSize = size;
		if (mOperationType == OP_TYPE_ENROLL) {
			if (mFingerUtil.checkId(mEnrollUserID)) {
				mFingerUtil.enrollToDB(mEnrollUserID);
				isSaveDataSet = true;
			}
			postFpResultStatus(FpResDef.FP_RES_ENROLL_OK);
		}
	}

	public boolean doEnrollToDB(String userId) {
		Log.d(TAG, "doEnrollToDB(), " + userId);
		return mFingerUtil.enrollToDB(mEnrollUserID); 
	}

	protected static void doGetVerifyImg (byte[] image, int width,
										  int height) {
    	Log.d(TAG, "doGetVerifyImg(), width = " + width + ", height = " +
				height);
    	mFPVerifyImg = image.clone();
    	mWidthImg = width;
    	mHeightImg = height;  
    	postFpResultStatus(FpResDef.FP_RES_THREAD_IMG);
    }
	
	protected static void doEnrollLearning(byte[] EnrollTemplate, int len)
	{
		Log.d(TAG,"doEnrollLearning(), mMatchedID = "+ mMatchedID +", len = " +
				EnrollTemplate.length);
		
		mFingerUtil.mFeature = EnrollTemplate;
	    mFingerUtil.mFeatureSize = len;
		if (mOperationType == OP_TYPE_VERIFY) {
			mFingerUtil.enrollToDB("*" + mMatchedID);
		}
	}

	protected static void NativeCallback(int eventId, int value1, int value2,
										 byte[] byteBuffer) {
    	Log.d(TAG,"NativeCallback(), event id = "+eventId + ", value1 = " +
				value1 + ", value2 = " + value2);
    	switch (eventId) {
			case EVENT_GET_FEATURE:
				doGetFeature(byteBuffer, value1);
				break;
			case EVENT_ENROLL_PROGRESS:
				mSuccessTrial++;
				mEnrollProgress = value1;
				Log.d(TAG, "NativeCallback(), mSuccessTrial = " +
						mSuccessTrial);
				postFpResultStatus(FpResDef.FP_RES_ENROLL_COUNT);
				break;
    		case EVENT_GET_IMAGE:
      			doGetImage(byteBuffer, value1, value2);
    			break;	    			
    		case EVENT_STATUS:
    			doGetStatus(value1);
    			break;	    			
    		case EVENT_ENROLL_MAP_PROGRESS:
    		case EVENT_ENROLL_CANDIDATE_COUNT:
    			doEnrollProgress(value1);
    			break;
    		case EVENT_GET_MATCHED_IMAGE:
    			doGetMatchedImage(byteBuffer,value1, value2);
    			postFpResultStatus(FpResDef.FP_RES_GETTED_GOOD_IMAGE);
    			break;
    		case EVENT_GET_VERIFY_RESULT:
    			identify_result(value1, value2);
    			break;
     		case EVENT_GET_VERIFY_IMG:
    			doGetVerifyImg(byteBuffer, value1, value2);
    			postFpResultStatus(FpResDef.FP_RES_GETTED_GOOD_IMAGE);
    			break;    			
    		case EVENT_ENROLL_LEARNING:
    			doEnrollLearning(byteBuffer, value1);
    			break;
    		default:
    			Log.e(TAG,"NativeCallback(),  eventId = " + eventId);
    			break;
		}
	}
}
