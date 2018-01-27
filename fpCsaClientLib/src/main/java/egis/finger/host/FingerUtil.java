package egis.finger.host;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import egis.client.api.FpResDef;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class FingerUtil{
	protected static Handler mApHandler;
	protected static final String TAG = "FpCsaClientLib_FingerUtil";
	public byte[] mFeature = new byte[1024];
	public int mFeatureSize = 0;
	private FileDB mFileDB = null;
	private static Context mContext;
	public static int[] fileDBMap;
	
    private static final String FEATURE_FILE_NEW = "Enroll_Data_New";
    private static final String FEATURE_FILE = "Enroll_Data";
    private static final String FEATURE_FILE_BAK = "Enroll_Data_Bak";
    private static final String FEATURE_FILE_DEFAULT_ID = "Egistec_Company_Internal_Default_Data_1_id";
    private static final String FEATURE_FILE_DEFAULT_VAULE = "Egistec_Company_Internal_Default_Data_1_value";
	
	public FingerUtil(Handler handle, Context context)
	{
		mApHandler = handle;
		mContext = context;
		
		mFileDB = new FileDB();
		if(!refreshList()){
    		Log.e(TAG, "onCreate load data from data fail, retry to use bak file");
    		if(!recoveryDB()){
    			Log.e(TAG, "onCreate recoveryDB fail");
    			if(!reInitDB()){ 
    				Log.e(TAG, "reInitDB fail");
    			}
    		}else{
    			Log.d(TAG, "Try to refresh");
    			if(!refreshList()){
    				Log.e(TAG, "onCreate still can not load data from DB");
    			}
    		}
    	}
	}
	
	public static void postFpResultStatus(int status) {
		Log.d(TAG, "postFpResultStatus status = " + status);
	    mApHandler.obtainMessage(FpResDef.FP_RESULT, status, -1).sendToTarget();    	
	}
	
	//database
	public boolean enrollToDB(String userID) {
		Log.d(TAG, "enrollToDB userID="+userID);
		byte[] feature = mFeature.clone();

		if (mFileDB == null) {
		    Log.e(TAG,"enrollToDB FileDB==null" );
		    FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_DB_NULL;
		    return false;
		}
		
		if(mFeature == null){
			Log.e(TAG,"mFeature == null" );
		    FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_CM_NULL;
		    return false;
		}
		
		if (!mFileDB.add(userID, feature)) {
			Log.e(TAG, "enrollToDB(): mFileDB.add() fail");
			postFpResultStatus(FpResDef.FP_RES_ENROLL_FAIL);
		    return false;
		}
		if (!saveFeatureToFile()) {
			Log.e(TAG, "enrollToDB(): saveFeatureToFile() fail");
			postFpResultStatus(FpResDef.FP_RES_ENROLL_FAIL);
		    return false;
		}
		return true;
	}
	
	private boolean saveFeatureToFile(){
		Log.d(TAG, "svaeFeatureToFile");
		if(saveFeatureToFile(FEATURE_FILE_NEW)){
			if(replaceDB()){
				Log.d(TAG, "svaeFeatureToFile complete");
				return true;
			}
			Log.e(TAG, "svaeFeatureToFile replaceDB fail");
		}
		Log.e(TAG, "saveFeatureToFile fail, retry");
		if(saveFeatureToFile(FEATURE_FILE_NEW)){
			if(replaceDB()){
				Log.d(TAG, "svaeFeatureToFile complete");
				return true;
			}
			Log.e(TAG, "svaeFeatureToFile retry then replaceDB fail");
		}
		Log.e(TAG, "saveFeatureToFile still fail");
		return false;
	}
	
	private boolean saveFeatureToFile(String fileName)
    {
    	checkFileLen(FEATURE_FILE);
    	
    	FileOutputStream fos = null;
    	try {
    		fos= mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
        	if (!mFileDB.save(fos)){
        		Log.e(TAG, "saveFeature() mFileDB.Save() fail");
        		return false;
        	}
        	Log.d(TAG, "svaeFeatureToFile success");
        	return true;
    	} catch (FileNotFoundException e) {
    		Log.e(TAG, "saveFeature() exception="+e.getMessage());
    		FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_NOT_EXIST;
    		return false;
    	} finally {
    		try {
				if (fos != null) {
					fos.close();
				}
			} catch (IOException e) {
				Log.e(TAG, "saveFeature() fos.close() exception="+e.getMessage());;
			}
    		Log.d(TAG, "svaeFeatureToFile end");
    	}
    	
    }
	
	private int checkFileLen(String fileName){
		Log.d(TAG, "checkFileLen fileName = " + fileName);
		int len=0;
		FileInputStream fis=null;
		BufferedInputStream ois=null;
        try {
        	fis = mContext.openFileInput(fileName);
           	if (fis != null) {
           		Log.d(TAG, "checkDBLen fislen = " + fis.available());
           		ois = new BufferedInputStream(fis);
           		Log.d(TAG, "checkDBLen oislen = " + ois.available());
           		len = ois.available();
           	} 
        } catch (FileNotFoundException e) {
        	Log.e(TAG, "checkDBLen(): FileNotFoundException:"+e.getMessage());
        	e.printStackTrace();
        } catch (IOException e) {
			Log.e(TAG, "checkDBLen(): IOException:"+e.getMessage());
			e.printStackTrace();
		} finally{
			try {
				if(fis != null)
					fis.close();
				if(ois != null)
					ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
        Log.d(TAG, "checkFileLen len = " + len);
        return len;
	}
	
	private boolean replaceDB(){
		Log.d(TAG, "replaceDB");
		if(!checkFileState(FEATURE_FILE_NEW)){ 
			Log.e(TAG, "replaceDB checkFileState fail " + FEATURE_FILE_NEW);
			return false;
		}
		File file_new = new File(getDBPath() + "/" + FEATURE_FILE_NEW);
		File file = new File(mContext.getFilesDir() + "/" + FEATURE_FILE);
		File file_bak = new File(getDBPath() + "/" + FEATURE_FILE_BAK);
		if(file_bak.exists()){
			if(!file_bak.delete()){
				Log.e(TAG, "replaceDB delete file fail " + file_bak.getPath());
				FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_NOT_EXIST;
				//return false;
			}
		}
		if(!file.renameTo(file_bak)){
			Log.e(TAG, "replaceDB rename file fail " + file.getPath() + " to " + file_bak.getPath());
		}
		//file = new File(getFilesDir() + "/" + FEATURE_FILE);
		if(file.exists()){
			if(!file.delete()){
				Log.e(TAG, "replaceDB delete file fail " + file.getPath());
				FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_NOT_EXIST;
				//return false;
			}
		}
		if(file_new.renameTo(file)){
			Log.d(TAG, "replaceDB complete");
			return true;
		}else{
			Log.e(TAG, "replaceDB rename file fail " + file_new.getPath() + " to " + file.getPath());
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_RENAME_DB_ERR;
			//return false;
		}
			
			//return false;
		return false; 
	}
	
	private boolean checkFileState(String fileName){
		Log.d(TAG, "checkFileState fileName = " + fileName);
		int len = checkFileLen(fileName);
		if(len == 0){
			Log.e(TAG, "   checkDBLen len == 0");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_DB_LEN_ZERO;
			return false;
		}
		String path = getDBPath();
		String value = null;
		if(path == null){
			Log.e(TAG, "checkDBState path == null");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_DB_PATH_NULL;
			return false;
		}
		if((value = dataRead(FEATURE_FILE_DEFAULT_ID)) == null){
			Log.e(TAG, "checkDBState dataRead error");
			return false;
		}
		if(value.equals(FEATURE_FILE_DEFAULT_VAULE)){
			Log.d(TAG, "checkDBState complete");
			return true;
		}
		Log.e(TAG, "checkDBState fail");
		FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_CHECK_DBSTATE_FAIL;
		return false;
	}
	
	private String getDBPath(){
		if(mContext.getFilesDir()!=null)
			Log.d(TAG, "getDBPath()="+mContext.getFilesDir().getAbsolutePath());
		else
			Log.e(TAG, "getDBPath()=null");
		
		return mContext.getFilesDir()!=null?mContext.getFilesDir().getAbsolutePath():null;
	}
	
	public String dataRead(String id){
		Log.d(TAG, "dataRead(): start, id=" + id);
		if(mFileDB == null){
			Log.e(TAG, "dataRead(): FileDB is null");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_DB_NULL;
			return null;
		}
		
		int idx = -1;
		try{
			for(int i=0; i<mFileDB.size(); i++){
				if(mFileDB.readID(i).compareTo(id) == 0){
					idx = i;
					break;
				}
			}
		}
		catch(Exception e){
			Log.d(TAG, "search data from db file error");
			return null;
		}
		
		if (idx != -1) {
			//return new String(mFileDB.readFeature(idx), 0, mFileDB.readFeature(idx).length);
			String data = new String(mFileDB.readFeature(idx), 0, mFileDB.readFeature(idx).length);
			Log.d(TAG, "data = " + data);
			return data;
		}
		else{
			Log.w(TAG, "data not found");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_ID_NOT_FOUND;
			return null;
		}
	}

	public boolean checkId(String id){
		Log.d(TAG, "checkId(): start, id=" + id);
		if(mFileDB == null){
			Log.e(TAG, "checkId(): FileDB is null");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_DB_NULL;
			return false;
		}
		
		int idx = -1, idLen;
		try{
			for(int i=0; i<mFileDB.size(); i++){
				String temp;
				idLen = -1;
				temp = mFileDB.readID(i);
				idLen = temp.indexOf(';');
				if(idLen == -1) continue;
				String str = temp.substring(0,idLen);
				String str2 = id.substring(0,id.indexOf(";"));
				if(str.compareTo(str2) == 0){
					idx = i;
					break;
				}
			}
		}
		catch(Exception e){
			Log.d(TAG, "checkId search data from db file error");
			return false;
		}
		
		if (idx != -1) {
			
			return true;
		}
		else{
			Log.w(TAG, "data not found");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_ID_NOT_FOUND;
			return false;
		}
	}
	private boolean refreshList(){
		Log.d(TAG, "refreshList");
		if(!replaceNewDB()){ ;
			Log.e(TAG, "refreshList replaceNewDB fail");
			return false;
		}
		FileInputStream fis=null;
        try {
        	fis = mContext.openFileInput(FEATURE_FILE);
           	if (fis != null) { 
           		mFileDB.Load(fis);
           		Log.d(TAG,"refreshList mFileDB.Load(fis)");
           		fis.close();
           	} 
           	Log.d(TAG, "refreshList load DB complete");
           	return true;
        } catch (FileNotFoundException e) {
        	Log.e(TAG, "refreshList(): FileNotFoundException:"+e.getMessage());
        	e.printStackTrace();
        	//mFileDB.add(FEATURE_FILE_DEFAULT_ID, FEATURE_FILE_DEFAULT_VAULE);
        	if(recoveryDB()){
        		Log.d(TAG, " refreshList() recoveryDB  complete");
        		return true;
        	}
        	else
        		if(dataSet(FEATURE_FILE_DEFAULT_ID, FEATURE_FILE_DEFAULT_VAULE) ){
        			Log.d(TAG,"refreshList init default data complete");
        			return true;
        		}
        	Log.e(TAG, "refreshList file not found, fail to recovertDB and dataSet");
        	//return false;
        } catch (IOException e) {
			Log.e(TAG, "refreshList(): IOException:"+e.getMessage());
			e.printStackTrace();
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_IOEXCEPTION;
			return false;
		} finally{
			try {
				if(fis != null)
					fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(!checkFileState(FEATURE_FILE)){
				File file = new File(FEATURE_FILE);
				if(file.exists()){
					if(file.delete()){
						Log.e(TAG, "refreshList delete file fail " + file.getPath());
						//return fasle;
					}
				}
				Log.e(TAG, "refreshList checkDBState fail, delete db and try to recovery");
				if(!recoveryDB()){
					Log.e(TAG, "refreshList recoveryDB fail");
					String path = getDBPath();
					File file2 = new File(path+"/"+FEATURE_FILE);
					if(file2.exists()){
						if(!file2.delete()){
							Log.e(TAG, "refreshList delete file error");
							//return false;
						}
					}
					//return false;
					mFileDB.clear();
					Log.e(TAG, "DB is cleared");
					if(!dataSet(FEATURE_FILE_DEFAULT_ID, FEATURE_FILE_DEFAULT_VAULE) ){
	        			Log.e(TAG," Set default data fail");
	        			return false;
	        		}
				}else{
					FileInputStream fis2=null;
					try {
						fis2 = mContext.openFileInput(FEATURE_FILE);
						if (fis2 != null) { 
			           		mFileDB.Load(fis2);
			           		Log.d(TAG,"refreshList mFileDB.Load(fis)");
			           		fis2.close();
			           	} 
					} catch (FileNotFoundException e) {
						e.printStackTrace();
						Log.e(TAG, "refreshList retry to load DB fail");
						FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_FILE_NOT_EXIST;
						return false;
					} catch (IOException e) {
						e.printStackTrace();
						Log.e(TAG, "refreshList retry to load DB fail");
						FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_IOEXCEPTION;
						return false;
					} finally{
						if(fis2 != null){
							try {
								fis2.close();
							} catch (IOException e) {
								e.printStackTrace();
								Log.e(TAG, "refreshList retry close DB fail");
								FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_IOEXCEPTION;
								return false;
							}
						}
						if(!checkFileState(FEATURE_FILE)){
							Log.e(TAG, "refresshList bak file does not work, init all data");
							if(!reInitDB()){ 
			    				Log.e(TAG, "refreshList reInitDB fail");
			    				//this.stopSelf();
			    				return false;
			    			}
						}
					}
		           	
				}
				
			}
			if(saveFeatureToBakFile()){
				Log.e(TAG, "refreshList saveFeatureToBakFile complete");
				return true;
			}
			Log.e(TAG, "refreshList saveFeatureToBakFile fail");
		}
        Log.e(TAG, "refreshList fail");
        return false;
	}
	
	private boolean recoveryDB(){
		Log.d(TAG, "recoveryDB");
		File file = new File(getDBPath() + "/" + FEATURE_FILE);
		File file_bak = new File(getDBPath() + "/" + FEATURE_FILE_BAK);
		if(!file_bak.exists()){
			Log.e(TAG, "Backup DB file does not exist");
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_BAK_DB_NOT_FOUND;
			return false;
		}
		
		if(file_bak.renameTo(file)){ 
			//if(!checkFileState(FEATURE_FILE)){ 
			//	Log.e(TAG, "replaceDB checkFileState fail " + FEATURE_FILE);
			//	return false;
			//}
			Log.d(TAG, "recoveryDB complete");
			return true;
		}else{
			Log.e(TAG, "rename file faile " + file.getPath() + " to " + file_bak.getPath());
			FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_RENAME_DB_ERR;
			//return false;
		}
		return false; 
	}
	
	private boolean reInitDB(){
		Log.d(TAG, "reInitDB");
		File file_new = new File(getDBPath() + "/" + FEATURE_FILE_NEW);
		File file = new File(mContext.getFilesDir() + "/" + FEATURE_FILE);
		File file_bak = new File(getDBPath() + "/" + FEATURE_FILE_BAK);
		file_new.delete();
		file.delete();
		file_bak.delete();
		if(dataSet(FEATURE_FILE_DEFAULT_ID, FEATURE_FILE_DEFAULT_VAULE) ){
			Log.d(TAG," onCreate init default data complete");
			return true;
		}
		Log.d(TAG, "reInitDB fail");
		return false;
	}
	
	private boolean replaceNewDB(){
		Log.d(TAG, "replaceNewDB");
		File file_new = new File(getDBPath() + "/" + FEATURE_FILE_NEW);
		if(file_new.exists()){
			Log.e(TAG, "replaceNewDB DB new file exists, replace current DB");
			File file = new File(getDBPath() + "/" + FEATURE_FILE);
			if(file.exists()){
				if(!file.delete()){
					Log.e(TAG, "replaceNewDB delete file fail " + file.getPath());
					//return false;
				}
			}
			if(!file_new.renameTo(file)){
				Log.e(TAG, "replaceNewDB rename fail " + file_new.getPath() + " to " + file.getPath());
				FPNativeBase.lastErrCode = FpResDef.SEVC_ERR_RENAME_DB_ERR;
				return false;
			}
			Log.d(TAG, "replaceNewDB complete");
			return true;
			//return false;
		}else{
			Log.d(TAG, "replaceNewDB new file does not exist");
			return true;
		}
	}
	
	public boolean dataSet(String id, String value){
		Log.d(TAG, "dataSet id="+id+"value="+value);
		if (!mFileDB.add(id, value)) {
    		Log.e(TAG, "dataSet(): mFileDB.add() fail");
        	return false;
    	}
    	if (!saveFeatureToFile()) {
    		Log.e(TAG, "dataSet(): saveFeatureToFile() fail");
        	return false;
    	}
		return true;
	}
	
	private boolean saveFeatureToBakFile(){
		Log.d(TAG, "svaeFeatureToBakFile");
		if(saveFeatureToFile(FEATURE_FILE_BAK)){
			Log.d(TAG, "svaeFeatureToBakFile complete");
			return true;
		}
		Log.e(TAG, "saveFeatureToBakFile fail, retry");
		if(saveFeatureToFile(FEATURE_FILE_BAK)){
			Log.d(TAG, "svaeFeatureToBakFile complete");
			return true;
		}
		Log.e(TAG, "saveFeatureToBakFile still fail");
		/*if(recoveryDB()){
			if(refreshList()){
				return true;
			}
			Log.e(TAG, "svaeFeatureToFile refreshList fail");
		}
		Log.e(TAG, "svaeFeatureToFile recoveryDB fail");*/
		return false;
	}
	
	public byte[][] getAllFeature(String IdentifyUserId){
		int size=0;
		for(int i=0 ; i<mFileDB.size(); i++){
		  String fid = mFileDB.readID(i);
		  if (fid.startsWith("*") && fid.substring(1, fid.indexOf(";")).equals(IdentifyUserId)) {
			size++;  
		  }		  
		}
		int index=0;
		byte[][] allFeature = new byte[size][];
		fileDBMap = new int[size];
		for(int i=0 ; i<mFileDB.size(); i++){
		  String fid = mFileDB.readID(i);
		  if (fid.startsWith("*") && fid.substring(1, fid.indexOf(";")).equals(IdentifyUserId)) {
			Log.d(TAG, "getAllFeature() fid="+fid);
		    allFeature[index]= new byte[mFileDB.readFeature(i).length];
		    allFeature[index]=mFileDB.readFeature(i);			    
		    fileDBMap[index]=i;
		    //Log.d(TAG, "allFeature=" + index + " size=" + allFeature[index].length);
		    index++;
		  }
		}
		//Log.d(TAG, "allFeature" + Arrays.toString(allFeature[1]));
		return allFeature;
	}
	
	public String getMatchedID(int matchIdx)
	{
		Log.d(TAG, "+++++ getMatchedID matchIdx = " + matchIdx + " +++++");
		//return (mFileDB.readID(matchIdx)).substring(1);
		String data = (mFileDB.readID(matchIdx)).substring(1);
		Log.d(TAG, "data = " + data);
		return data;
	}
	
	public String getEnrollListFromDB(String userID) {
		Log.d(TAG, "getEnrollListFromDB="+userID);
		
    	String enrollList = null;
    	for(int i=0; i<mFileDB.size(); i++) {
    		String fid = mFileDB.readID(i);
    		Log.d(TAG, "fid="+fid);
    		if (!userID.equals(new String("*"))) { 
    			if(fid.startsWith("*")){
    				Log.d(TAG, "in startsWith() fid="+fid);
    				String dbUserId = fid.substring(0, fid.indexOf(";"));
    				Log.d(TAG, "dbUserId="+dbUserId);
    				if(userID.equals(dbUserId)){
    				  int idx = fid.length()-2;
    				  fid = fid.substring(idx);
    				}else
    				  continue;
    			} else 
    				continue;
    		}else { //get all enroll list
        		Log.d(TAG, "userID="+userID);
    			if (!fid.startsWith("*")) continue; //not finger;
    			fid = fid.substring(1); //remove "*"
    		}
    		if (enrollList == null)
    			enrollList = fid;
    		else
    			enrollList +=";"+fid;
    		
    		Log.d(TAG, "-------------------------");
    	}
    	Log.d(TAG, "enrollList="+enrollList);
    	return enrollList;
    }
	
	public String[] getUserIdList(){
		Log.d(TAG, "+++++ getUserIdList+++++");
		String userName = null;
		List<String> enrollList = new ArrayList<String>();
    	for(int i=0; i<mFileDB.size(); i++) {
    		String fid = mFileDB.readID(i);   // for example: *USER;L0
    		if (fid.startsWith("*")) {  // prefix with * means that it's User finger
    			if(fid.indexOf(";") == -1) continue;	    				
    			userName = fid.substring(1, fid.indexOf(";"));
    			if(enrollList.contains(userName))
    				continue;
    			enrollList.add(userName);
    		}
    	}
    	
    	if(enrollList.size()  == 0){
    		Log.d(TAG, "no user fingers in the fileDB");
    		return null;	
    	}    		
    	
		String[] s = new String[enrollList.size()];
		s = enrollList.toArray(s);
		Log.d(TAG, "s = " + s);
		return s;	
	}
	
	public boolean deleteFromDB(String userID) {
		Log.d(TAG, "+++++ deleteFromDB  userID = " + userID + " +++++");
    	if (!mFileDB.delete(userID)) {
    		Log.e(TAG, "deleteFromDB(): mFileDB.delete() fail");
    		return false;
    	}
    	if (!saveFeatureToFile()) {
    		Log.e(TAG, "deleteFromDB(): saveFeature() fail");
    		return false;
    	}
    	return true;
    }
	
	public boolean dataDelete(String id){
		if (!mFileDB.delete(id)) {
    		Log.e(TAG, "dataDelete(): mFileDB.delete() fail");
        	return false;
    	}
    	if (!saveFeatureToFile()) {
    		Log.e(TAG, "dataDelete(): saveFeatureToFile() fail");
        	return false;
    	}
		return true;
    }
}
