package com.example.testet300enroll;

import java.util.ArrayList;

import android.content.Context;

public class FPList extends ArrayList<FPFingerInfo> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Context mContext;
	
	public FPList(Context context){
		this.mContext = context;
	}
	
	@Override
	public String toString() {
		String fingers = "";
		for (FPFingerInfo ff : this) {				
			if (ff.getAliasName().equals(mContext.getString(R.string.add_finger)))
				continue;
			fingers += ff.getTotalName() + ";";
		}
		int index = fingers.lastIndexOf(";");
		if(index == -1){
			return "";
		}
		return fingers.substring(0, index);
	}
	
}
