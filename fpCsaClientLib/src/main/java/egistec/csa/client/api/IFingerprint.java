package egistec.csa.client.api;

import java.io.UnsupportedEncodingException;
import egistec.csa.client.api.Fingerprint.FingerprintEventListener;

public interface IFingerprint {
	public String getVersion();
	public int getSensorStatus();
	public int[] getFingerprintIndexList(String userId);
	public byte[] getFingerprintId(String userId, int index);
	public String[] getUserIdList();
	public String getSensorInfo();
	public int setAccuracyLevel(int level);
	public int setEnrollSession(boolean flag);
	public int setPassword(String userId, byte[] pwdHash) throws UnsupportedEncodingException;
	public int verifyPassword(String userId, byte[] pwdHash) throws UnsupportedEncodingException;
	public int identify(String userId);
	public int enroll(String userId, int index);
	public int swipeEnroll(String userId, int index);
	public int getEnrollRepeatCount();
	public int request(int status, Object obj);
	//public int notify(int status, Object obj);
	public int verifySensorState(int cmd, int sId, int opt, int logOpt, int uId);
	public int remove(String userId, int index);
	public int cancel();
	public int cleanup();
	public int enableSensorDevice(boolean enable);
	public void setEventListener(FingerprintEventListener l);
	public int eeprom_test(int cmd, int addr, int value);
}
