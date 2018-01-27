package egistec.fingerauth.api;

import egistec.fingerauth.api.FpResDef;

public class FPAuthListeners {
	public interface StatusListener {
		public static final int GETTED_GOOD_IMAGE = FpResDef.FP_RES_GETTED_GOOD_IMAGE;
		public static final int EXTRACTING_FEATURE = FpResDef.FP_RES_EXTRACTING_FEATURE;
		public static final int START_OPERATION = FpResDef.FP_STATE_START_OPERATION;
		public static final int END_OPERATION = FpResDef.FP_STATE_END_OPERATION;
		public static final int GETTED_BAD_IMAGE = FpResDef.FP_RES_GETTED_BAD_IMAGE;
		public static final int GETTED_IMAGE_FAIL = FpResDef.FP_RES_GETTED_IMAGE_FAIL;
		public static final int GETTED_IMAGE_TOO_SHORT = FpResDef.FP_RES_GETTED_IMAGE_TOO_SHORT;
		public static final int FP_RES_FINGER_DETECTED = FpResDef.FP_RES_FINGER_DETECTED;
		public static final int FP_RES_FINGER_REMOVED = FpResDef.FP_RES_FINGER_REMOVED;
		public static final int FP_RES_FINGER_WAIT_FPON = FpResDef.FP_RES_FINGER_WAIT_FPON;
		public static final int FP_RES_SOMETHING_ON_SENSOR = FpResDef.FP_RES_SOMETHING_ON_SENSOR;
//		public static final int IMAGE_TOO_HEAVY = FP_HANDLE_RESULT.FP_RES_IMAGE_TOO_HEAVY;
//		public static final int IMAGE_TOO_LIGHT = FP_HANDLE_RESULT.FP_RES_IMAGE_TOO_LIGHT;
		public static final int	STATUS_IMAGE_BAD			=	5;//Bad image
		public static final int	STATUS_FEATURE_LOW			=	6;//Bad image
//		public static final int	STATUS_SWIPE_TOO_FAST		=	12;//Bad image
//		public static final int	STATUS_SWIPE_TOO_SLOW		=	13;//Bad image
		public static final int	STATUS_SWIPE_TOO_SHORT		=	14;//Bad image
//		public static final int	STATUS_SWIPE_TOO_SKEWED		=	15;//Bad image
//		public static final int	STATUS_SWIPE_TOO_LEFT		=	16;//Bad image
//		public static final int	STATUS_SWIPE_TOO_RIGHT		=	17;//Bad image
//		public static final int	STATUS_USER_TOO_FAR			=	19;//Bad image
//		public static final int	STATUS_USER_TOO_CLOSE		=	20;//Bad image
//		public static final int	STATUS_LUX_TOO_LOWER		=	21;//Bad image
//		public static final int	STATUS_LUX_TOO_HIGHER		=	22;//Bad image
//		public static final int	STATUS_SWIPE_TOO_WET		=	25;//Bad image
//		public static final int	STATUS_SWIPE_TOO_DRY		=	26;//Bad image
		
		public static final int DEV_STATE_CHANGE				= FpResDef.DEV_STATE_CHANGE;
	    public static final int DEV_STATE_DISCONNECTED 			= FpResDef.DEV_STATE_DISCONNECTED;
	    public static final int DEV_STATE_CONNECTING 			= FpResDef.DEV_STATE_CONNECTING;
	    public static final int DEV_STATE_CONNECTED 			= FpResDef.DEV_STATE_CONNECTED;
	    public static final int DEV_EXTRA_PERMISSION_GRANTED 	= FpResDef.DEV_EXTRA_PERMISSION_GRANTED;
	    public static final int DEV_ACTION_USB_DEVICE_ATTACHED 	= FpResDef.DEV_ACTION_USB_DEVICE_ATTACHED;
	    public static final int DEV_ACTION_USB_DEVICE_DEATTACHED 	= FpResDef.DEV_ACTION_USB_DEVICE_DEATTACHED;
	    public static final int DEV_STATE_NOT_FOUND				= FpResDef.DEV_STATE_NOT_FOUND;
		
		public void onBadImage(int status);
		public void onServiceConnected();
		public void onServiceDisConnected();
		public void onFingerFetch();
		public void onFingerImageGetted();
		public void onUserAbort();
		public void onStatus(int status);
	}
	public interface PluginDeviceListener {
		public void onPermissionGranted();
		public void onDevicePlug();
		public void onDeviceUnPlug();
	}
	public interface GetRawDataListener {
		public void onGetRawData(byte[] rawData, int width, int height);
	}
	public interface EnrollListener {
		public void onSuccess();
		public void onFail();
		public void onProgress();		
	}
	public interface VerifyListener {
		public void onSuccess();
		public void onFail();
	}
	
	public interface TinyEnrollListener {
		public void onStatusCandidate(int status, int[] mapInfo, byte[] map);
		public void onEnrollMap(int[] mapInfo, byte[] map);
	}
	
	public interface VerifyLearningListener{
		public void LearningScore(int score);
	}
	
	public interface ThreadImageListener{
		void onGetImg(byte[] img, int width, int height);
	}		
	
	public interface MatchedImageListener{
		void onGetMatchedImg(byte[] img, int width, int height);
	}	
	
	public interface EnrollMapProgressListener{
		void onEnrollMapProgress(int progress);
	}
}
