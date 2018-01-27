package egistec.fingerauth.api;

import static egistec.csa.client.api.Fingerprint.CAPTURE_FAILED;
import static egistec.csa.client.api.Fingerprint.CAPTURE_FINISHED;
import static egistec.csa.client.api.Fingerprint.CAPTURE_READY;
import static egistec.csa.client.api.Fingerprint.CAPTURE_STARTED;
import static egistec.csa.client.api.Fingerprint.CAPTURE_SUCCESS;
import static egistec.csa.client.api.Fingerprint.ENROLL_FAILED;
import static egistec.csa.client.api.Fingerprint.ENROLL_FAILURE_CANCELED;
import static egistec.csa.client.api.Fingerprint.ENROLL_SUCCESS;
import static egistec.csa.client.api.Fingerprint.IDENTIFY_FAILED;
import static egistec.csa.client.api.Fingerprint.IDENTIFY_SUCCESS;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import egistec.csa.client.api.Fingerprint;
import egistec.csa.client.api.Fingerprint.FingerprintEventListener;
import egistec.fingerauth.api.FPAuthListeners.EnrollListener;
import egistec.fingerauth.api.FPAuthListeners.EnrollMapProgressListener;
import egistec.fingerauth.api.FPAuthListeners.GetRawDataListener;
import egistec.fingerauth.api.FPAuthListeners.MatchedImageListener;
import egistec.fingerauth.api.FPAuthListeners.PluginDeviceListener;
import egistec.fingerauth.api.FPAuthListeners.StatusListener;
import egistec.fingerauth.api.FPAuthListeners.ThreadImageListener;
import egistec.fingerauth.api.FPAuthListeners.TinyEnrollListener;
import egistec.fingerauth.api.FPAuthListeners.VerifyLearningListener;
import egistec.fingerauth.api.FPAuthListeners.VerifyListener;



public class SettingLib {
    private static final String TAG = "SettingLib";
   
    public static final byte OP_NONE 		= 10;
    public static final byte OP_ENROLL 	= 11;
    public static final byte OP_IDENTIFY 	= 12;
    public static final byte OP_CONNECTING	= 13;
      
    public static final int FP_RESULT 						= FpResDef.FP_RESULT;
    public static final int FP_RES_ENROLL_OK				= FpResDef.FP_RES_ENROLL_OK;
    public static final int FP_RES_ENROLL_FAIL 				= FpResDef.FP_RES_ENROLL_FAIL;
    public static final int FP_RES_MATCHED_OK				= FpResDef.FP_RES_MATCHED_OK;
    public static final int FP_RES_MATCHED_FAIL 			= FpResDef.FP_RES_MATCHED_FAIL;
    public static final int FP_RES_GETTING_IMAGE			= FpResDef.FP_RES_GETTING_IMAGE;
    public static final int FP_RES_GETTED_IMAGE				= FpResDef.FP_RES_GETTED_IMAGE;
    public static final int FP_RES_EXTRACTING_FEATURE		= FpResDef.FP_RES_EXTRACTING_FEATURE;
    public static final int FP_RES_GETTED_GOOD_IMAGE 		= FpResDef.FP_RES_GETTED_GOOD_IMAGE;
    public static final int FP_RES_GETTED_BAD_IMAGE 		= FpResDef.FP_RES_GETTED_BAD_IMAGE;
    public static final int FP_RES_ENROLL_DUPLICATED 		= FpResDef.FP_RES_ENROLL_DUPLICATED;
    public static final int FP_RES_ENROLL_COUNT 			= FpResDef.FP_RES_ENROLL_COUNT;
    public static final int FP_RES_ABORT_OK					= FpResDef.FP_RES_ABORT_OK;
    public static final int FP_RES_ABORT_FAIL				= FpResDef.FP_RES_ABORT_FAIL;
    public static final int FP_RES_GETTED_IMAGE_TOO_SHORT 	= FpResDef.FP_RES_GETTED_IMAGE_TOO_SHORT;
    public static final int FP_RES_SENSOR_TIMEOUT 			= FpResDef.FP_RES_SENSOR_TIMEOUT;
    public static final int FP_RES_NOT_CONNECTED 			= FpResDef.FP_RES_NOT_CONNECTED;
    public static final int FP_RES_IMAGE_INFO				= FpResDef.FP_RES_IMAGE_INFO;
    public static final int FP_RES_BLOB						= FpResDef.FP_RES_BLOB;
    public static final int FP_RES_DELETE_OK				= FpResDef.FP_RES_DELETE_OK;
    public static final int FP_RES_DELETE_FAIL				= FpResDef.FP_RES_DELETE_FAIL;
    public static final int FP_RES_GETTED_IMAGE_FAIL 		= FpResDef.FP_RES_GETTED_IMAGE_FAIL;
    public static final int FP_RES_IMAGE_TOO_HEAVY 			= FpResDef.FP_RES_IMAGE_TOO_HEAVY;
    public static final int FP_RES_IMAGE_TOO_LIGHT 			= FpResDef.FP_RES_IMAGE_TOO_LIGHT;
    public static final int FP_RES_FINGER_LIST		    	= FpResDef.FP_RES_FINGER_LIST;
    public static final int FP_RES_STRING					= FpResDef.FP_RES_STRING;
    public static final int FP_RES_VERSION					= FpResDef.FP_RES_VERSION;
    public static final int FP_RES_COMMAND_ERROR			= FpResDef.FP_RES_COMMAND_ERROR;
    public static final int FP_RES_CAPTURE_VERIFY_OK		= FpResDef.FP_RES_CAPTURE_VERIFY_OK;

