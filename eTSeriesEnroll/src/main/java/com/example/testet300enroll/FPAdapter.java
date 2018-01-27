package com.example.testet300enroll;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.TransitionDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class FPAdapter extends ArrayAdapter<FPFingerInfo> {

	private static final String TAG = "FingerprintAdapter";
	private List<FPFingerInfo> mList;
	private Context mContext;
	private ViewHolder viewHolder;
	private Handler mHandler;
	private LayoutInflater minflater;
	
	//public FPAdapter(Context context, ArrayList<FPFingerInfo> list){
	public FPAdapter(Context context, List<FPFingerInfo> list, Handler handler){
		super(context, R.layout.fp_finger_cell, list);
		
		this.mList = list;
		this.mContext = context;
		this.mHandler = handler;
		minflater = LayoutInflater.from(context);
	}
	
	static class ViewHolder {
		protected TextView fingerName;
		//protected ImageView fingerImage;
	}
	
	@Override
	public View getView (int position, View convertView, ViewGroup parent){
		View v = null;
		FPFingerInfo listItem = mList.get( position );

		if(convertView == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.fp_finger_cell, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.fingerName = (TextView)v.findViewById(R.id.finger_name);
			//viewHolder.fingerImage = (ImageView)v.findViewById(R.id.finger);
			v.setTag( viewHolder );
		}else{
			v = convertView;
		}
		
		if(listItem.isFingerMatched()){
			//v.setBackgroundColor(Color.GREEN);	
            // Transition drawable with a transparent drawable and the final drawable
            final TransitionDrawable td =
                    new TransitionDrawable(new Drawable[] {
                            new ColorDrawable(Color.BLACK),
                            new ColorDrawable(Color.GREEN),  
                    });
			
            v.setBackground(td);
            td.startTransition(FPFingerActivity.VERIFY_TRANSITION_TIME);            
            mHandler.postDelayed(new Runnable(){
            	@Override
            	public void run(){
            		td.reverseTransition(FPFingerActivity.VERIFY_TRANSITION_TIME);	
            	}
            }, FPFingerActivity.TIMER_DELAY);
            //mHandler.postDelayed(((FPFingerActivity)mContext).new VerifyTask(), FPFingerActivity.TIMER_DELAY);                      
            listItem.resetFingerMatched();
		}else{
			v.setBackgroundColor(Color.BLACK);
		}
		ViewHolder holder = (ViewHolder)v.getTag();
		holder.fingerName.setText(listItem.getAliasName());
		
		if(0 == position){
			holder.fingerName.setTextColor(Color.parseColor("#3786c8"));
		}

		return v;
	}	
	
}
