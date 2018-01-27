package egistec.csa.client.api;

import java.io.File;
import java.io.UnsupportedEncodingException;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import vigis.client.api.VigisFingerprint;
import egis.client.api.EgisFingerprint;

public class Fingerprint {
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
	public static final int ACCURACY_LOW = 0;
	public static final int ACCURACY_REGULAR = 1;
	public static final int ACCURACY_HIGH = 2;
	public static final int ACCURACY_VERY_HIGH = 3;

	/*
	 * Sensor Control
	 */
	public static final int PAUSE_ENROLL = 2010;
	public static final int RESUME_ENROLL = 2011;
	public static final int SENSOR_TEST_NORMALSCAN_COMMAND					= 100103;
	public static final int SENSOR_TEST_SNR_ORG_COMMAND						= 100106;
	public static final int SENSOR_TEST_SNR_FINAL_COMMAND					= 100107;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START			= 0x11000003;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END			= 0x11000004;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_PUT					= 0x11000005;
	public static final int EVT_ERROR = 2020;
	public static final int SENSOR_EEPROM_WRITE_COMMAND = 2030;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_START				= 2031;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_END					= 2032;
	
	//EEPROM status
	public static final int EEPROM_STATUS_OPERATION_END						= 101;
	
	/*
	 * Sensor Status
	 */
	public static final int SENSOR_OK = 2004;
	public static final int SENSOR_WORKING = 2005;
	public static final int SENSOR_OUT_OF_ORDER = 2006;

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
	public static final int QUALITY_WATER = 0x00001000;
	public static final int QUALITY_TOO_SLOW = 0x00010000; // not support
	public static final int QUALITY_TOO_SHORT = 0x00020000; // not support
	public static final int QUALITY_SKEW_TOO_LARGE = 0x00040000; // not support
	public static final int QUALITY_REVERSE_MOTION = 0x00080000; // not support
	public static final int QUALITY_STICTION = 0x001000000; // not support
	public static final int QUALITY_ONE_HAND_SWIPE = 0x002000000; // not support
	public static final int QUALITY_PARTIAL_TOUCH = 0x80000000;
	public static final int QUALITY_EMPTY_TOUCH = 0x20000000; // not support

	/*
	 * Result
	 */
	public static final int RESULT_OK = 0;
	public static final int RESULT_FAILED = -1;
	public static final int RESULT_CANCELED = -2;
	// public static final int RESULT_SENSOR_ERROR = -3; //scope3
	public static final int RESULT_NO_AUTHORITY = -4;
	public static final int RESULT_TOO_MANY_FINGER = -5;

	public static final String TAG = "FpCsaClientLib_Fingerprint";
	private static Fingerprint mInstance;
	private static Context mContext;
	private static IFingerprint mIFingerprint;
	private static final int EGIS_SENSOR = 1;
	private static final int VIGIS_SENSOR = 2;
	private static final String EGIS_SENSOR_PATH = "/dev/esfp0";
	private static final String VIGIS_SENSOR_PATH = "/dev/vfsspi";
	public static boolean m_abort= false;

	public interface FingerprintEventListener {
		void onFingerprintEvent(int eventId, Object eventData);
	}

	public static class FingerprintBitmap {
		public int width;
		public int height;
		public int quality;
		public Bitmap bitmap;
	}

	public static class EnrollStatus {
		public int progress;
		public int totalTrial;
		public int successTrial;
		public int badTrial;
		public Bitmap enrollMap;
	}

	public static class EnrollBitmap {
		public Bitmap enrollMap;
	}

	public static class IdentifyResult {
		public int result;
		public int index;
	}

	public static class SensorTest {
		int scriptId;
		int options;
		int dataLogOpt;
		int unitId;
	}

	public static Fingerprint create(Context context) {
		mContext = context;
		if (mInstance == null) {
			Log.d(TAG, "new Fingerprint()");
			mInstance = new Fingerprint();
		}
		
		if (mIFingerprint == null) {
			Log.d(TAG, " mIFingerprint is null");
			return null;
		}
		
		return mInstance;
	}

	private Fingerprint() {
		if (checkSensorType() == EGIS_SENSOR) {
			mIFingerprint = (IFingerprint) EgisFingerprint.create(mContext);
		} else if (checkSensorType() == VIGIS_SENSOR) {
			mIFingerprint = (IFingerprint) VigisFingerprint.create(mContext);
		}
	}