    public static final int FP_RES_CHECKSUM_FAIL 			= FpResDef.FP_RES_CHECKSUM_FAIL;
    public static final int FP_RES_FLASH_WRITE_OK 			= FpResDef.FP_RES_FLASH_WRITE_OK;
    public static final int FP_RES_FLASH_WRITE_FAIL 		= FpResDef.FP_RES_FLASH_WRITE_FAIL;
    public static final int FP_RES_FLASH_DEL_OK 			= FpResDef.FP_RES_FLASH_DEL_OK;
    public static final int FP_RES_FLASH_DEL_FAIL 			= FpResDef.FP_RES_FLASH_DEL_FAIL;
    public static final int FP_RES_FLASH_READ_OK 			= FpResDef.FP_RES_FLASH_READ_OK;
    public static final int FP_RES_FLASH_READ_FAIL 			= FpResDef.FP_RES_FLASH_READ_FAIL;
    public static final int FP_RES_FLASH_DATA_NOT_FOUND 	= FpResDef.FP_RES_FLASH_DATA_NOT_FOUND;
    public static final int FP_RES_INVALID_PASSWORD 		= FpResDef.FP_RES_INVALID_PASSWORD;
    public static final int FP_RES_KEY_LIST 				= FpResDef.FP_RES_KEY_LIST;
    public static final int FP_RES_ENROLL_FEATURE_BLOB 		= FpResDef.FP_RES_ENROLL_FEATURE_BLOB;
    public static final int FP_RES_VERIFY_FEATURE_BLOB 		= FpResDef.FP_RES_VERIFY_FEATURE_BLOB;
    public static final int FP_RES_SYSTEM_INFO 				= FpResDef.FP_RES_SYSTEM_INFO;
    public static final int FP_RES_NEED_AUTHORIZED 			= FpResDef.FP_RES_NEED_AUTHORIZED;
    public static final int FP_RES_INVALID_PARAMETER 		= FpResDef.FP_RES_INVALID_PARAMETER;
    public static final int FP_RES_SYSTEM_INFO_NOT_EXISTED	= FpResDef.FP_RES_SYSTEM_INFO_NOT_EXISTED;
    public static final int FP_RES_FLASH_RESET_OK			= FpResDef.FP_RES_FLASH_RESET_OK;
    public static final int FP_RES_FLASH_RESET_FAIL			= FpResDef.FP_RES_FLASH_RESET_FAIL;
    public static final int FP_RES_VOLTAGE					= FpResDef.FP_RES_VOLTAGE;
    public static final int FP_RES_NO_BATTERY				= FpResDef.FP_RES_NO_BATTERY;
    public static final int FP_RES_POWEROFF					= FpResDef.FP_RES_POWEROFF;
    public static final int FP_RES_NAVIGATION				= FpResDef.FP_RES_NAVIGATION;
    public static final int FP_RES_NO_PERMISSION			= FpResDef.FP_RES_NO_PERMISSION;

    
    public static final int BLOB_TYPE						= FpResDef.BLOB_TYPE;
    public static final int BLOB_TYPE_IMAGE					= FpResDef.BLOB_TYPE_IMAGE;
	public static final int BLOB_TYPE_ENROLL_FEATURE		= FpResDef.BLOB_TYPE_ENROLL_FEATURE;
	public static final int BLOB_TYPE_VERIFY_FEATURE		= FpResDef.BLOB_TYPE_VERIFY_FEATURE;
	public static final int BLOB_TYPE_FLASH_DATA			= FpResDef.BLOB_TYPE_FLASH_DATA;
	public static final int BLOB_TYPE_AES_ENCRYPTED_DATA	= FpResDef.BLOB_TYPE_AES_ENCRYPTED_DATA;
	public static final int BLOB_TYPE_AES_DECRYPTED_DATA	= FpResDef.BLOB_TYPE_AES_DECRYPTED_DATA;
	public static final int BLOB_TYPE_RSA_ENCRYPTED_DATA	= FpResDef.BLOB_TYPE_RSA_ENCRYPTED_DATA;
	public static final int BLOB_TYPE_RSA_DECRYPTED_DATA	= FpResDef.BLOB_TYPE_RSA_DECRYPTED_DATA;
	public static final int BLOB_TYPE_SIGNATURE_DATA		= FpResDef.BLOB_TYPE_SIGNATURE_DATA;
	public static final int BLOB_TYPE_RSA_PUBLIC_KEY		= FpResDef.BLOB_TYPE_RSA_PUBLIC_KEY;
	public static final int BLOB_TYPE_SYS_INFO				= FpResDef.BLOB_TYPE_SYS_INFO;

    public static final int DEV_STATE_CHANGE				= FpResDef.DEV_STATE_CHANGE;
    public static final int DEV_STATE_DISCONNECTED 			= FpResDef.DEV_STATE_DISCONNECTED;
    public static final int DEV_STATE_CONNECTING 			= FpResDef.DEV_STATE_CONNECTING;
    public static final int DEV_STATE_CONNECTED 			= FpResDef.DEV_STATE_CONNECTED;

    public static final int NONE_DEVICE = FpResDef.NONE_DEVICE;
    public static final int YUKEY_DEVICE = FpResDef.YUKEY_DEVICE;
    public static final int SENSOR_DEVICE = FpResDef.SENSOR_DEVICE;
    public static final int YUKEY_L_DEVICE = FpResDef.YUKEY_L_DEVICE;
    
    public static final int SERVICE_STATE = 4000;
    public static final int SERVICE_CONNECTED = 4001;
    public static final int SERVICE_DISCONNECTED = 4002;
    
    public static final int ACT_RES_DO_ABORT			= 5001;
    public static final int ACT_RES_MATCHED_OK			= FP_RES_MATCHED_OK;
    public static final int ACT_RES_MATCHED_FAIL		= FP_RES_MATCHED_FAIL;
    public static final int ACT_RES_DEV_DISCONNECTED	= DEV_STATE_DISCONNECTED;
    public static final int ACT_RES_CAPTURE_FEATURE_OK	= 5002;
    public static final int ACT_RES_NEED_AUTHORIZED	= FP_RES_NEED_AUTHORIZED;
    
    public static final int ERR_CODE_OK = FpResDef.ERR_CODE_OK;
    public static final int ERR_CODE_INVALID_DATA = FpResDef.ERR_CODE_INVALID_DATA;
    public static final int ERR_CODE_FILESYSTEM_OPERATION_ERR = FpResDef.ERR_CODE_FILESYSTEM_OPERATION_ERR;
    public static final int ERR_CODE_NO_PWD_DATA = FpResDef.ERR_CODE_NO_PWD_DATA;
    public static final int ERR_CODE_INCORRECT_PWD = FpResDef.ERR_CODE_INCORRECT_PWD;
    public static final int ERR_CODE_NO_PERMISSION = FpResDef.ERR_CODE_NO_PERMISSION;
    public static final int ERR_CODE_ENCRYPT_FAIL = FpResDef.ERR_CODE_ENCRYPT_FAIL;
    public static final int ERR_CODE_UNKNOW_ERR = FpResDef.ERR_CODE_UNKNOW_ERR;
    public static final int ERR_CODE_DB_DAMAGED = FpResDef.ERR_CODE_DB_DAMAGED;
    public static final int ERR_CODE_INTERNAL_ERROR = FpResDef.ERR_CODE_INTERNAL_ERROR;
    
