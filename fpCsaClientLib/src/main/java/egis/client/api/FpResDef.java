package egis.client.api;

public class FpResDef {
    public static final int FP_RESULT 						= 1000;
    public static final int FP_RES_ENROLL_OK				= 1001;
    public static final int FP_RES_ENROLL_FAIL 				= 1002;
    public static final int FP_RES_MATCHED_OK				= 1003;
    public static final int FP_RES_MATCHED_FAIL 			= 1004;
    public static final int FP_RES_GETTING_IMAGE			= 1005;
    public static final int FP_RES_GETTED_IMAGE				= 1006;
    public static final int FP_RES_EXTRACTING_FEATURE		= 1007;
    public static final int FP_RES_GETTED_GOOD_IMAGE 		= 1008;
    public static final int FP_RES_GETTED_BAD_IMAGE 		= 1009;
    public static final int FP_RES_ENROLL_DUPLICATED 		= 1010;
    public static final int FP_RES_ENROLL_COUNT 			= 1011;
    public static final int FP_RES_ABORT_OK					= 1012;
    public static final int FP_RES_ABORT_FAIL				= 1014;
    public static final int FP_RES_GETTED_IMAGE_TOO_SHORT 	= 1015;
    public static final int FP_RES_SENSOR_TIMEOUT 			= 1016;
    public static final int FP_RES_NOT_CONNECTED 			= 1017;
    public static final int FP_RES_IMAGE_INFO				= 1018;
    public static final int FP_RES_BLOB						= 1019;
    public static final int FP_RES_DELETE_OK				= 1020;
    public static final int FP_RES_DELETE_FAIL				= 1021;
    public static final int FP_RES_GETTED_IMAGE_FAIL 		= 1022;
    public static final int FP_RES_IMAGE_TOO_HEAVY 			= 1023;
    public static final int FP_RES_IMAGE_TOO_LIGHT 			= 1024;
    public static final int FP_RES_FINGER_LIST		    	= 1025;
    public static final int FP_RES_STRING					= 1026;
    public static final int FP_RES_VERSION					= 1027;
    public static final int FP_RES_COMMAND_ERROR			= 1028;
    public static final int FP_RES_CAPTURE_VERIFY_OK		= 1029;

    public static final int FP_RES_CHECKSUM_FAIL 			= 1030;
    public static final int FP_RES_FLASH_WRITE_OK 			= 1031;
    public static final int FP_RES_FLASH_WRITE_FAIL 		= 1032;
    public static final int FP_RES_FLASH_DEL_OK 			= 1033;
    public static final int FP_RES_FLASH_DEL_FAIL 			= 1034;
    public static final int FP_RES_FLASH_READ_OK 			= 1035;
    public static final int FP_RES_FLASH_READ_FAIL 			= 1036;
    public static final int FP_RES_FLASH_DATA_NOT_FOUND 	= 1037;
    public static final int FP_RES_INVALID_PASSWORD 		= 1038;
    public static final int FP_RES_KEY_LIST 				= 1039;
    public static final int FP_RES_ENROLL_FEATURE_BLOB 		= 1040;
    public static final int FP_RES_VERIFY_FEATURE_BLOB 		= 1041;
    public static final int FP_RES_SYSTEM_INFO 				= 1042;
    public static final int FP_RES_NEED_AUTHORIZED 			= 1044;
    public static final int FP_RES_INVALID_PARAMETER 		= 1045;
    public static final int FP_RES_SYSTEM_INFO_NOT_EXISTED	= 1059;
    public static final int FP_RES_FLASH_RESET_OK			= 1060;
    public static final int FP_RES_FLASH_RESET_FAIL			= 1061;
    public static final int FP_RES_VOLTAGE					= 1068;
    public static final int FP_RES_NO_BATTERY				= 1069;
    public static final int FP_RES_POWEROFF					= 1070;
    public static final int FP_RES_OUT_OF_MEMORY			= 1071;
    public static final int FP_RES_NAVIGATION				= 1072;
    public static final int FP_RES_FINGER_DETECTED          = 1073;
    public static final int FP_RES_FINGER_REMOVED           = 1074;
    public static final int FP_RES_FINGER_WAIT_FPON         = 1075;
    public static final int FP_RES_VERIFY_LEARNING			= 1076;
    public static final int FP_RES_LEARNING_FAIL			= 1077;
    public static final int FP_RES_THREAD_IMG               = 1078;
    public static final int FP_RES_SENSOR_OPEN              = 1079;
    public static final int FP_RES_ENROLL_MAP_PROGRESS      = 1080;
    public static final int FP_RES_MATCHED_IMG 				= 1081;
    public static final int FP_RES_PARTIAL_IMG      		= 1082;
    public static final int FP_RES_WET_IMG      			= 1084;
    public static final int FP_RES_WATER_IMG      			= 1085;
    public static final int FP_RES_FAST_IMG      			= 1086;
    public static final int FP_RES_TEST_ENROLL_OK  			= 1087;
    public static final int FP_RES_TEST_ENROLL_FAIL  		= 1088;
    public static final int FP_RES_TEST_MATCHED_OK          = 1089;
    public static final int FP_RES_TEST_MATCHED_FAIL  		= 1090;

