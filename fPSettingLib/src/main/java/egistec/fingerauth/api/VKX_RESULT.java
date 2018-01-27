package egistec.fingerauth.api;

public class VKX_RESULT {
	public static final int FAIL = -1;
	public static final int OK = 0;
	public static final int SUCCESS = 0;
	public static final int MATCHED = SUCCESS;
	public static final int NOT_MATCHED = 1;

	public static final int NOT_CONNECTED = -6;
	public static final int CALIBRATION_FAIL = -7;

	public static final int ADD_DUPLICATE = -100;
	public static final int DUPLICATE_FEATURE = -101;
	public static final int OUT_OF_RESOURCE = -104;
	public static final int DECRYPT_ERROR = -106;
	public static final int LIMITED = -109;
	public static final int INVALID_DATA_VERSION = -116;
	public static final int INVALID_TEMPLATE_SIZE = -117;
	public static final int INVALID_KEY_INDEX = -118;
	public static final int ENROLL_FAIL = -1001;
	public static final int NO_SSL_PRIVATE_KEY = -2000;
	public static final int FLASH_READ_FAIL = -2001;
	public static final int FLASH_WRITE_FAIL = -2002;
	public static final int FLASH_ERASE_FAIL = -2003;
	public static final int FLASH_DATA_NOT_FOUND = -2004;
	public static final int FLASH_FULL = -2005;
	public static final int DATA_SIZE_TOO_LARGE = -2006;
	public static final int INVALID_PARAMETER = -2010;
	
	public static final int ERR_NO_DEVICE_HANDLE = -2101;
	public static final int ERR_REQUIRE_USB_PREMISSION = -2102;
	public static final int ERR_NO_USB_PREMISSION = -2103;
	public static final int ERR_GET_CONNECTION_HANDLE_FAIL = -2104;
	public static final int ERR_NOT_FOUND_DEVICE = -2105;

}
