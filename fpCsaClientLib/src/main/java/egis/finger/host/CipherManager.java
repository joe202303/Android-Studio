package egis.finger.host;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import android.util.Base64;
import android.util.Log;
import egis.client.api.FpResDef;

public class CipherManager {
	
	private static final String TAG = "FpCsaClientLib_CipherManager";
	private SecretKeySpec mSecKey;
	private AlgorithmParameterSpec mSpec;
	private String mN;
	private String mE;
	private String mD;
	
	private static final String PREFIX = "yu";
	
	public enum CipherType {AES, RSA};		
	private CipherType cipherMode;
	
	public CipherManager(CipherType cipherMode){
		this.cipherMode = cipherMode;		
	}
	
	public void init(byte[] keys, byte[] iv){
		if(cipherMode != CipherType.AES){
			return;
		}			
		mSecKey = new SecretKeySpec(keys, "AES");
		mSpec = new IvParameterSpec(iv);
	}
	
	public void init(String mod, String pub, String sec){
		mN = mod;
		mE = pub;
		mD = sec;
	}
	
	private byte[] extendKey(String key){  
		if(key == null){
			Log.e(TAG, "extendKey key == null");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_EXREND_KEY_NULL;
			return null;
		}
		byte[] bKey= new byte[32];
		for(int i=0;i<bKey.length;i++){
			bKey[i] = 'A';
		}
		byte[] bData = key.getBytes();
		
		for(int i=0;i<key.length() && i<bKey.length;i++){
			bKey[i] = bData[i];
		}
		return bKey;
	}
	
	public boolean setKey(String key){
		byte[] bKey = extendKey(key);
		if(bKey == null){
			Log.e(TAG, "setKey bKey == null");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_SET_KEY_NULL;
			return false;
		}
		mSecKey = null;
		mSecKey = new SecretKeySpec(bKey, "AES");
		if(mSecKey == null){
			Log.e(TAG, "setKey mSecKey == null");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_SEC_KEY_NULL;
			return false;
		}
		return true;
	}
		
	public byte[] encryption(String plaintext){
		return encryption(plaintext.getBytes());		
	}
		
	public byte[] encryption(byte[] plaintext){
		if(plaintext == null){
			Log.e(TAG, "encryption plaintext == null");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_ENCRYPTION_NULL;
			return null;
		}
		
		Cipher cipher=null;
		byte[] encryptedData = null;
		if(cipherMode == CipherType.AES){
			try {
				cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
				cipher.init(Cipher.ENCRYPT_MODE, mSecKey, mSpec);	
				encryptedData = cipher.doFinal(plaintext);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
					
		}else if(cipherMode == CipherType.RSA){
			PublicKey pubKey = null;
			try {
				pubKey = readPubKey();
				cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.ENCRYPT_MODE, pubKey);
				reverse(plaintext);	
				encryptedData = cipher.doFinal(plaintext);
				reverse(encryptedData);	
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
		
		}
		return encryptedData;		
	}
	
	public byte[] decryption(String ciphertext){
		return decryption(ciphertext.getBytes());
	}	
		
	public byte[] decryption(byte[] ciphertext){
		if(ciphertext == null){
			Log.e(TAG, "decryption data == null");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_DECRYPTION_NULL;
			return null;
		}
		
		Cipher cipher=null;
		byte[] decryptedData = {'n', 'o'};
		if(cipherMode == CipherType.AES){
			try {
				cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
				cipher.init(Cipher.DECRYPT_MODE, mSecKey, mSpec);	
				decryptedData = cipher.doFinal(ciphertext);
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (InvalidAlgorithmParameterException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
						 
		}else if(cipherMode == CipherType.RSA){			
			PrivateKey privKey=null;
			try {
				privKey = readPrivKey();
				cipher = Cipher.getInstance("RSA");
				cipher.init(Cipher.DECRYPT_MODE, privKey);
				reverse(ciphertext);
				decryptedData = cipher.doFinal(ciphertext);
				reverse(decryptedData);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			} catch (NoSuchPaddingException e) {
				e.printStackTrace();
			} catch (InvalidKeyException e) {
				e.printStackTrace();
			} catch (IllegalBlockSizeException e) {
				e.printStackTrace();
			} catch (BadPaddingException e) {
				e.printStackTrace();
			}
			
		}
		
		return decryptedData;
	}	
	
	private PublicKey readPubKey() throws IOException {
		
		  BigInteger bign = new BigInteger(mN, 16);
		  BigInteger bige = new BigInteger(mE, 16);
		  try {
			  
			  RSAPublicKeySpec keySpec = new RSAPublicKeySpec(bign, bige);
			  KeyFactory fact = KeyFactory.getInstance("RSA");
			  PublicKey pubKey = fact.generatePublic(keySpec);
			  return pubKey;
		  } catch (Exception e) {
			  throw new RuntimeException("Spurious serialisation error", e);
		  }
		  
	}
	
	private PrivateKey readPrivKey() throws IOException{

		 BigInteger bign = new BigInteger(mN, 16);
		 BigInteger bigd = new BigInteger(mD, 16);
		 
		 try {
			  RSAPrivateKeySpec keySpec = new RSAPrivateKeySpec(bign, bigd);
			  KeyFactory fact = KeyFactory.getInstance("RSA");
			  PrivateKey privKey = fact.generatePrivate(keySpec);			  
			  return privKey;
		  } catch (Exception e) {
			  throw new RuntimeException("Spurious serialisation error", e);
		  }
		 
	}
	
	private void reverse(byte[] array){
	      if (array == null) {
	          return;
	      }
	      int i = 0;
	      int j = array.length - 1;
	      byte tmp;
	      while (j > i) {
	          tmp = array[j];
	          array[j] = array[i];
	          array[i] = tmp;
	          j--;
	          i++;
	      }
	}
	
	public String packageData(String data){
		return PREFIX + data;
	}
	
	public String unpackageData(String data){
		if(!data.startsWith(PREFIX))
			return null;
		
		return data.substring(PREFIX.length());
	}
	
	public String encryptData(String data){
		if(data == null){
			Log.e(TAG, "encryptData fail, data is null");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_DATA_NULL;
			return null;
		}
		
		byte[] b = this.encryption(data);
		
		if(b == null){
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_ENCRYPTION_DATA_NULL;
			return null;
		}
		
		return Base64.encodeToString(b, Base64.DEFAULT);
	}
	public String decryptData(String data){
		byte[] b = this.decryption(Base64.decode(data.getBytes(), Base64.DEFAULT));
		
		if(b == null){
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_DECRYPTION_DATA_NULL;
			return null;
		}
		
		String str = null;
		try {
			str = new String(b, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
		return str;
	}
	public boolean validateData(String data){
		if(data == null || data.length() <= 0){
			Log.e(TAG, "validateData invalid data");
			FPNativeBase.lastErrCode = FpResDef.CM_ERR_INVALID_DATA_NULL;
			return false;
		}
		if(data.startsWith(PREFIX)){
			return true;
		}
		return false;
	}
}
