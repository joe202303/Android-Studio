package egis.client.api;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.util.SparseArray;
import egis.finger.host.FPNativeBase;
import egistec.csa.client.api.Fingerprint;
import egistec.csa.client.api.Fingerprint.*;
import egistec.csa.client.api.IFingerprint;

public class EgisFingerprint implements IFingerprint {

	public static final String TAG = "FpCsaClientLib_EgisFingerprint";

	public static final String MAJOR_VERSION = "1";
	public static final String JAR_VERSION = "30";

	/* Enroll Event */
	public static final int ENROLL_STATUS = 1000;
	public static final int ENROLL_SUCCESS = 1001;
	public static final int ENROLL_FAILED = 1002;
	public static final int ENROLL_FAILURE_CANCELED = 1003;
	public static final int ENROLL_FAULURE_ENROLL_FAILURE_EXCEED_MAX_TRIAL  = 1004;
	public static final int ENROLL_BITMAP = 1005;
	
	/* Identify Event */
	public static final int IDENTIFY_SUCCESS = 1020;
	public static final int IDENTIFY_FAILED = 1021;
	public static final int IDENTIFY_FAILURE_CANCELED = 1022;
	public static final int IDENTIFY_FAILURE_TIMEOUT = 1023;
	public static final int IDENTIFY_FAILURE_NOT_MATCH = 1024;
	public static final int IDENTIFY_FAILURE_BAD_QUALITY = 1025;
	
	/* Sensor Event */
	public static final int CAPTURE_READY = 1040;
	public static final int CAPTURE_STARTED = 1041;
	public static final int CAPTURE_COMPLETED = 1042;
	public static final int CAPTURE_FINISHED = 1043;
	public static final int CAPTURE_SUCCESS = 1044;
	public static final int CAPTURE_FAILED	 = 1045;
	
	/* Accuracy Level */
	public static final int ACCURACY_LOW = 0;
	public static final int ACCURACY_REGULAR = 1;
	public static final int ACCURACY_HIGH = 2;
	public static final int ACCURACY_VERY_HIGH = 3;
	
	/* Sensor Control */
	public static final int PAUSE_ENROLL = 2010;
	public static final int RESUME_ENROLL = 2011;
	public static final int SENSOR_TEST_NORMALSCAN_COMMAND = 100103;
	public static final int SENSOR_TEST_SNR_ORG_COMMAND = 100106;
	public static final int SENSOR_TEST_SNR_FINAL_COMMAND = 100107;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START = 0x11000003;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END = 0x11000004;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_PUT = 0x11000005;
	public static final int EVT_ERROR = 2020;
	public static final int SENSOR_EEPROM_WRITE_COMMAND = 2030;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_START = 2031;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_END = 2032;
	
	/* Sensor Status */
	public static final int SENSOR_OK = 2004;
    public static final int SENSOR_WORKING = 2005;
    public static final int SENSOR_OUT_OF_ORDER = 2006;

	/* Image Quality */
	public static final int QUALITY_GOOD = 0x00000000;
	public static final int QUALITY_FAILED = 0x10000000;
	public static final int QUALITY_OFFSET_TOO_FAR_LEFT = 0x00000001;
	public static final int QUALITY_OFFSET_TOO_FAR_RIGHT = 0x00000002;
	public static final int QUALITY_SOMETHING_ON_THE_SENSOR = 0x00000004;
	public static final int QUALITY_WET_FINGER = 0x00000008;
	public static final int QUALITY_NOT_A_FINGER_SWIPE = 0x00000010;
	public static final int QUALITY_PRESSURE_TOO_LIGHT = 0x00000020;
	public static final int QUALITY_PRESSURE_TOO_HARD = 0x00000040;
	public static final int QUALITY_FINGER_TOO_THIN = 0x00000080;
	public static final int QUALITY_DUPLICATED_SCANNED_IMAGE = 0x00000100;
	public static final int QUALITY_TOO_FAST = 0x00000200;
	public static final int QUALITY_WATER = 0x00001000;
	public static final int QUALITY_TOO_SLOW = 0x00010000;
	public static final int QUALITY_TOO_SHORT = 0x00020000;
	public static final int QUALITY_SKEW_TOO_LARGE = 0x00040000;
	public static final int QUALITY_REVERSE_MOTION = 0x00080000;
	public static final int QUALITY_STICTION = 0x01000000;
	public static final int QUALITY_ONE_HAND_SWIPE = 0x02000000;
	public static final int QUALITY_PARTIAL_TOUCH= 0x80000000;
	public static final int QUALITY_EMPTY_TOUCH = 0x20000000;