	private int checkSensorType() {
		File f = new File(EGIS_SENSOR_PATH);
		if (f.exists() && !f.isDirectory()) {
			Log.d(TAG, EGIS_SENSOR_PATH);
			return EGIS_SENSOR;
		}
		f = new File(VIGIS_SENSOR_PATH);
		if (f.exists() && !f.isDirectory()) {
			Log.d(TAG, VIGIS_SENSOR_PATH);
			return VIGIS_SENSOR;
		}
		return 0;
	}

	public void setEventListener(FingerprintEventListener l) {
		Log.d(TAG, "setEventListener()");
		mIFingerprint.setEventListener(l);
	}

	public String getVersion() {
		Log.d(TAG, "getVersion()");
		return mIFingerprint.getVersion();
	}

	public int getSensorStatus() {
		Log.d(TAG, "getSensorStatus()");
		return mIFingerprint.getSensorStatus();
	}

	public int[] getFingerprintIndexList(String userId) {
		Log.d(TAG, "getFingerprintIndexList() userId = " + userId);
		return mIFingerprint.getFingerprintIndexList(userId);
	}

	public byte[] getFingerprintId(String userId, int index) {
		Log.d(TAG, "getFingerprintId() userId = " + userId + ", index = " + index);
		return mIFingerprint.getFingerprintId(userId, index);
	}

	public String[] getUserIdList() {
		Log.d(TAG, "getUserIdList()");
		return mIFingerprint.getUserIdList();
	}

	public int getEnrollRepeatCount() {
		Log.d(TAG, "getEnrollRepeatCount()");
		return mIFingerprint.getEnrollRepeatCount();
	}

	public String getSensorInfo() {
		Log.d(TAG, "getSensorInfo()");
		return mIFingerprint.getSensorInfo();
	}

	public int setAccuracyLevel(int level) {
		Log.d(TAG, "setAccuracyLevel() level = " + level);
		return mIFingerprint.setAccuracyLevel(level);
	}

	public int setEnrollSession(boolean flag) {
		Log.d(TAG, "setEnrollSession() flag = " + flag);
		return mIFingerprint.setEnrollSession(flag);
	}

	public int setPassword(String userId, byte[] pwdHash) throws UnsupportedEncodingException {
		Log.d(TAG, "setPassword() userId = " + userId);
		return mIFingerprint.setPassword(userId, pwdHash);
	}

	public int verifyPassword(String userId, byte[] pwdHash) throws UnsupportedEncodingException {
		int ret = mIFingerprint.verifyPassword(userId, pwdHash);
		Log.d(TAG, "verifyPassword() userId = " + userId + ", ret = " + ret);
		return ret;
	}

	/*
	 * Operation
	 */
	public int identify(String userId) {
		Log.d(TAG, "identify() userId = " + userId);
		m_abort = false;
		return mIFingerprint.identify(userId);
	}

	public int enroll(String userId, int index) {
		Log.d(TAG, "enroll() userId = " + userId + ", index = " + index);
		m_abort = false;
		return mIFingerprint.enroll(userId, index);
	}

	public int swipeEnroll(String userId, int index) {
		Log.d(TAG, "swipeEnroll() userId = " + userId + ", index = " + index);
		m_abort = false;
		return mIFingerprint.swipeEnroll(userId, index);
	}

	public int remove(String userId, int index) {
		Log.d(TAG, "remove() userId = " + userId + ", index = " + index);
		return mIFingerprint.remove(userId, index);
	}

	public int request(int status, Object obj) {
		Log.d(TAG, "request() status = " + status);
		return mIFingerprint.request(status, obj);
	}

	public int verifySensorState(int cmd, int sId, int opt, int logOpt, int uId) {
		Log.d(TAG, "verifySensorState() cmd = " + cmd + ", sId = " + sId + ", opt = " + opt);
		return mIFingerprint.verifySensorState(cmd, sId, opt, logOpt, uId);
	}
	
	public int enableSensorDevice(boolean enable)
	{
		Log.d(TAG, "enableSensorDevice() enable = " + enable);
		return mIFingerprint.enableSensorDevice(enable);
	}
	
	public int eeprom_test(int cmd, int addr, int value) {
		return mIFingerprint.eeprom_test(cmd, addr, value);
	}

	/*
	 * Cleanup
	 */
	public int cancel() {
		Log.d(TAG, "cancel()");
		m_abort = true;
		return mIFingerprint.cancel();
	}

	public int cleanup() {
		Log.d(TAG, "cleanup()");
		return mIFingerprint.cleanup();
	}

}