    public static final int BLOB_TYPE						= 1100;
    public static final int BLOB_TYPE_IMAGE					= 1101;
	public static final int BLOB_TYPE_ENROLL_FEATURE		= 1102;
	public static final int BLOB_TYPE_VERIFY_FEATURE		= 1103;
	public static final int BLOB_TYPE_FLASH_DATA			= 1104;
	public static final int BLOB_TYPE_AES_ENCRYPTED_DATA	= 1105;
	public static final int BLOB_TYPE_AES_DECRYPTED_DATA	= 1106;
	public static final int BLOB_TYPE_RSA_ENCRYPTED_DATA	= 1107;
	public static final int BLOB_TYPE_RSA_DECRYPTED_DATA	= 1108;
	public static final int BLOB_TYPE_SIGNATURE_DATA		= 1109;
	public static final int BLOB_TYPE_RSA_PUBLIC_KEY		= 1120;
	public static final int BLOB_TYPE_SYS_INFO				= 1121;
    
	public static final int TINY_STATUS						= 1200;
	public static final int TINY_STATUS_ENROLL_MAP 			= TINY_STATUS + 34;
	public static final int TINY_STATUS_SELECT_CANDIDATE 	= TINY_STATUS + 35;
	public static final int TINY_STATUS_DELETED_CANDIDATE 	= TINY_STATUS + 36;
	public static final int TINY_STATUS_DUPLICATED_CANDIDATE = TINY_STATUS + 37;
	public static final int TINY_STATUS_REMOVE_CANDIDATE 	= TINY_STATUS + 38;
	public static final int TINY_STATUS_MOVE_CANDIDATE 		= TINY_STATUS + 39;
	public static final int TINY_STATUS_SELECT_IMAGE 		= TINY_STATUS + 40;
	public static final int TINY_STATUS_ADD_CANDIDATE 		= TINY_STATUS + 41;
	public static final int TINY_STATUS_BEFORE_GENERALIZE 	= TINY_STATUS + 42;
	public static final int TINY_STATUS_AFTER_GENERALIZE    = TINY_STATUS + 43;
	public static final int TINY_STATUS_HIGHLY_SIMILAR		= TINY_STATUS + 45;
	
    public static final int NONE_DEVICE = -1001;
    public static final int YUKEY_DEVICE = 3000;
    public static final int SENSOR_DEVICE = 3001;
    public static final int YUKEY_L_DEVICE = 3002;
    public static final int FPOTG = 3003;
    public static final int FPSPI = 3004;
    public static final int FPUSB = 3005;

    public static final int DEV_STATE_CHANGE				= 2000;
    public static final int DEV_STATE_DISCONNECTED 			= 2001;
    public static final int DEV_STATE_CONNECTING 			= 2002;
    public static final int DEV_STATE_CONNECTED 			= 2003;
    public static final int DEV_EXTRA_PERMISSION_GRANTED 	= 2004;
    public static final int DEV_ACTION_USB_DEVICE_ATTACHED 	= 2005;
    public static final int DEV_ACTION_USB_DEVICE_DEATTACHED 	= 2006;
    public static final int DEV_STATE_NOT_FOUND				= -2000;
    
    public static final int FP_STATE_START_OPERATION		= 3000;
    public static final int FP_STATE_END_OPERATION		= 3001;
    
    public static final int FP_RES_NO_PERMISSION			= 9999;
    
    //lastErrCode
    public static final int ERR_CODE_OK = 0;
    public static final int ERR_CODE_UNKNOW_ERR = 3100;
    public static final int ERR_CODE_INVALID_DATA = 3000;
    public static final int ERR_CODE_FILESYSTEM_OPERATION_ERR = 3001;
    public static final int ERR_CODE_NO_PWD_DATA = 3002;
    public static final int ERR_CODE_INCORRECT_PWD = 3003;
    public static final int ERR_CODE_NO_PERMISSION = 3004;
    public static final int ERR_CODE_ENCRYPT_FAIL = 3005;
    public static final int ERR_CODE_DB_DAMAGED = 3006;
    public static final int ERR_CODE_INTERNAL_ERROR = 3007;
    