	/* Result */
	public static final int RESULT_OK =0;
	public static final int RESULT_FAILED = -1;
	public static final int RESULT_CANCELED = -2;
	//public static final int RESULT_SENSOR_ERROR = -3;
	public static final int RESULT_NO_AUTHORITY = -4;
	public static final int RESULT_TOO_MANY_FINGER=-5;
		

	public static final int MAX_ENROLL_FINGERS = 5;

	private static EgisFingerprint mInstance;
	private static Context mContext;
	private FPNativeBase mFPNativeBase;

    private Map<String, Integer> fingerIndexMap;
    private SparseArray<String> indexMapToFinger;
    private static final String FPID_PREFIX = "EGISFPID";
    private static final String PASSOWORD_PREFIX = "EGISPWD";
    private static final String PASSWORD = "egistec";
        
    private boolean mIsEnrollSessionOpen;
    private boolean mIsFingerOrPWValidate;
    
    private String mEnrollUserId;
    private int mEnrollIndex;

	/* use this flag to filter some unwanted event */
    private boolean mIsFilterOn;
    
	public static final int OP_TYPE_ENROLL = 101;
	public static final int OP_TYPE_VERIFY = 102;
	private static final int MAP_W = 256;
	private static final int MAP_H = 256;
    private static FingerprintEventListener mFingerprintEventListener;
            
    public static EgisFingerprint create(Context context) {
    	Log.d(TAG, "create()");
    	mContext = context;
    	if (mInstance == null) {
    		Log.d(TAG, "create(), new EgisFingerprint()");
    		mInstance = new EgisFingerprint();
    	}
    	return mInstance;
    }

	private EgisFingerprint() {
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
		
		mFPNativeBase = new FPNativeBase(mHandler, mContext);
		getVersion();
	}

    public void setEventListener(FingerprintEventListener
										 fingerprintEventListener) {
    	Log.d(TAG, "setEventListener()");
    	mFingerprintEventListener = fingerprintEventListener;
    }
    
	public String getVersion() {
		String sdkVersion = mFPNativeBase.getVersion();

		String[] sdkVersionArray = sdkVersion.split("\\.");
		if (sdkVersionArray.length != 4) {
			Log.e(TAG, "getVersion(), SDK Version length = " +
					sdkVersionArray.length);
			return "UNKNOW_VERSION";
		}

		String outputVersion = EgisFingerprint.MAJOR_VERSION + "." +
				EgisFingerprint.JAR_VERSION + "." + sdkVersionArray[2] + "." +
				sdkVersionArray[3];

		Log.d(TAG, "getVersion(), SDK Version = " + sdkVersion +
				", Output Version = " +	outputVersion);
		return outputVersion;
	}
	
	public int getSensorStatus() {
		Log.d(TAG, "getSensorStatus()");
        return mFPNativeBase.getSensorStatus();
    }
	
	public int[] getFingerprintIndexList(String userId) {
		Log.d(TAG, "getFingerprintindexList(), userId = " + userId);
		String enrollList = mFPNativeBase.getEnrollList(userId);
		Log.d(TAG, "enrollList="+enrollList);
		if (enrollList == null || enrollList.equals("")) {
			if (enrollList == null) {
				Log.e(TAG, "getFingerprintindexList(),, enrollList == null");
			} else {
				Log.e(TAG, "getFingerprintindexList(), enrollList.equals(\"\") == true");
			}
			return null;
		}
		String[] fingers = fpSplitor(enrollList);
		Arrays.sort(fingers);
		Log.d(TAG, "getFingerprintindexList(), fingers = " +
				Arrays.toString(fingers));
		int[] fingerIndex = new int[fingers.length];
		for (int i = 0; i < fingerIndex.length; i++) {
			fingerIndex[i] = fingerIndexMap.get(fingers[i]);
			Log.d(TAG, "getFingerprintindexList(), fingerIndex[" + i + "] = "
					+ fingerIndex[i]);
		}
		return fingerIndex;
	}
	
	private String[] fpSplitor(String fpData) {
		Log.d(TAG, "fpSplitor(), fpData = " + fpData);
		if (fpData.equals("")) {
			Log.e(TAG, "fpSplitor(), fpData is null");
			return null;
		}
		String[] fingerArray = null;
		if (fpData.contains(";")) {
			fingerArray = fpData.split(";");
		} else {
			fingerArray = new String[] {fpData};
		}
		Log.d(TAG, "fpSplitor(), fingerArray = " +
				Arrays.toString(fingerArray));
		return fingerArray;
	}
	