    protected static final int	STATUS_SENSOR_OPEN			=	1;//onDevice
    protected static final int	STATUS_SENSOR_CLOSE			=	2;//onDevice
    protected static final int	STATUS_IMAGE_FETCH			=	3;//onReady
    protected static final int	STATUS_IMAGE_READY			=	4;//onReady
    protected static final int	STATUS_IMAGE_BAD			=	5;//Bad image
    protected static final int	STATUS_FEATURE_LOW			=	6;//Bad image
    protected static final int	STATUS_OPERATION_BEGIN		=	7;//onBegin
    protected static final int	STATUS_OPERATION_END		=	8;//onEnd
    protected static final int	STATUS_IMAGE_FETCHING		=	9;//onSwipping
    protected static final int	STATUS_FINGER_DETECTED		=	10;//onTouch
    protected static final int	STATUS_FINGER_REMOVED		=	11;//onTouch
    protected static final int	STATUS_SWIPE_TOO_FAST		=	12;//Bad image
    protected static final int	STATUS_SWIPE_TOO_SLOW		=	13;//Bad image
    protected static final int	STATUS_SWIPE_TOO_SHORT		=	14;//Bad image
    protected static final int	STATUS_SWIPE_TOO_SKEWED		=	15;//Bad image
    protected static final int	STATUS_SWIPE_TOO_LEFT		=	16;//Bad image
    protected static final int	STATUS_SWIPE_TOO_RIGHT		=	17;//Bad image
    protected static final int	STATUS_SENSOR_UNPLUG		=	18;//onDevice
    protected static final int	STATUS_USER_TOO_FAR			=	19;//Bad image
    protected static final int	STATUS_USER_TOO_CLOSE		=	20;//Bad image
    protected static final int	STATUS_LUX_TOO_LOWER		=	21;//Bad image
    protected static final int	STATUS_LUX_TOO_HIGHER		=	22;//Bad image
    protected static final int	STATUS_FINGER_TOUCH			=	23;//onTouch
    protected static final int	STATUS_FINGER_REMOVE		=	24;//onTouch
    protected static final int	STATUS_SWIPE_TOO_WET		=	25;//Bad image
    protected static final int	STATUS_SWIPE_TOO_DRY		=	26;//Bad image
    protected static final int	STATUS_SENSOR_TIMEOUT		=	27;//Timeout
    protected static final int	STATUS_USER_ABORT			=	28;//onAbort
    protected static final int STATUS_GET_IMAGE_FAIL		=	29;//Bad image
    protected static final int STATUS_SWIPE_IMAGE_BAD		=	30;//Bad image
    protected static final int STATUS_DIRTY_IMAGE			=	31;//Bad image
    protected static final int STATUS_TARGET_SENSOR_NOT_FOUND = 32;//onDevice
    //libvkapi.h
    protected static final int STATUS_IMAGE_SMALL			=	33;//Bad image

    protected StatusListener statusListener;
	protected PluginDeviceListener pluginDeviceListener;
	protected GetRawDataListener getRawDataListener;
    protected EnrollListener enrollListener;
    protected VerifyListener verifyListener;
	protected TinyEnrollListener mTinyEnrollListener;
	protected VerifyLearningListener mLearningListener;
	protected ThreadImageListener mThreadImageListener;
	protected MatchedImageListener mMatchedImageListener;
	protected EnrollMapProgressListener mEnrollMapProgressListener;
    protected IFPAuthService mFPAuthService = null;
    protected FingerprintEventListener FingerprintEventListener;
	
	protected Context mContext;
	public String mUserID = "SYSTEM";
	protected Fingerprint mFPCsaClientLib;	
	public static final String mPW = "0000";
	public static byte[] mDefaultPW; 
	public static boolean bNeedSetDefaultPW = true;
	public int identifyResultt = 99;
	public int identifyindex = 99;


	public AlertDialog getAlertDialog(String message){
        return new AlertDialog.Builder(mContext)
        .setMessage(message)
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    };
        })
        .create();
        
    }
	
	
	
	public SettingLib(Context context){
		mContext = context;
		mFPCsaClientLib = Fingerprint.create(mContext);
		//if(bNeedSetDefaultPW)
			//SetPassWord();
		
		mFPCsaClientLib.setEventListener(new FingerprintEventListener() {
			
			@Override
			public void onFingerprintEvent(int eventId, Object eventData) {
				Log.d(TAG, "eventId="+eventId);
				switch(eventId){				
					case Fingerprint.ENROLL_STATUS:
						if(eventData instanceof Fingerprint.EnrollStatus){
							Fingerprint.EnrollStatus enrollStatus = 
							  (Fingerprint.EnrollStatus) eventData;
							notifyEnrollMapProgress(enrollStatus.progress);
							notifyOnProgress();
						}
						break;	
					case Fingerprint.ENROLL_BITMAP:
						Log.d(TAG, "Fingerprint.ENROLL_BITMAP:");
						if(eventData instanceof Fingerprint.EnrollBitmap){
							Fingerprint.EnrollBitmap enrollBitmap = 									
							  (Fingerprint.EnrollBitmap) eventData;	

						}
						break;
					case CAPTURE_READY:
						notifyOnStatus(StatusListener.FP_RES_FINGER_WAIT_FPON);
						//mLogBoard.append("CAPTURE_READY\n");
						break;
					case CAPTURE_STARTED:
						notifyOnStatus(StatusListener.FP_RES_SOMETHING_ON_SENSOR);
						//mLogBoard.append("CAPTURE_STARTED\n");
						break;
					/*case CAPTURE_COMPLETED:
						if(eventData instanceof Fingerprint.FingerprintBitmap){
							Fingerprint.FingerprintBitmap fpBitmap = 
							  (Fingerprint.FingerprintBitmap) eventData;	
							mLogBoard.append(String.format("CAPTURE_COMPLETED width=%d height=%d quality=%d\n", 
							  fpBitmap.width, fpBitmap.height, fpBitmap.quality));
						}
						break;*/
					case CAPTURE_FINISHED:
						notifyOnStatus(StatusListener.FP_RES_FINGER_REMOVED);
						//mLogBoard.append("CAPTURE_FINISHED\n");
						break;
					case CAPTURE_SUCCESS:
						//mLogBoard.append("CAPTURE_SUCCESS\n");
						if(eventData instanceof Fingerprint.FingerprintBitmap){
							Fingerprint.FingerprintBitmap fpBitmap = 
							  (Fingerprint.FingerprintBitmap) eventData;	
							if(fpBitmap.bitmap != null){
								int bytes = fpBitmap.bitmap.getByteCount();
								ByteBuffer buffer = ByteBuffer.allocate(bytes); 
								fpBitmap.bitmap.copyPixelsToBuffer(buffer); 

								byte[] array = buffer.array(); 
								for (int index = 0; index < bytes; index ++) {
									array[index] = (byte) ~array[index];
						      		 }
								
								notifyOnMatchedImg(array, fpBitmap.width, fpBitmap.height);
							}							
						}
						break;
					case CAPTURE_FAILED:
						Log.d(TAG,"CAPTURE_FAILED");
						if(eventData instanceof Fingerprint.FingerprintBitmap){
							Fingerprint.FingerprintBitmap fpBitmap = 
							  (Fingerprint.FingerprintBitmap) eventData;	
							if(fpBitmap.quality == Fingerprint.QUALITY_FAILED)
							{
								Log.d(TAG,"QUALITY_FAILED");
								notifyOnBadImage(FpResDef.FP_RES_GETTED_BAD_IMAGE);
							}
							else if(fpBitmap.quality == Fingerprint.QUALITY_PARTIAL_TOUCH)
							{
								Log.d(TAG,"QUALITY_PARTIAL_TOUCH");
								notifyOnBadImage(FpResDef.FP_RES_PARTIAL_IMG);
							}
							else if(fpBitmap.quality == Fingerprint.QUALITY_WET_FINGER)
							{
								Log.d(TAG,"QUALITY_WET_FINGER");
								notifyOnBadImage(FpResDef.FP_RES_WET_IMG);
							}
							else if(fpBitmap.quality == Fingerprint.QUALITY_WATER)
							{
								Log.d(TAG,"QUALITY_WATER");
								notifyOnBadImage(FpResDef.FP_RES_WATER_IMG);
							}
							else if(fpBitmap.quality == Fingerprint.QUALITY_TOO_FAST)
							{
								Log.d(TAG,"QUALITY_TOO_FAST");
								notifyOnBadImage(FpResDef.FP_RES_FAST_IMG);
							}
						}
						break;
					case ENROLL_SUCCESS:
						Log.d(TAG,"ENROLL_SUCCESS\n");					
						notifyOnEnrollSuccess();
//						mLogBoard.append("ENROLL_SUCCESS\n");
//						mEnrollRequest.setText(R.string.enroll_request);
//						mEnrollRequestFlag = false;
						break;
					case ENROLL_FAILED:
//						mLogBoard.append("ENROLL_FAILED\n");
//						mEnrollRequest.setText(R.string.enroll_request);
//						mEnrollRequestFlag = false;
						break;
					case ENROLL_FAILURE_CANCELED:
//						mLogBoard.append("ENROLL_FAILURE_CANCELED\n");
//						mEnrollRequest.setText(R.string.enroll_request);
//						mEnrollRequestFlag = false;
						break;
					case IDENTIFY_SUCCESS:
//						mLogBoard.append("IDENTIFY_SUCCESS\n");
						if(eventData instanceof Fingerprint.IdentifyResult){
							Fingerprint.IdentifyResult identifyResult = 
							  (Fingerprint.IdentifyResult) eventData;
							identifyResultt = identifyResult.result;
							identifyindex = identifyResult.index;
							Log.d(TAG,"IDENTIFY_SUCCESS " + identifyResultt + ", " + identifyindex);	
							notifyOnVerifySuccess();
//							mLogBoard.append(String.format("result=%d index=%d\n", 
//							  identifyResult.result, identifyResult.index));
						}
						break;
					case IDENTIFY_FAILED:
//						mLogBoard.append("IDENTIFY_FAILED\n");
						if(eventData instanceof Fingerprint.IdentifyResult){
							Fingerprint.IdentifyResult identifyResult = 
							  (Fingerprint.IdentifyResult) eventData;
							notifyOnVerifyFail();
//							mLogBoard.append(String.format("result=%d index=%d\n", 
//							  identifyResult.result, identifyResult.index));
						}
						break;
					/*case IDENTIFY_FAILURE_NOT_MATCH:
						mLogBoard.append("IDENTIFY_FAILURE_NOT_MATCH\n");
						break;
					case IDENTIFY_FAILURE_CANCELED:
						mLogBoard.append("IDENTIFY_FAILURE_CANCELED\n");
						break;
					case IDENTIFY_FAILURE_BAD_QUALITY:
						mLogBoard.append("IDENTIFY_FAILURE_BAD_QUALITY\n");
						break;*/
				}
			}
		});	
		
		//new finger.java;
	}
	

