package egis.finger.host;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;
import egis.client.api.FpResDef;
import egis.finger.host.CipherManager.CipherType;

class FingerData implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String mID;
	byte[] mFeature;
	
	FingerData(String id, byte[] feature){
		this.mID = id;
		if(feature != null)
			mFeature=feature.clone();
	}
	
	public String getid(){
		return mID;
	}  
	public byte[] getfeature(){
		return mFeature;
	}
	public void setfeature(byte[] feature){
		mFeature = feature.clone();
	}
	public boolean equals(Object obj){
		return ((FingerData)(obj)).mID.equals(this.mID);
	}

}

public class FileDB{
	private static final String TAG = "FpCsaClientLib_FileDB";
	private static final byte[] key = {0,1,2,3,4,5,6,7,8,9,0xa,0xb,0xc,0xd,0xe,0xf};
	private static final byte[] iv = {0,1,2,3,4,5,6,7,8,9,0xa,0xb,0xc,0xd,0xe,0xf};
	
	List<FingerData> mList;
	CipherManager cm;
	
	public FileDB() {
		mList = new ArrayList<FingerData>();
		cm = new CipherManager(CipherType.AES);
        cm.init(key, iv);
	}
	public boolean add(String id, String feature){
		if(id == null){
			Log.e(TAG, "id == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_NULL;
			return false;
		}
		if(feature == null){
			Log.e(TAG, "feature == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FEATURE_NULL;
			return false;
		}
		if(id.length() <= 0){
			Log.e(TAG, "id.length <= 0");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_LEN_ZERO;
			return false;
		}
		if(feature.length() <= 0){
			Log.e(TAG, "feature.length <= 0");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FEATURE_LEN_ZERO;
			return false;
		}
		return add(id, feature.getBytes());
	}
	  
	public boolean add(String id, byte[] feature){
		if(feature == null){
			Log.e(TAG, "feature == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FEATURE_NULL;
			return false;
		}
		if(feature.length <= 0){
			Log.e(TAG, "feature.length <= 0");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FEATURE_LEN_ZERO;
			return false;
		}

		int mRepIndex;
		
		FingerData mFD = new FingerData(id, feature);
		/*if (mFD == null){
			Log.e(TAG, "FingerData is null");
			FPAuthService.lastErrCode = FpResDef.DB_ERR_FD_NULL;
			return false;
		}*/
		if((mRepIndex = mList.indexOf(mFD)) == -1 )
			mList.add(mFD);
		else
			mList.get(mRepIndex).setfeature(feature);
		
		return true;
	}
	
	public boolean delete(String id){
		if(id == null){
			Log.e(TAG, "delete id == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_NULL;
			return false;
		}
		if(id.length() <= 0){
			Log.e(TAG, "delete id length invalid");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_LEN_ZERO;
			return false;
		}
		
		int mDelIndex;
		
		if (id.equals(new String("*")))
		{
			mList.clear();
			return true;
		}
		
		if( (mDelIndex = mList.indexOf(new FingerData(id, null))) == -1 ){
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_NOT_FOUND;
			return false;
		}
		
		mList.remove(mDelIndex);
		
		return true;
	}
	
	public boolean update(String id, byte[] feature){
		if(id == null){
			Log.e(TAG, "id == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_NULL;
			return false;
		}
		if(feature == null){
			Log.e(TAG, "feature == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FEATURE_NULL;
			return false;
		}
		if(id.length() <= 0){
			Log.e(TAG, "id.length <= 0");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_LEN_ZERO;
			return false;
		}
		if(feature.length <= 0){
			Log.e(TAG, "feature.length <= 0");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FEATURE_LEN_ZERO;
			return false;
		}
		
		int mUpdateIndex ;
		
		if( (mUpdateIndex = mList.indexOf(new FingerData(id, null))) == -1 ){
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_ID_NOT_FOUND;
			return false;
		}

		mList.get(mUpdateIndex).setfeature(feature);
		
		return true;
	}
	
	private byte[] objtoByte(Object obj){
		if(obj == null){
			Log.e(TAG, "objtoByte obj == null");
			return null;
		}
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutput out = null;
		try {
			out = new ObjectOutputStream(bos);  
			Log.e(TAG, "+++ before out.writeObject(obj) +++");
			out.writeObject(obj);
			Log.e(TAG, "+++ after out.writeObject(obj) +++");
			Log.e(TAG, "objtoByte obj == null");
			byte[] bytes = bos.toByteArray();
			return bytes;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				bos.close();
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return null;
	}
	private Object bytetoObj(byte[] data){
		if(data == null){
			Log.e(TAG, "delete data == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_DATA_NULL;
			return false;
		}
		if(data.length <= 0){
			Log.e(TAG, "delete data length invalid");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_DATA_LEN_ZERO;
			return false;
		}
		ByteArrayInputStream bis = new ByteArrayInputStream(data);
		ObjectInput in = null;
		try {
			in = new ObjectInputStream(bis);
			Object o = in.readObject(); 
			return o;
		} catch (StreamCorruptedException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				bis.close();
			} catch (IOException ex) {
				// ignore close exception
			}
			try {
				if (in != null) {
					in.close();
				}
			} catch (IOException ex) {
				// ignore close exception
			}
		}
		return null;
	}
	public boolean save(FileOutputStream fos){	
		Log.d(TAG, "save start");
		if(fos == null){
			Log.e(TAG, "save file stream == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FOS_NULL;
			return false;
		}
		BufferedOutputStream oos = null;
		try{
			oos = new BufferedOutputStream(fos);
			Log.d(TAG, "mList.size = " + mList.size());
			byte[] data = objtoByte(mList);
			if(data == null){
				Log.e(TAG, "save data == null");
				FPNativeBase.lastErrCode = FpResDef.DB_ERR_DATA_NULL;
				return false;
			}
			Log.d(TAG, "save data.length = " + data.length);
			byte[] outData = cm.encryption(data);
			if(outData == null){
				Log.e(TAG, "save outData ==  null");
				FPNativeBase.lastErrCode = FpResDef.DB_ERR_OUT_DATA_NULL;
				return false;
			}
			Log.d(TAG, "save outData.length = " + outData.length);
			oos.write(outData);
			oos.flush();
			FileDescriptor fd = fos.getFD();
			fd.sync();
			oos.close();
			fos.close();
			Log.d(TAG, "save success");
			return true;
		}catch(IOException e){
			e.printStackTrace();
		}finally{
			try {
				oos.close();
				fos.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			//return false;
		}
		Log.d(TAG, "save fail");
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public boolean Load(FileInputStream fis){
		Log.d(TAG, "Load start");
		if(fis == null){
			Log.e(TAG, "Load file stream == null");
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_FIS_NULL;
			return false;
		}
		int  mFisSize;
		try{
			mFisSize = fis.available();
			Log.d(TAG, "Load mFisSize = " + mFisSize);
		}catch(IOException e){
			e.printStackTrace();
			FPNativeBase.lastErrCode = FpResDef.DB_ERR_IOEXCEPTION;
			return false;
		}		
		
		BufferedInputStream ois = null;
		if(mFisSize!=0){	
			try{
				ois = new BufferedInputStream(fis);
				Log.d(TAG, "Load ois.available() = " + ois.available());
				byte[] data = new byte[ois.available()];
				ois.read(data);
				/*if(data == null){
					Log.e(TAG, "FileDB load file fail, data is null");
					FPAuthService.lastErrCode = FpResDef.DB_ERR_LOAD_DATA_FAIL;
 					ois.close();
					fis.close();
					return false;
				}*/
				byte[] inData = cm.decryption(data);
				if(inData == null){
					Log.e(TAG, "Load inData == null");
					FPNativeBase.lastErrCode = FpResDef.DB_ERR_IN_DATA_NULL;
					return false;
				}
				Log.d(TAG, "Load inData.length = " + inData.length);
				mList = (ArrayList<FingerData>) bytetoObj(inData);
				ois.close();
				fis.close();
				Log.d(TAG, "Load succuess");
				return true;
			}catch(IOException e){
				e.printStackTrace();
			}finally{
				try {
					ois.close();
					fis.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		}
		Log.d(TAG, "Load fail");
		return false;
	}
	
	public int size(){
		Log.d(TAG, "mFileDB.mList.size()");
		return mList.size();
	}
	
	public byte[] readFeature(int index){
		return ((FingerData)mList.get(index)).mFeature;
	}

	public byte[] readFeature(String fid){
	  for(FingerData data : mList){		  
		if(data.mID.equals(fid)){			
		  return data.mFeature;				
		}
	  }
	  return null;
	}
	
	public String readID(int index){
		return ((FingerData)mList.get(index)).mID;
	}
	
	public int readID(String fid){
		int idx=-1;
		for(FingerData f : mList){
			idx++;
			if(f.getid().equals(fid)){
				return idx;
			}			
		}
		return idx;
	}
	
	public void clear(){
		mList.clear();
	}
}