	public byte[] getFingerprintId(String userId, int index) {
		Log.d(TAG, "getFingerprintId(), userId = " + userId + ", index = " +
				index);
		if ((userId == null) || userId.equals("") ||
				(indexMapToFinger.get(index) == null)) {
			Log.e(TAG, "getFingerprintId(). input parameter error");
			return null;
		}			
		String str = mFPNativeBase.dataRead(FPID_PREFIX+";" + userId + ";" +
				indexMapToFinger.get(index), PASSWORD);
		if (str == null) {
			Log.e(TAG, "getFingerprintId(), data read error");
			return null;
		} else {
			return str.getBytes();
		}
	}
	
	public String[] getUserIdList() {
		Log.d(TAG, "getUserIdList()");
		return mFPNativeBase.getUserIdList();
	}
	
	public int getEnrollRepeatCount() {
		Log.d(TAG, "getEnrollRepeatCount()");
		int[] enrollStatus = mFPNativeBase.getEnrollStatus();
		if ((enrollStatus[0] == 0) && (enrollStatus[1] == 0) &&
				(enrollStatus[2] == 0)) {
			Log.d(TAG, "getEnrollRepeatCount(), enrollStatus is null");
			return -1; 
		}
		Log.d(TAG, "getEnrollRepeatCount(), " + enrollStatus[0] + ", " +
				enrollStatus[1] + ", " + enrollStatus[2]);
		return enrollStatus[0] + enrollStatus[1];
	}
	
	public String getSensorInfo() {
		Log.d(TAG, "getSensorInfo()");
		return mFPNativeBase.getSensorInfo();
	}

	public int setAccuracyLevel(int level) {
		Log.d(TAG, "setAccuracyLevel(), level = " + level);
        return mFPNativeBase.setAccuracyLevel(level);
    }
	
	public int setEnrollSession(boolean flag) {
		Log.d(TAG, "setEnrollSession(), flag = " + flag);
		mIsEnrollSessionOpen = flag;
		mIsFingerOrPWValidate = false;
		return RESULT_OK;
	}
	