    public static final int DB_ERR_FEATURE_LEN_ZERO     = 2010;
    public static final int DB_ERR_FEATURE_NULL     	= 2011;
    public static final int DB_ERR_ID_LEN_ZERO			= 2012;
    public static final int DB_ERR_ID_NULL      		= 2013;
    public static final int DB_ERR_ID_NOT_FOUND	        = 2014;
    public static final int DB_ERR_IOEXCEPTION			= 2015;
    public static final int DB_ERR_DATA_LEN_ZERO		= 2016;
    public static final int DB_ERR_DATA_NULL			= 2017;
    public static final int DB_ERR_LOAD_DATA_FAIL		= 2018;
    public static final int DB_ERR_OUT_DATA_NULL		= 2019;
    public static final int DB_ERR_IN_DATA_NULL			= 2020;
    public static final int DB_ERR_FOS_NULL				= 2021;
    public static final int DB_ERR_FIS_NULL				= 2022;
    public static final int DB_ERR_FD_NULL				= 2023;
    
    public static final int SEVC_ERR_FILE_DB_NULL		= 2030;
    public static final int SEVC_ERR_ID_NOT_FOUND		= 2031;
    public static final int SEVC_ERR_FPDEV_NULL			= 2032;
    public static final int SEVC_ERR_HASH_PWD_NULL		= 2033;
    public static final int SEVC_ERR_CM_NULL			= 2034;
    public static final int SEVC_ERR_ID_NULL			= 2035;
    public static final int SEVC_ERR_VALUE_NULL			= 2036;
    public static final int SEVC_ERR_PWD_NULL			= 2037;
    public static final int SEVC_ERR_PWD_INCORRECT		= 2038;
    public static final int SEVC_ERR_DB_LEN_ZERO		= 2039;
    public static final int SEVC_ERR_DB_PATH_NULL		= 2040;
    public static final int SEVC_ERR_CHECK_DBSTATE_FAIL = 2041;
    public static final int SEVC_ERR_DELETE_FILE_FAIL   = 2042;
    public static final int SEVC_ERR_COPY_FILE_FAIL     = 2043;
    public static final int SEVC_ERR_FILE_NOT_EXIST     = 2044;
    public static final int SEVC_ERR_FEATURE_NULL       = 2045;
    public static final int SEVC_ERR_MATCH_FAIL         = 2046;
    public static final int SEVC_ERR_NO_PERMISSION		= 2047;
    public static final int SEVC_ERR_BAK_DB_NOT_FOUND		= 2048;
    public static final int SEVC_ERR_DB_NOT_FOUND		= 2049;
    public static final int SEVC_ERR_NEW_DB_NOT_FOUND		= 2050;
    public static final int SEVC_ERR_RENAME_DB_ERR		= 2051;
    public static final int SEVC_ERR_IOEXCEPTION		= 2052;
    
    public static final int NB_ERR_LOAD_MOUDLE_FAIL		= 2060;
    public static final int NB_ERR_UNKNOWN_TYPE			= 2061;
    public static final int NB_ERR_DO_GET_IMAGE_FAIL	= 2062;
    public static final int NB_ERR_FINGER_UTIL_NULL		= 2063;
    public static final int NB_ERR_USER_ID_NULL			= 2064;
    public static final int NB_ERR_USER_ID_LEN_ZERO		= 2065;
    public static final int NB_ERR_CONNECT_FAIL			= 2066;
    public static final int NB_ERR_MAP_NULL				= 2067;
    
    public static final int CM_ERR_EXREND_KEY_NULL		= 2080;
    public static final int CM_ERR_SET_KEY_NULL			= 2081;
    public static final int CM_ERR_SEC_KEY_NULL			= 2082;
    public static final int CM_ERR_ENCRYPTION_NULL		= 2083;
    public static final int CM_ERR_DECRYPTION_NULL		= 2084;
    public static final int CM_ERR_DATA_NULL			= 2085;
    public static final int CM_ERR_ENCRYPTION_DATA_NULL	= 2086;
    public static final int CM_ERR_DECRYPTION_DATA_NULL	= 2087;
    public static final int CM_ERR_INVALID_DATA_NULL	= 2088;
    
	/*
	 * 	Sensor Control
	 */
	public static final int PAUSE_ENROLL									= 2010;
	public static final int RESUME_ENRORLL									= 2011;
	public static final int SENSOR_TEST_NORMALSCAN_COMMAND					= 100103;
	public static final int SENSOR_TEST_SNR_ORG_COMMAND						= 2013;
	public static final int SENSOR_TEST_SNR_FINAL_COMMAND					= 2014;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_START			= 0x11000003;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_SCRIPT_END			= 0x11000004;
	public static final int FACTORY_TEST_EVT_SNSR_TEST_PUT					= 0x11000005;
	public static final int EVT_ERROR										= 2020;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_START				= 2031;
	public static final int FACTORY_WRITE_EEPROM_SCRIPT_END					= 2032;
	
	//EEPROM status
	public static final int EEPROM_STATUS_OPERATION_END						= 101;
}
