package com.example.testet300enroll;

import android.content.Context;

public class FPFingerInfo {

	private String mKeyWithAlias;
	private boolean mIsMatched;
	private String mEnrollKey;
	private String mAliasName;
	
	public FPFingerInfo(String name, Context context){
		if(name.equals("AddFinger")){
			this.mAliasName = context.getString(R.string.add_finger);
			this.mEnrollKey="";
			this.mKeyWithAlias="";
			return;
		}
		this.mKeyWithAlias = name;
		this.mEnrollKey = name.substring(0, name.indexOf(":"));
		this.mAliasName = name.substring(name.indexOf(":")+1);		
	}
	
	public String getTotalName(){
		return mKeyWithAlias;
	}	
	
	public String getEnrollkey(){
		return mEnrollKey;
	}	
	
	public void setFingerMatched(){
		mIsMatched=true;
	}
	public void resetFingerMatched(){
		mIsMatched=false;
	}
	public boolean isFingerMatched(){
		return mIsMatched;
	}
	
	public String getAliasName(){
		return mAliasName;
	}
	
	public void setAliasName(String aliasName){
		this.mAliasName = aliasName;
		this.mKeyWithAlias = mEnrollKey + ":" + this.mAliasName;
	}
	
}
