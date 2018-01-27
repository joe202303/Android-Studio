package egistec.csa.client.api;  

import egistec.csa.client.api.IFPAuthServiceCallback;

interface IFPAuthService {
	
	//IFPSystem
	boolean enroll(String userID);
	boolean deleteFeature(String userID);
	String getEnrollList(String userId);
	boolean setPwd(String pwd);
	boolean deletePwd();
	boolean isRequestVerify();
	byte[] getTinyMap();
	int[] getTinyMapInfo();
	
	//IFPAuth
    void registerCallback(IFPAuthServiceCallback cb);
    void unregisterCallback(IFPAuthServiceCallback cb);
    
    boolean connectDevice();
    boolean disconnectDevice();
    boolean identify(String userId);  
    boolean learningIdentify(String fid);  
    boolean abort();
    int getDeviceType();
    boolean isEnrolled();
    boolean isSimplePwd();
    boolean verifyPwd(String pwd);
	boolean DataSet(String key, String value, String pwd);
	String DataRead(String key, String pwd);
	boolean DataDelete(String key, String pwd);   
    String getMatchedUserID();
    void setOnGetRawData(boolean enable);
    boolean captureRawData();
    boolean captureFrame();
    byte[] getRawData();
	int[] getRawDataInfo();
	int getLastErrCode();
	int getVerifyLearningScore();
	boolean learning();
	byte[] getThreadImg();
	int[] getThreadImgInfo();
	int getEnrollProgress();
	
	//for samsung
	String getVersion();
	
	String getSensorInfo();	
	String[] getUserIdList();
	//int getEnrollRepeatCount();
	int[] getEnrollStatus();
	int setAccuracyLevel(int level);
	boolean IsSensorWorking();
	int getOperationType();
	byte[] getMatchedImg();
	int[] getMatchedImgInfo();
	void checkServiceException();
	int sensorControl(int request);
}