	public int setPassword(String userId, byte[] pwdHash) throws
			UnsupportedEncodingException {
		Log.d(TAG, "setPassword(), userId = " + userId);
		
		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "setPassword(), mIsEnrollSessionOpen");
			return RESULT_NO_AUTHORITY;
		}
		if (userId.isEmpty()) {
			Log.e(TAG, "setPassword(), userId");
			return RESULT_NO_AUTHORITY; 
		}
				
		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;	
		}
		
		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "setPassword(), no validate finger");
			return RESULT_NO_AUTHORITY;
		}
		boolean ret = false;
		ret =  mFPNativeBase.dataSet(PASSOWORD_PREFIX + ";" + userId,
				new String(pwdHash, "UTF-8"), PASSWORD);
		if (ret) {
			if (mFPNativeBase.doEnrollToDB(userId)) {
				Log.d(TAG, "setPassword(), save dataSet = " + FPID_PREFIX +
						";" + userId + ";" +
						indexMapToFinger.get(mEnrollIndex));
				mFPNativeBase.dataSet(FPID_PREFIX + ";" + userId + ";" +
						indexMapToFinger.get(mEnrollIndex),
						UUID.randomUUID().toString(), PASSWORD);
			}
			return RESULT_OK;
		} else {
			return RESULT_FAILED;
		}
	}
	
	public int verifyPassword(String userId, byte[] pwdHash) throws
			UnsupportedEncodingException {
		Log.d(TAG, "verifyPassword(), userId = " + userId);
		
		String dbPwd = mFPNativeBase.dataRead(PASSOWORD_PREFIX + ";" + userId,
				PASSWORD);
		if (dbPwd == null) {
			Log.e(TAG, "verifyPassword(), dbPwd");
			return RESULT_FAILED; 
		}			
		String keyInPw = new String(pwdHash, "UTF-8");
		Log.d(TAG, "verifyPassword(), keyInPw = " + keyInPw + " dbPwd = " +
				dbPwd);
		if (keyInPw.equals(dbPwd)) {
			mIsFingerOrPWValidate = true;
			return RESULT_OK;
		} else {
			return RESULT_FAILED;
		}
	}
	
	/* Operation */
	public int identify(String userId) {
		Log.d(TAG, "identify(), userId = " + userId);
		
		if ((userId == null) || userId.equals("")) {
			Log.d(TAG, "identify(), Invalid userId");
			return RESULT_FAILED;
		}

		if (userId == "Test") {
			/*
			 * Id userId is Test, it calls testEnrollmentAndVerification() to
			 * tests.
			 */
			boolean ret = mFPNativeBase.testEnrollmentAndVerification(false);
			if (ret) {
				return RESULT_OK;
			} else {
				return RESULT_FAILED;
			}
		}

		mIsFilterOn = false;
		boolean ret = false;
		ret = mFPNativeBase.identify(userId);
		if (ret) {
			return RESULT_OK;
		} else {
			return RESULT_FAILED;
		}
	}
	
	public int enroll(String userId, int index) {
		Log.d(TAG, "enroll(), userId = " + userId + ", index = " + index);

		if ((userId == null) || userId.equals("") ||
				(indexMapToFinger.get(index) == null)) {
			Log.d(TAG, "enroll(), Invalid userId or index");
			return RESULT_FAILED;
		}

		if (userId == "Test") {
			/*
			 * Id userId is Test, it calls testEnrollmentAndVerification() to
			 * tests.
			 */
			boolean ret = mFPNativeBase.testEnrollmentAndVerification(true);
			if (ret) {
				return RESULT_OK;
			} else {
				return RESULT_FAILED;
			}
		}

		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "enroll(), no open enrollSession");
			return RESULT_NO_AUTHORITY;
		}
		
		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;	
		}
		
		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "enroll(), no validate finger or password");
			return RESULT_NO_AUTHORITY;
		}
		
		int[] fingerList = getFingerprintIndexList(userId);
		boolean hasID = false;
		if (fingerList != null && fingerList.length >= MAX_ENROLL_FINGERS) {
			for(int i = 0; i < fingerList.length; i++) {
				if (fingerList[i] == index) {
					hasID = true;
				}
			}
		} else {
			hasID = true;
		}
		
		if (!hasID) {
			Log.d(TAG, "enroll(), Too many");
			return RESULT_TOO_MANY_FINGER;
		}
		
		mIsFilterOn = false;
		String egisId = userId + ";" + indexMapToFinger.get(index);
		Log.d(TAG, "enroll(), egisId = " + egisId);

		boolean ret = mFPNativeBase.enroll(egisId);
		if (ret) {
			mEnrollUserId = userId;
			mEnrollIndex = index;
			Log.d(TAG, "enroll(), mEnrollUserId = " + mEnrollUserId +
					", mEnrollIndex = " + mEnrollIndex);
			return RESULT_OK;
		} else {
			return RESULT_FAILED;
		}
	}

    public int swipeEnroll(String userId, int index) {
        Log.d(TAG, "swipeEnroll(), userId = " + userId + ", index = " + index);

        if ((userId == null) || userId.equals("") ||
                (indexMapToFinger.get(index) == null)) {
            Log.d(TAG, "swipeEnroll(), Invalid userId or index");
            return RESULT_FAILED;
        }

        if (userId == "Test") {
			/*
			 * Id userId is Test, it calls testEnrollmentAndVerification() to
			 * tests.
			 */
            boolean ret = mFPNativeBase.testEnrollmentAndVerification(true);
            if (ret) {
                return RESULT_OK;
            } else {
                return RESULT_FAILED;
            }
        }

        if (!mIsEnrollSessionOpen) {
            Log.e(TAG, "swipeEnroll(), no open enrollSession");
            return RESULT_NO_AUTHORITY;
        }

        if (!hasFinger(userId) && !hasPasswd(userId)) {
            mIsFingerOrPWValidate = true;
        }

        if (!mIsFingerOrPWValidate) {
            Log.e(TAG, "swipeEnroll(), no validate finger or password");
            return RESULT_NO_AUTHORITY;
        }

        int[] fingerList = getFingerprintIndexList(userId);
        boolean hasID = false;
        if (fingerList != null && fingerList.length >= MAX_ENROLL_FINGERS) {
            for(int i = 0; i < fingerList.length; i++) {
                if (fingerList[i] == index) {
                    hasID = true;
                }
            }
        } else {
            hasID = true;
        }

        if (!hasID) {
            Log.d(TAG, "swipeEnroll(), Too many");
            return RESULT_TOO_MANY_FINGER;
        }

        mIsFilterOn = false;
        String egisId = userId + ";" + indexMapToFinger.get(index);
        Log.d(TAG, "swipeEnroll(), egisId = " + egisId);

        boolean ret = mFPNativeBase.swipeEnroll(egisId);
        if (ret) {
            mEnrollUserId = userId;
            mEnrollIndex = index;
            Log.d(TAG, "swipeEnroll(), mEnrollUserId = " + mEnrollUserId +
                    ", mEnrollIndex = " + mEnrollIndex);
            return RESULT_OK;
        } else {
            return RESULT_FAILED;
        }
    }
	
	private boolean hasFinger(String userId) {
		Log.d(TAG, "hasFinger(), userId = " + userId);
		String enrollList = mFPNativeBase.getEnrollList(userId);
		Log.d(TAG, "hasFinger(), enrollList = " + enrollList);
		return (enrollList != null && !enrollList.equals(""));
	}
	
	private boolean hasPasswd(String userId) {
		Log.d(TAG, "hasPasswd(), userId = " + userId);
		if (mFPNativeBase.dataRead(PASSOWORD_PREFIX + ";" + userId, PASSWORD)
				== null) {
			Log.e(TAG, "hasPasswd(), dataRead false");
			return false;
		}
		return true;
	}		
	
	public int remove(String userId, int index) {
		Log.d(TAG, "remove(), userId = " + userId + "index = " + index);
		
		if ((userId == null) || userId.equals("") ||
				(indexMapToFinger.get(index) == null)) {
			Log.e(TAG, "remove(), Invalid userId");
			return RESULT_FAILED;
		}
		
		if (!mIsEnrollSessionOpen) {
			Log.e(TAG, "remove(), no open enrollSession");
			return RESULT_NO_AUTHORITY;
		}
		
		if (!hasFinger(userId) && !hasPasswd(userId)) {
			mIsFingerOrPWValidate = true;	
		}		
		
		if (!mIsFingerOrPWValidate) {
			Log.e(TAG, "remove(), no validate finger or password");
			return RESULT_NO_AUTHORITY;
		}
		
		if (!hasFinger(userId)) {
			Log.e(TAG, "remove(), hasFinger()");
			return RESULT_FAILED;
		}							

		String egisEnrollId = userId + ";" + indexMapToFinger.get(index);
		if (!mFPNativeBase.deleteFeature(egisEnrollId)) {
			Log.e(TAG, "remove(), deleteFeature()");
			return RESULT_FAILED;
		}

		mFPNativeBase.dataDelete(FPID_PREFIX+";"+userId+";"+indexMapToFinger.get(index), PASSWORD);
		String enrollList = mFPNativeBase.getEnrollList(userId);
		Log.d(TAG, "remove(), enrollList = " + enrollList);
		if (enrollList == null || enrollList.equals("")) {
			Log.d(TAG, "remove(), no finger, so delete password");
			mFPNativeBase.dataDelete(PASSOWORD_PREFIX+";"+userId, PASSWORD);
		}
		return RESULT_OK;
	}
	
	public int request(int status, Object obj) {
		Log.d(TAG, "request(), status = " + status);
		return mFPNativeBase.sensorControl(status, 0);
	}
	
	/*
	public int notify(int status, Object obj) {
		Log.d(TAG, "notify(), status = " + status);
		return mFPNativeBase.sensorControl(status, 0);
	}
	*/
	
	public int verifySensorState(int cmd, int sId, int opt, int logOpt,
								 int uId) {
		Log.d(TAG, "verifySensorState(), cmd = " + cmd + ", opt = " + opt);
		return mFPNativeBase.sensorControl(cmd, opt);
	}
	
	public int cancel() {
		Log.d(TAG, "cancel()");
		
		boolean ret = false;
		ret = mFPNativeBase.abort();
		if (ret) {
			return RESULT_OK;
		} else {
			return RESULT_FAILED;
		}
	}
	
	public int cleanup() {
		Log.d(TAG, "cleanup()");
		mContext = null;
		mInstance = null;
		return mFPNativeBase.cleanup();
	}
	
	public int enableSensorDevice(boolean enable) {
		Log.d(TAG, "enableSensorDevice(), enable = " + enable);
		int ret = mFPNativeBase.sensorDeviceEnable(enable);
		Log.d(TAG, "enableSensorDevice(), ret = " + ret);
		return ret;
	}
	
	private void notifyOnEnrollStatus() {
		if (mFingerprintEventListener != null) {
			int[] enrollInfo = null;
			enrollInfo = mFPNativeBase.getEnrollStatus();

			EnrollStatus enrollStatus = new EnrollStatus();
			enrollStatus.successTrial = enrollInfo[0];
			enrollStatus.badTrial     = enrollInfo[1];
			enrollStatus.totalTrial   = enrollInfo[0] + enrollInfo[1];
			enrollStatus.progress	  = enrollInfo[2];       		 					
			mFingerprintEventListener.onFingerprintEvent(ENROLL_STATUS,
					enrollStatus);
		} else {
			Log.e(TAG, "notifyOnEnrollStatus(), mFingerprintEventListener");
		}
	}
	
	private void notifyOnEnrollMap() {
		if (mFingerprintEventListener != null) {
			EnrollBitmap enrollMap = new EnrollBitmap();				
			int[] mapInfo = mFPNativeBase.getTinyMapInfo();
			Log.d(TAG, "notifyOnEnrollMap(), mapInfo[1] = " + mapInfo[1] +
					", mapInfo[2] = " + mapInfo[2]);
			if (mapInfo[1] != MAP_W && mapInfo[2] != MAP_H) {
				return;
			}
				
			byte[] map = mFPNativeBase.getTinyMap();
			for (int index = 0; index < map.length; index ++) {
				 map[index] = (byte) ((map[index]==-1)?0:255);
			}
			Bitmap bitmap = Bitmap.createBitmap(mapInfo[1], mapInfo[2],
					Bitmap.Config.ALPHA_8);
			bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(map));				
			enrollMap.enrollMap = bitmap;
			mFingerprintEventListener.onFingerprintEvent(ENROLL_BITMAP,
					enrollMap);
		} else {
			Log.d(TAG, "notifyOnEnrollMap mFingerprintEventListener");
		}
	}
	
	private void notifyOnBadImage(int quality) {
		Log.d(TAG, "notifyOnBadImage(), quality = " + quality);
	 	if (mFingerprintEventListener != null) {
	 		byte[] rawData = null;
      		int[] rawDataInfo = null;
      		rawData = mFPNativeBase.getMatchedImg();
      		rawDataInfo =  mFPNativeBase.getMatchedImgInfo();				
      		FingerprintBitmap fpBitmap = new FingerprintBitmap();
      		fpBitmap.width = rawDataInfo[0];
      		fpBitmap.height = rawDataInfo[1];
      		fpBitmap.quality = quality;
      		fpBitmap.bitmap = Bitmap.createBitmap(rawDataInfo[0],
					rawDataInfo[1], Bitmap.Config.ALPHA_8);
      		for (int index = 0; index < rawData.length; index ++) {
      		 rawData[index] = (byte) ~rawData[index];
      		}
			fpBitmap.bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(rawData));
			Log.d(TAG, "rawData len="+rawData.length);
			mFingerprintEventListener.onFingerprintEvent(CAPTURE_FAILED,
					fpBitmap);
	 	} else{
			Log.d(TAG, "notifyOnBadImage(), mFingerprintEventListener");
		}
	}

	private void handleFpResult(int result) {
		switch (result) {
			case FpResDef.FP_RES_ENROLL_COUNT:
				Log.d(TAG, "handleFpResult(), FP_RES_ENROLL_COUNT");
				notifyOnEnrollStatus();
				break;
			case FpResDef.FP_RES_ENROLL_OK:
				Log.d(TAG, "handleFpResult(), FP_RES_ENROLL_OK");
				/* Generate a fid for this fingerprint */
				if (mFPNativeBase.isSaveDataSet) {
					mFPNativeBase.dataSet(FPID_PREFIX + ";" + mEnrollUserId +
									";" + indexMapToFinger.get(mEnrollIndex),
									UUID.randomUUID().toString(), PASSWORD);
					mFPNativeBase.isSaveDataSet = false;
				}
				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(
							ENROLL_SUCCESS, null);
					Log.i(TAG, "handleFpResult(), ENROLL_SUCCESS");
				}
				mEnrollUserId = null;
				break;
			case FpResDef.FP_RES_ENROLL_FAIL:
				Log.d(TAG, "handleFpResult(), FP_RES_ENROLL_FAIL");
				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(ENROLL_FAILED,
							null);
					Log.i(TAG, "handleFpResult(), ENROLL_FAILED");
				}
				mEnrollUserId = null;
				mEnrollIndex = 0;
				break;
			case FpResDef.FP_RES_MATCHED_OK:
				Log.d(TAG, "handleFpResult(), FP_RES_MATCHED_OK");

				mIsFingerOrPWValidate = true;
				if (mFingerprintEventListener != null) {
					IdentifyResult identifyResult = new IdentifyResult();
					String egisId;
					egisId = mFPNativeBase.getMatchedUserID();
					String[] idWithIndex = egisId.split(";");
					if (idWithIndex.length < 2) {
						Log.e(TAG, "handleFpResult(), ERROR " + egisId);
					}
					Log.d(TAG, "handleFpResult(), user=" + idWithIndex[0] +
							" index=" + idWithIndex[1]);
					identifyResult.index = fingerIndexMap.get(idWithIndex[1]);
					identifyResult.result = IDENTIFY_SUCCESS;
					mFingerprintEventListener.onFingerprintEvent(
							IDENTIFY_SUCCESS, identifyResult);
					Log.i(TAG, "handleFpResult(), IDENTIFY_SUCCESS");
				}
				break;
			case FpResDef.FP_RES_MATCHED_FAIL:
				Log.d(TAG, "handleFpResult(), FP_RES_MATCHED_FAIL");

				if (mFingerprintEventListener != null) {
					IdentifyResult identifyResult = new IdentifyResult();
					identifyResult.index = -1;
					identifyResult.result = IDENTIFY_FAILURE_NOT_MATCH;
					mFingerprintEventListener.onFingerprintEvent(
							IDENTIFY_FAILED, identifyResult);
					Log.i(TAG, "handleFpResult(), IDENTIFY_FAILED");
				}
				break;
			case FpResDef.FP_RES_GETTING_IMAGE:
				break;
			case FpResDef.FP_RES_FINGER_WAIT_FPON:
				Log.d(TAG, "handleFpResult(), FP_RES_FINGER_WAIT_FPON");
				if (mFingerprintEventListener != null) {
					if (mIsFilterOn)
						break;
					mFingerprintEventListener.onFingerprintEvent(CAPTURE_READY,
							null);
					Log.i(TAG, "handleFpResult(), CAPTURE_READY");
				}
				break;
			case FpResDef.FP_RES_FINGER_DETECTED:
				Log.d(TAG, "handleFpResult(), FP_RES_FINGER_DETECTED");

				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(
							CAPTURE_STARTED, null);
					Log.i(TAG, "handleFpResult(), CAPTURE_STARTED");
				}
				break;
			case FpResDef.FP_RES_FINGER_REMOVED:
				Log.d(TAG, "handleFpResult(), FP_RES_FINGER_REMOVED");

				if (mFingerprintEventListener != null) {
					if (mIsFilterOn || Fingerprint.m_abort)
						break;
					mFingerprintEventListener.onFingerprintEvent(
							CAPTURE_FINISHED, null);
					Log.i(TAG, "handleFpResult(), CAPTURE_FINISHED");
				}
				break;
			case FpResDef.FP_RES_GETTED_GOOD_IMAGE:
				Log.d(TAG, "handleFpResult(), FP_RES_GETTED_GOOD_IMAGE");

				if (mFingerprintEventListener != null) {
					byte[] rawData = mFPNativeBase.getMatchedImg();
					Log.d(TAG, "handleFpResult(), rawData len = " +
							rawData.length);
					int[] rawDataInfo = mFPNativeBase.getMatchedImgInfo();
					FingerprintBitmap fpBitmap = new FingerprintBitmap();
					fpBitmap.width = rawDataInfo[0];
					fpBitmap.height = rawDataInfo[1];
					fpBitmap.quality = QUALITY_GOOD;
					fpBitmap.bitmap = Bitmap.createBitmap(rawDataInfo[0],
							rawDataInfo[1], Bitmap.Config.ALPHA_8);
					for (int index = 0; index < rawData.length; index++) {
						rawData[index] = (byte) ~rawData[index];
					}
					fpBitmap.bitmap.copyPixelsFromBuffer(ByteBuffer.wrap(
							rawData));
					mFingerprintEventListener.onFingerprintEvent(
							CAPTURE_SUCCESS, fpBitmap);
				}
				break;
			case FpResDef.FP_RES_GETTED_BAD_IMAGE:
				Log.d(TAG, "handleFpResult(), FP_RES_GETTED_BAD_IMAGE");
				notifyOnBadImage(QUALITY_FAILED);
				break;
			case FpResDef.FP_RES_PARTIAL_IMG:
				Log.d(TAG, "handleFpResult(), FP_RES_PARTIAL_IMG");
				notifyOnBadImage(QUALITY_PARTIAL_TOUCH);
				break;
			case FpResDef.FP_RES_WET_IMG:
				Log.d(TAG, "handleFpResult(), FP_RES_WET_IMG");
				notifyOnBadImage(QUALITY_WET_FINGER);
				break;
			case FpResDef.FP_RES_WATER_IMG:
				Log.d(TAG, "handleFpResult(), FP_RES_WATER_IMG");
				notifyOnBadImage(QUALITY_WATER);
				break;
			case FpResDef.FP_RES_FAST_IMG:
				Log.d(TAG, "handleFpResult(), FP_RES_FAST_IMG");
				notifyOnBadImage(QUALITY_TOO_FAST);
				break;
			case FpResDef.FP_RES_ABORT_OK:
				Log.d(TAG, "handleFpResult(), FP_RES_ABORT_OK");
				if (mIsFilterOn) {
					break;
				}
				switch (mFPNativeBase.getOperationType()) {
					case OP_TYPE_ENROLL:
						mFingerprintEventListener.onFingerprintEvent(
								ENROLL_FAILURE_CANCELED, null);
						break;
					case OP_TYPE_VERIFY:
						IdentifyResult identifyResult = new IdentifyResult();
						identifyResult.result = IDENTIFY_FAILURE_CANCELED;
						mFingerprintEventListener.onFingerprintEvent(
								IDENTIFY_FAILED, identifyResult);
						Log.i(TAG, "handleFpResult(), IDENTIFY_FAILED");
						break;
				}
				break;
			case FpResDef.FP_RES_TEST_ENROLL_OK:
				Log.d(TAG, "handleFpResult(), FP_RES_TEST_ENROLL_OK");

				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(
							ENROLL_SUCCESS, null);
					Log.i(TAG, "handleFpResult(), ENROLL_SUCCESS");
				}

				mEnrollUserId = null;
				break;
			case FpResDef.FP_RES_TEST_ENROLL_FAIL:
				Log.d(TAG, "handleFpResult(), FP_RES_TEST_ENROLL_FAIL");

				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(
							ENROLL_FAILED, null);
					Log.i(TAG, "handleFpResult(), ENROLL_FAILED");
				}

				mEnrollUserId = null;
				mEnrollIndex = 0;
				break;
			case FpResDef.FP_RES_TEST_MATCHED_OK:
				Log.d(TAG, "handleFpResult(), FP_RES_TEST_MATCHED_OK");
				mIsFingerOrPWValidate = true;

				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(
							IDENTIFY_SUCCESS, null);
					Log.i(TAG, "handleFpResult(), IDENTIFY_SUCCESS");
				}
				break;
			case FpResDef.FP_RES_TEST_MATCHED_FAIL:
				Log.d(TAG, "handleFpResult(), FP_RES_TEST_MATCHED_FAIL");

				if (mFingerprintEventListener != null) {
					mFingerprintEventListener.onFingerprintEvent(
							IDENTIFY_FAILED, null);
					Log.i(TAG, "handleFpResult(), IDENTIFY_FAILED");
				}
				break;
			case FpResDef.FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START:
				Log.d(TAG, "handleFpResult(), FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START");
				mFingerprintEventListener.onFingerprintEvent(
						FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START, null);
				break;
			case FpResDef.FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END:
				Log.d(TAG, "handleFpResult(), FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END");
				mFingerprintEventListener.onFingerprintEvent(
						FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END, null);
				break;
			case FpResDef.FACTORY_TEST_EVT_SNSR_TEST_PUT:
				Log.d(TAG, "handleFpResult(), FACTORY_TEST_EVT_SNSR_TEST_PUT");
				mFingerprintEventListener.onFingerprintEvent(
						FACTORY_TEST_EVT_SNSR_TEST_PUT, null);
				break;
			case FpResDef.EVT_ERROR:
				Log.d(TAG, "handleFpResult(), EVT_ERROR");
				mFingerprintEventListener.onFingerprintEvent(EVT_ERROR, null);
				break;
			case FpResDef.FACTORY_WRITE_EEPROM_SCRIPT_START:
				Log.d(TAG, "handleFpResult(), FACTORY_WRITE_EEPROM_SCRIPT_START");
				mFingerprintEventListener.onFingerprintEvent(
						FACTORY_WRITE_EEPROM_SCRIPT_START, null);
				break;
			case FpResDef.FACTORY_WRITE_EEPROM_SCRIPT_END:
				Log.d(TAG, "handleFpResult(), FACTORY_WRITE_EEPROM_SCRIPT_END");
				mFingerprintEventListener.onFingerprintEvent(
						FACTORY_WRITE_EEPROM_SCRIPT_END, null);
				break;
			case FpResDef.EEPROM_STATUS_OPERATION_END:
				Log.d(TAG, "handleFpResult(), EEPROM_STATUS_OPERATION_END");
				mFingerprintEventListener.onFingerprintEvent(FpResDef.EEPROM_STATUS_OPERATION_END, null);
				break;
			default:
		}
	}

	private void handleTinyResult(int result) {
		switch(result) {
			case FpResDef.TINY_STATUS_ENROLL_MAP:
				Log.d(TAG, "handleTinyResult(), TINY_STATUS_ENROLL_MAP");
				notifyOnEnrollMap();
				break;
			case FpResDef.TINY_STATUS_HIGHLY_SIMILAR:
				Log.d(TAG, "handleTinyResult(), TINY_STATUS_HIGHLY_SIMILAR");
				break;
			default:
		}
	}

	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {        
        	Log.d(TAG, "mHandler(), meg.what = " + msg.what + ", msg.arg1 = "
					+ msg.arg1);
        	switch (msg.what) {
				case FpResDef.FP_RESULT:
					handleFpResult(msg.arg1);
					break;
        		case FpResDef.TINY_STATUS:
					handleTinyResult(msg.arg1);
					break;
				default:
        	}
        }	
	};

	@Override
	public int eeprom_test(int cmd, int address, int value) {
		Log.d(TAG, "eeprom_test(), cmd = " + cmd + ", address = " + address +
				", value = " + value);
		return mFPNativeBase.eepromTest(cmd, address, value);
	}
}