/*
	protected ServiceConnection mFPAuthConnection = new ServiceConnection() {
        public void onServiceConnected(ComponentName className,
                IBinder service) {
        	Log.d(TAG, "onServiceConnected");
        	mFPAuthService = IFPAuthService.Stub.asInterface(service);

            try {
				mFPAuthService.registerCallback(mFPAuthServiceCallback);
            	notifyOnServiceConnect();

            } catch (RemoteException e) {

            }
        }
        
        public void onServiceDisconnected(ComponentName className) {
        	Log.d(TAG, "onServiceDisConnected");
			mFPAuthService = null;
        	notifyOnServiceDisConnect();
        }

    };
	*/
    protected IFPAuthServiceCallback mFPAuthServiceCallback = new IFPAuthServiceCallback.Stub() {

    	public void postMessage(int what, int arg1, int arg2) {
    		mHandler.obtainMessage(what, arg1, arg2).sendToTarget();
        }
    };

   /*
    public void bind()
    {
    	Log.d(TAG,"+++ bind +++");
        List<PackageInfo> serviceList = mContext.getPackageManager().getInstalledPackages(PackageManager.GET_SERVICES);
        Log.d(TAG,"+++ after bind +++");
        PackageInfo packageInfo = null;
        for (PackageInfo info : serviceList) {
            if (info.packageName.equals("egistec.fingerauth.host.service")) {
                packageInfo = info;
                break;
            }
        }
        if (packageInfo != null) {
			  if(!(mContext.bindService(new Intent(IFPAuthService.class.getName()),
                    mFPAuthConnection, Context.BIND_AUTO_CREATE)))
				  Log.e(TAG, "bindService fail");
        }
        else {
            Log.e(TAG, "FPAuthService.apk is not installed!");
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setCancelable(false);
            builder.setTitle("Error_title");
            builder.setMessage("FPAutService_is_not_installed");
            builder.setPositiveButton(android.R.string.ok, null);
            builder.show();
    	}    	
    }

    public void unbind()
    {
    	Log.d(TAG,"+++ unbind +++");
    	if (mFPAuthService==null) return;
        try {
        	mFPAuthService.unregisterCallback(mFPAuthServiceCallback);
        } catch (RemoteException e) {
        	e.printStackTrace();
        }
    	mContext.unbindService(mFPAuthConnection);
    }
 */   
    public boolean connectDevice() {
    	Log.d(TAG, "connectDevice!!!!!!!!!!!!!!!!!!!!!");
    	return mFPCsaClientLib.getSensorStatus() == Fingerprint.SENSOR_OK ? true : false;
 
	}

	public boolean disconnectDevice() {
		Log.d(TAG, "disconnectDevice");
		try{
			return mFPAuthService.disconnectDevice();
    	} catch (RemoteException e) {
    		Log.e(TAG, "disconnectDevice() RemoteException:"+e.getMessage());
    		return false;
    	} catch(Exception e) {
    		Log.e(TAG, "disconnectDevice() Exception:"+e.getMessage());
    		return false;
    	}
	}
	
	public boolean identify() {
		Log.d(TAG, "identify");
		int res = mFPCsaClientLib.identify(mUserID);
		Log.d(TAG, "identify " + res);
		return (res == 0 );
	
	}
	
	public boolean learningIdentify(String fid){
		try{
			return mFPAuthService.learningIdentify(fid);
		}catch(RemoteException e){
			Log.e(TAG, "learningIdentify() RemoteException:"+e.getMessage()); 
			return false;
		}catch(Exception e){
			Log.e(TAG, "learningIdentify() Exception:"+e.getMessage()); 
			return false;
		}		
	}
	
	public boolean learning(){
		Log.d(TAG, "+++ learning +++");
		try{
			return mFPAuthService.learning();
		}catch(RemoteException e){
			Log.e(TAG, "learningIdentify() RemoteException:"+e.getMessage()); 
			return false;
		}catch(Exception e){
			Log.e(TAG, "learningIdentify() Exception:"+e.getMessage()); 
			return false;
		}		
	}
	
	public boolean abort() {
		Log.e(TAG, "abort() ");
		if( mFPCsaClientLib.cancel() == Fingerprint.RESULT_OK){
			notifyOnUserAbort();
			return true;
		}
		else
			return false;
	
	}
	
	public int getDeviceType() {
    	try {
    		return mFPAuthService.getDeviceType();
    	}catch(RemoteException e) {
    		Log.e(TAG, "getDeviceType() RemoteException:"+e.getMessage());
    		return NONE_DEVICE;
    	}catch(Exception e) {
    		Log.e(TAG, "getDeviceType() Exception:"+e.getMessage());
    		return NONE_DEVICE;
    	}
    }	
	
	public boolean isEnrolled() {
    	try {
    		return mFPAuthService.isEnrolled();
    	}catch(RemoteException e) {
    		Log.e(TAG, "isEnrolled() RemoteException:"+e.getMessage());
    		return false;
    	}catch(Exception e) {
    		Log.e(TAG, "isEnrolled() Exception:"+e.getMessage());
    		return false;
    	}
    }
	
	public boolean isSimplePwd() {
    	try {
    		return mFPAuthService.isSimplePwd();
    	}catch(RemoteException e) {
    		Log.e(TAG, "isSimplePwd() RemoteException:"+e.getMessage());
    		return false;
    	}catch(Exception e) {
    		Log.e(TAG, "isSimplePwd() Exception:"+e.getMessage());
    		return false;
    	}
    }
	
	public boolean verifyPwd(String pwd) {
    	try {
    		return mFPAuthService.verifyPwd(pwd);
    	}catch(RemoteException e) {
    		Log.e(TAG, "isEnrolled() RemoteException:"+e.getMessage());
    		return false;
    	}catch(Exception e) {
    		Log.e(TAG, "isEnrolled() Exception:"+e.getMessage());
    		return false;
    	}
    }
	
	public boolean DataSet(String key, String value, String passwd) {
		try{
			return mFPAuthService.DataSet(key, value, passwd);
		}catch(RemoteException e){
			Log.e(TAG, "DataSet() RemoteException:"+e.getMessage());
			return false;
		}catch(Exception e) {
			Log.e(TAG, "DataSet() Exception:"+e.getMessage());
			return false;
		}
	}
	public boolean DataDelete(String key, String passwd) {
		try{
			return mFPAuthService.DataDelete(key, passwd);
		}catch(RemoteException e){
			Log.e(TAG, "DataDelete() RemoteException:"+e.getMessage());
			return false;
		}catch(Exception e) {
			Log.e(TAG, "DataDelete() Exception:"+e.getMessage());
			return false;
		}
	}
	
	public String DataRead(String key, String passwd){
		try{
			return mFPAuthService.DataRead(key, passwd);
		}catch(RemoteException e){
			Log.e(TAG, "DataRead() RemoteException:"+e.getMessage());
			return null;
		}catch(Exception e) {
			Log.e(TAG, "DataRead() Exception:"+e.getMessage());
			return null;
		}
	}

	public String getMatchedUserID(){
		Log.e(TAG, "getMatchedUserID 1: "+ identifyindex);
		String egisId;
		if (identifyindex > 0)
		{
			String map[] = {"L0","L1","L2","L3","L4","R0","R1","R2","R3","R4"};
			egisId = mUserID + "_" + map[identifyindex-1];
			Log.e(TAG, "getMatchedUserID: "+ egisId);
			return egisId;
		}
		return null;
	}
	
	public void setOnGetRawData(boolean enable) {
    	try {
    		mFPAuthService.setOnGetRawData(enable);
    	}catch(RemoteException e) {
    		Log.e(TAG, "setOnGetRawData() RemoteException:"+e.getMessage());
    	}catch(Exception e) {
    		Log.e(TAG, "setOnGetRawData() Exception:"+e.getMessage());
    	}
    }
	
	public boolean captureRawData() {
    	try {
    		return mFPAuthService.captureRawData();
    	}catch(RemoteException e) {
    		Log.e(TAG, "captureRawData() RemoteException:"+e.getMessage());
    		return false;
    	}catch(Exception e) {
    		Log.e(TAG, "captureRawData() Exception:"+e.getMessage());
    		return false;
    	}
    }
	
	public boolean captureFrame() {
    	try {
    		return mFPAuthService.captureFrame();
    	}catch(RemoteException e) {
    		Log.e(TAG, "captureFrame() RemoteException:"+e.getMessage());
    		return false;
    	}catch(Exception e) {
    		Log.e(TAG, "captureFrame() Exception:"+e.getMessage());
    		return false;
    	}
    }

	private int convertErrCode(int err){
		int ret = ERR_CODE_OK;
		if((err >= FpResDef.CM_ERR_EXREND_KEY_NULL && err <= FpResDef.CM_ERR_INVALID_DATA_NULL) || err == FpResDef.SEVC_ERR_CM_NULL){
			ret = ERR_CODE_ENCRYPT_FAIL;
		}
		else{
			switch(err){
				case FpResDef.DB_ERR_FEATURE_LEN_ZERO:
				case FpResDef.DB_ERR_FEATURE_NULL:
				case FpResDef.DB_ERR_ID_LEN_ZERO:
				case FpResDef.DB_ERR_ID_NULL:
				case FpResDef.DB_ERR_ID_NOT_FOUND:
				case FpResDef.DB_ERR_DATA_LEN_ZERO:
				case FpResDef.DB_ERR_DATA_NULL:
				case FpResDef.SEVC_ERR_ID_NOT_FOUND:
				case FpResDef.SEVC_ERR_ID_NULL:
				case FpResDef.SEVC_ERR_VALUE_NULL:
				case FpResDef.SEVC_ERR_PWD_NULL:
				case FpResDef.SEVC_ERR_FEATURE_NULL:
				case FpResDef.NB_ERR_USER_ID_NULL:
				case FpResDef.NB_ERR_USER_ID_LEN_ZERO:
					ret = ERR_CODE_INVALID_DATA;
					break;
													 //
				case FpResDef.DB_ERR_IOEXCEPTION:
				case FpResDef.DB_ERR_LOAD_DATA_FAIL:
				case FpResDef.DB_ERR_OUT_DATA_NULL:
				case FpResDef.DB_ERR_IN_DATA_NULL:
				case FpResDef.DB_ERR_FOS_NULL:
				case FpResDef.DB_ERR_FIS_NULL:
				case FpResDef.DB_ERR_FD_NULL:
				case FpResDef.SEVC_ERR_FILE_DB_NULL:
				case FpResDef.SEVC_ERR_FPDEV_NULL:
				case FpResDef.SEVC_ERR_DB_PATH_NULL:
				case FpResDef.SEVC_ERR_DELETE_FILE_FAIL:
				case FpResDef.SEVC_ERR_COPY_FILE_FAIL:
				case FpResDef.SEVC_ERR_FILE_NOT_EXIST:
				case FpResDef.SEVC_ERR_RENAME_DB_ERR:
				case FpResDef.SEVC_ERR_IOEXCEPTION:
					ret = ERR_CODE_FILESYSTEM_OPERATION_ERR;
					break;
	
				case FpResDef.SEVC_ERR_HASH_PWD_NULL:
					ret = ERR_CODE_NO_PWD_DATA;
					break;
	
				case FpResDef.SEVC_ERR_PWD_INCORRECT:
					ret = ERR_CODE_INCORRECT_PWD;
					break;
	
				case FpResDef.SEVC_ERR_DB_LEN_ZERO:
				case FpResDef.SEVC_ERR_CHECK_DBSTATE_FAIL:
				case FpResDef.SEVC_ERR_BAK_DB_NOT_FOUND	:
				case FpResDef.SEVC_ERR_DB_NOT_FOUND:
				case FpResDef.SEVC_ERR_NEW_DB_NOT_FOUND:
					ret = ERR_CODE_DB_DAMAGED;
					break;
	
				case FpResDef.NB_ERR_LOAD_MOUDLE_FAIL:
				case FpResDef.NB_ERR_DO_GET_IMAGE_FAIL:
				case FpResDef.NB_ERR_FINGER_UTIL_NULL:
				case FpResDef.NB_ERR_CONNECT_FAIL:
				case FpResDef.NB_ERR_MAP_NULL:
					ret = ERR_CODE_INTERNAL_ERROR;
					break;
	
				case FpResDef.SEVC_ERR_NO_PERMISSION:
					ret = ERR_CODE_INTERNAL_ERROR;
					break;
				default:
					//SEVC_ERR_MATCH_FAIL
					//NB_ERR_UNKNOWN_TYPE
					ret = ERR_CODE_UNKNOW_ERR;
					break;
			}
		}
		Log.d(TAG, "convertErrCode ret = "+ret);
		return ret;
	}
	
	public int getLastErrCode(){
		try {
    		return convertErrCode(mFPAuthService.getLastErrCode());
    	}catch(RemoteException e) {
    		Log.e(TAG, "captureRawData() RemoteException:"+e.getMessage());
    		return -1;
    	}catch(Exception e) {
    		Log.e(TAG, "captureRawData() Exception:"+e.getMessage());
    		return -1;
    	}
	}
	
	



	private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
        	Log.d(TAG, "msg.what = " + msg.what);
        	int status;
        	switch(msg.what) 
        	{
        		case FpResDef.TINY_STATUS:
        		{
	   	 			Log.d(TAG, "get TINY_STATUS="+msg.arg1);
	   	 			
	   	 			switch(msg.arg1) {
	   	 				case FpResDef.TINY_STATUS_ENROLL_MAP:
	   	 					notifyOnEnrollMap();
	   	 					break;
	   	 				default:
	   	 					notifyOnStatusCandidate(msg.arg1);
	   	 					break;
	   	 			}
	   	 			break;
   	 		 
   	 	    	} //end case TINY_STATUS

        		case FpResDef.FP_RES_BLOB:
	        	{
		   	 		Log.d(TAG, "get FP_HANDLE_RESULT.FP_RES_BLOB");
		   	 		switch(msg.arg1) {
		   	 			case FpResDef.BLOB_TYPE_SYS_INFO:
		   	 				break;
		   	 			default:
		   	 				break;
		   	 		}
		   	 		break;
	   	 		 
	   	 	    } //end case FP_RES_BLOB
	        	case FpResDef.DEV_STATE_CHANGE: {
	        		 switch (msg.arg1) {
		        		 case FpResDef.DEV_EXTRA_PERMISSION_GRANTED://Todo
		                	Log.d(TAG, "DEV_EXTRA_PERMISSION_GRANTED");
		                	notifyOnPermissionGranted();
		                    break;
		     		 	case FpResDef.DEV_ACTION_USB_DEVICE_ATTACHED:
		             		Log.d(TAG, "DEV_ACTION_USB_DEVICE_ATTACHED");
		             		notifyOnDevicePlug();
		             		break;
	             		
		                 case FpResDef.DEV_ACTION_USB_DEVICE_DEATTACHED:
		             		Log.d(TAG, "DEV_ACTION_USB_DEVICE_DEATTACHED");
		             		notifyOnDeviceUnPlug();
		                 	break;
	         		 
	        		 }
	        		 break;
	        	}
        	
				case FpResDef.FP_RESULT: {
	        		 Log.d(TAG, "FP_HANDLE_RESULT.FP_RESULT msg.arg1 = " + msg.arg1);
	        		 	
	        		 switch (msg.arg1) {
	        		 
	                     case FpResDef.FP_RES_SOMETHING_ON_SENSOR:
	                         Log.d(TAG, "FP_RES_SOMETHING_ON_SENSOR");
	                         notifyOnStatus(StatusListener.FP_RES_SOMETHING_ON_SENSOR);
	                         break; 
		        		 
		        		 case FpResDef.FP_RES_ENROLL_COUNT: {
		        			 Log.d(TAG, "FP_HANDLE_RESULT.FP_RES_ENROLL_COUNT");
		        			 notifyOnProgress();
		        			 break;
		        		 }
		        		 
		        		case FpResDef.FP_RES_SENSOR_OPEN: {//Check
			            	Log.d(TAG, "FP_RES_SENSOR_OPEN");
			            	notifyOnStatus(FpResDef.FP_RES_SENSOR_OPEN);
		        		 	break;
		        		 }
		        		 
		            	case FpResDef.FP_RES_GETTED_GOOD_IMAGE: {//Check
		            		Log.d(TAG, "FP_HANDLE_RESULT.FP_RES_GETTED_GOOD_IMAGE");
		            		notifyOnStatus(StatusListener.GETTED_GOOD_IMAGE);
	        		 		break;
	        		 	}
		            	
		            	case FpResDef.FP_RES_EXTRACTING_FEATURE: {
		            		Log.d(TAG, "FP_HANDLE_RESULT.FP_RES_EXTRACTING_FEATURE");
		            		notifyOnStatus(StatusListener.EXTRACTING_FEATURE);
		            		break;
		            	}
		            	
		    			case FpResDef.FP_RES_ENROLL_OK: {
		        			 Log.d(TAG, "FP_RES_ENROLL_OK");
		        			 notifyOnEnrollSuccess();
		        			 break;
		        		 }
    					case FpResDef.FP_RES_ENROLL_FAIL: {
			       			 Log.d(TAG, "FP_RES_ENROLL_FAIL");
		        			 notifyOnEnrollFail();
		        			 break;
		        		 }
		    			
		    			case FpResDef.FP_RES_ABORT_OK: {
							Log.d(TAG, "FP_RES_ABORT_OK");
							notifyOnUserAbort();
							break;
						}
		    			
		    			case FpResDef.FP_RES_GETTING_IMAGE: {
            				Log.d(TAG, "FP_RES_GETTING_IMAGE");
            				notifyOnFingerFetch();
            				break;
            			}
		    			case FpResDef.FP_RES_GETTED_IMAGE: {
				    		Log.d(TAG, "FP_RES_GETTED_IMAGE");
				    		notifyOnFingerImageGetted();
				    		break;
				    	}
		    			case FpResDef.FP_RES_GETTED_BAD_IMAGE:
		    				status = FpResDef.FP_RES_GETTED_BAD_IMAGE;
		    				notifyOnBadImage(status);
		    				break;
		    			case FpResDef.FP_RES_PARTIAL_IMG:
		    				status = FpResDef.FP_RES_PARTIAL_IMG;
		    				notifyOnBadImage(status);
		    				break;
				    	case FpResDef.FP_RES_GETTED_IMAGE_FAIL:
				    		status = FpResDef.FP_RES_GETTED_IMAGE_FAIL;
				    		notifyOnBadImage(status);
				    		break;
				    	case FpResDef.FP_RES_IMAGE_INFO:
				    	    try {
				    	        byte[] rawData = mFPAuthService.getRawData();
				    	        int[] rawDataSize = mFPAuthService.getRawDataInfo();
				    	        notifyOnGetRawData(rawData, rawDataSize[0], rawDataSize[1]);
				    	     } catch (RemoteException e) {
				    	        // TODO Auto-generated catch block
				    	        e.printStackTrace();
				    	     }
				    	    break;
				    	case FpResDef.FP_RES_MATCHED_OK:
				    		Log.d(TAG, "FP_RES_MATCHED_OK");
				    		notifyOnVerifySuccess();
	        	 	  		break;
	
	        	 	  	case FpResDef.FP_RES_MATCHED_FAIL:
	        	 	  		Log.d(TAG, "FP_RES_MATCHED_FAIL");
	        	 	  		notifyOnVerifyFail();
	        	 	  		break;
		    			
	        	 	  	case FpResDef.FP_RES_THREAD_IMG:
	        	 	  		Log.d(TAG, "FP_RES_VERIFY_IMG");
	        	 	  		try {
	        	 	  			int[] imgInfo = mFPAuthService.getThreadImgInfo();
	        	 	  			notifyOnVerifyImg(mFPAuthService.getThreadImg(), imgInfo[0], imgInfo[1]);
	        	 	  		} catch (RemoteException e1) {
	        	 	  			// TODO Auto-generated catch block
	        	 	  			e1.printStackTrace();
	        	 	  		}
	        	 	  		break;	        	 	  		
	        	 	     case FpResDef.FP_RES_FINGER_DETECTED:
	                            Log.d(TAG, "FP_RES_FINGER_TOUCH");
	                            notifyOnStatus(StatusListener.FP_RES_FINGER_DETECTED);
	                            break;
	                     case FpResDef.FP_RES_FINGER_REMOVED:
	                          Log.d(TAG, "FP_RES_FINGER_REMOVE");
	                          notifyOnStatus(StatusListener.FP_RES_FINGER_REMOVED);
	                          break;	
	                     case FpResDef.FP_RES_FINGER_WAIT_FPON:
	                          Log.d(TAG, "FP_RES_FINGER_WAIT_FPON");
	                          notifyOnStatus(StatusListener.FP_RES_FINGER_WAIT_FPON);
	                          break; 

	                     case FpResDef.FP_RES_VERIFY_LEARNING:
	                    	 try {
	                    		 int score = mFPAuthService.getVerifyLearningScore();
	                    		 notifyOnLearningScore(score);
	                    	 } catch (RemoteException e) {
	                    		 // TODO Auto-generated catch block
	                    		 e.printStackTrace();
	                    	 }
	                    	 break;
	                     case FpResDef.FP_RES_ENROLL_MAP_PROGRESS:
	                    	 try {
	                    		 int progress = mFPAuthService.getEnrollProgress();
	                    		 notifyEnrollMapProgress(progress);
	                    	 } catch (RemoteException e) {
	                    		 // TODO Auto-generated catch block
	                    		 e.printStackTrace();
	                    	 }
	                    	 break;
	                     case FpResDef.FP_RES_MATCHED_IMG:
		        	 	  	try {
		        	 	  		int[] imgInfo = mFPAuthService.getMatchedImgInfo();
		        	 	  		notifyOnMatchedImg(mFPAuthService.getMatchedImg(), imgInfo[0], imgInfo[1]);
		        	 	  	} catch (RemoteException e1) {
		        	 	  		// TODO Auto-generated catch block
		        	 	  		e1.printStackTrace();
		        	 	  	}
	                    	 break;
	        		 }//switch
	        		break;
	        	}//case result	
				
        	}
        }  
	};
	
	
	
	//TinyListener
	private void notifyOnStatusCandidate(int status){
		if(mTinyEnrollListener != null){
			try {
				mTinyEnrollListener.onStatusCandidate(status, mFPAuthService.getTinyMapInfo(), null);
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void notifyOnEnrollMap(){
		if(mTinyEnrollListener != null){
			try {
				mTinyEnrollListener.onEnrollMap(mFPAuthService.getTinyMapInfo(), mFPAuthService.getTinyMap());
			} catch (RemoteException e) {
				e.printStackTrace();
			}
		}
	}

	private void notifyEnrollProgress(){
		
	}
	
	//StatusListener
	private void notifyOnBadImage(int status){
		Log.d(TAG, "notifyOnBadImage");
		if(statusListener != null)
    		statusListener.onBadImage(status);
    }
	private void notifyOnServiceConnect(){
		Log.d(TAG, "notifyOnServiceConnect");
		if(statusListener != null){
			Log.d(TAG, "notifyOnServiceConnect != null");
    		statusListener.onServiceConnected();}
    }
	private void notifyOnServiceDisConnect(){
		if(statusListener != null)
    		statusListener.onServiceDisConnected();
    }
	private void notifyOnPermissionGranted(){
		if(pluginDeviceListener != null)
    		pluginDeviceListener.onPermissionGranted();
    }
	private void notifyOnDevicePlug(){
		if(pluginDeviceListener != null)
    		pluginDeviceListener.onDevicePlug();
    }
	private void notifyOnDeviceUnPlug(){
		if(pluginDeviceListener != null)
    		pluginDeviceListener.onDeviceUnPlug();
    }
	private void notifyOnFingerFetch(){
		if(statusListener != null)
    		statusListener.onFingerFetch();
    }
	private void notifyOnFingerImageGetted(){
		if(statusListener != null)
    		statusListener.onFingerImageGetted();
    }
	private void notifyOnGetRawData(byte[] rawData, int width, int height){
		if(getRawDataListener != null)
    		getRawDataListener.onGetRawData(rawData, width, height);
    }
	private void notifyOnUserAbort(){
		if(statusListener != null)
    		statusListener.onUserAbort();
    }
    private void notifyOnStatus(int status){
    	if(statusListener != null)
    		statusListener.onStatus(status);
    }
    
    //EnrollListener
    private void notifyOnEnrollSuccess(){
    	if(enrollListener != null)
    		enrollListener.onSuccess();
    }
    private void notifyOnEnrollFail(){
    	if(enrollListener != null)
    		enrollListener.onFail();
    }
    private void notifyOnVerifySuccess(){
    	if(verifyListener != null)
    		verifyListener.onSuccess();
    }
    private void notifyOnVerifyFail(){
    	if(verifyListener != null)
    		verifyListener.onFail();
    }
    private void notifyOnVerifyImg(byte[] img, int width, int height){
    	if(mThreadImageListener != null)
    		mThreadImageListener.onGetImg(img, width, height);
    }   
    private void notifyOnMatchedImg(byte[] img, int width, int height){
    	if(mMatchedImageListener != null)
    		mMatchedImageListener.onGetMatchedImg(img, width, height);
    } 
    private void notifyOnProgress(){
    	if(enrollListener != null)
    		enrollListener.onProgress();
    }
    private void notifyOnLearningScore(int score){
    	if(mLearningListener != null)
    		mLearningListener.LearningScore(score);
    }
    private void notifyEnrollMapProgress(int progress){
    	Log.d(TAG, "notifyEnrollMapProgress "+progress);
    	if(mEnrollMapProgressListener != null)
    		mEnrollMapProgressListener.onEnrollMapProgress(progress);
    }
    public void setStatusListener(StatusListener listener){
    	statusListener = listener;
    }
	public void setPluginDeviceListener(PluginDeviceListener listener){
    	pluginDeviceListener = listener;
    }
	public void setGetRawDataListener(GetRawDataListener listener){
    	getRawDataListener = listener;
    }
	public void setEnrollListener(EnrollListener listener){
    	enrollListener = listener;
    }
    public void setVerifyListener(VerifyListener listener){
    	verifyListener = listener;
    }
    public void setTinyEnrollListener(TinyEnrollListener listener){
    	mTinyEnrollListener = listener;
    }
    public void setVerifyLearningListener(VerifyLearningListener listener){
    	mLearningListener = listener;
    }
    public void setThreadImageListener(ThreadImageListener listener){
    	mThreadImageListener = listener;
    }
    public void setEnrollMapProgressListener(EnrollMapProgressListener listener){
    	mEnrollMapProgressListener = listener;
    }
    public void setMatchedImageListener(MatchedImageListener listener){
    	mMatchedImageListener = listener;
    }
    
   
    
    public void cleanListeners(){
    	statusListener = null;
		pluginDeviceListener = null;
		getRawDataListener = null;
    	enrollListener = null;
    	verifyListener = null;
    	mTinyEnrollListener = null;
    	mLearningListener = null;
    	mThreadImageListener = null;
    	mEnrollMapProgressListener = null;
    }
    
    
	public boolean enroll(String uid) {
		Log.d(TAG, "enrollchechechecheche uid "+uid);
		int index = Integer.parseInt(uid.substring(uid.length()-1));
		mUserID = uid.substring(0, 6);
		mFPCsaClientLib.setEnrollSession(true);
		VerifyPassWord();
		Log.d(TAG, "enroll("+ mUserID + "," + index + ")");
		int ret = mFPCsaClientLib.enroll(mUserID, index+1);
		Log.d(TAG, "enroll("+ mUserID + "," + index + ") ret=" + ret);

		if(ret<0)
			return false;
		else
			return true;
	}

	public boolean swipeEnroll(String uid) {
		Log.d(TAG, "swipeEnrollchechechecheche uid "+uid);
		int index = Integer.parseInt(uid.substring(uid.length()-1));
		mUserID = uid.substring(0, 6);
		mFPCsaClientLib.setEnrollSession(true);
		VerifyPassWord();
		Log.d(TAG, "swipeEnroll("+ mUserID + "," + index + ")");
		int ret = mFPCsaClientLib.swipeEnroll(mUserID, index+1);
		Log.d(TAG, "swipeEnroll("+ mUserID + "," + index + ") ret=" + ret);

		if(ret<0)
			return false;
		else
			return true;
	}
    
    public boolean deleteFeature(String id) {
    	Log.d(TAG, "deleteFeature="+id );
    	int index = Integer.parseInt(id.substring(id.length()-1));
		mUserID = id.substring(0, 6);
		mFPCsaClientLib.setEnrollSession(true);
		VerifyPassWord();
		Log.d(TAG, "enroll("+ mUserID + "," + index + ")");
		int ret = mFPCsaClientLib.remove(mUserID, index+1);
		Log.d(TAG, "enroll("+ mUserID + "," + index + ") ret=" + ret);
		
		return (ret<0)? false:true;

	}
    
    public String getEnrollList(String userId) {
		Log.d(TAG, "getEnrollList="+userId + mUserID);
		String str = "";
		String map[] = {"L0","L1","L2","L3","L4","R0","R1","R2","R3","R4"};
		int[] fingerList = mFPCsaClientLib.getFingerprintIndexList(mUserID);
		Log.d(TAG, "getEnrollList2="+ fingerList);
		if(fingerList != null){
		for(int i = 0 ; i < fingerList.length ; i++)
			str += map[fingerList[i]-1] + ";";
		
			return str;
		}
		return null;
	}
    
    public boolean setPwd(String pwd) {
		try{
			return mFPAuthService.setPwd(pwd);
		}catch(RemoteException e){
			Log.e(TAG, "setPwd() RemoteException:"+e.getMessage());
			return false;
		}catch(Exception e) {
			Log.e(TAG, "setPwd() Exception:"+e.getMessage());
			return false;
		}
	}
	public boolean deletePwd() {
		try{
			return mFPAuthService.deletePwd();
		}catch(RemoteException e){
			Log.e(TAG, "flashDelete() RemoteException:"+e.getMessage());
			return false;
		}catch(Exception e) {
			Log.e(TAG, "flashDelete() Exception:"+e.getMessage());
			return false;
		}
	}
	
	public boolean isRequestVerify() {
    	try {
    		return mFPAuthService.isRequestVerify();
    	}catch(RemoteException e) {
    		Log.e(TAG, "isRequestVerify() RemoteException:"+e.getMessage());
    		return false;
    	}catch(Exception e) {                                                                                                                         
    		Log.e(TAG, "isRequestVerify() Exception:"+e.getMessage());
    		return false;
    	}
    }
	
	public int setEnrollSession(boolean flag)
	{
		return mFPCsaClientLib.setEnrollSession(flag);
	}
	
	public void SetPassWord(){
		Log.d(TAG, "SetPassWord");
		//mFPCsaClientLib.setEnrollSession(true);
		try {
			 int ret=0;
			 MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			 messageDigest.update(mPW.getBytes());
			 ret = mFPCsaClientLib.setPassword(mUserID, messageDigest.digest());
			 Log.d(TAG,"setPassword() ret="+ret+ "mUserID" + mUserID + "\n");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		//mFPCsaClientLib.setEnrollSession(false);
	}
	
	public void VerifyPassWord(){
		Log.d(TAG, "VerifyPassWord");
		try {
			 int ret=0;
			 MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			 messageDigest.update(mPW.getBytes());
			 ret = mFPCsaClientLib.verifyPassword(mUserID, messageDigest.digest());
			 Log.d(TAG,"verifyPassword() ret="+ret+ "mUserID" + mUserID + "\n");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}
	
	public void cleanup() throws InterruptedException{
		Log.d(TAG, "cleanup");
		try {
			int ret = 0;
			MessageDigest messageDigest = MessageDigest.getInstance("MD5");
			messageDigest.update(mPW.getBytes());
			for (int i = 0; i < 5; i++) {
				ret = mFPCsaClientLib.cleanup();
				Log.d(TAG, "cleanup() ret = " + ret);
				if (ret == 0)
					break;
				
				Thread.sleep(500);
			}
		}catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
	}
}
