package com.wisdom.layout;

import com.wisdom.app.activity.R;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
/*
 * 自定义Spinner，实现文字居中
 * @author Jinjingyun
 * @date 10/16/2018
 * */
public class CustomSpinner extends Spinner{
	private Context mContext;
	private ArrayAdapter<String> mSpinnerAdapter;
	public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes, int mode) {
		super(context, attrs, defStyleAttr, defStyleRes, mode);
		// TODO Auto-generated constructor stub
		mContext=context;
		init();
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr, int mode) {
		super(context, attrs, defStyleAttr, mode);
		// TODO Auto-generated constructor stub
		mContext=context;
		init();
	}

	public CustomSpinner(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		mContext=context;
		init();
	}

	public CustomSpinner(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext=context;
		init();
	}

	public CustomSpinner(Context context, int mode) {
		super(context, mode);
		// TODO Auto-generated constructor stub
		mContext=context;
		init();
	}

	public CustomSpinner(Context context) {
		super(context);
		mContext=context;
		init();
	}
	
	private void init()
	{
		SpinnerAdapter sp=this.getAdapter();
		mSpinnerAdapter = new ArrayAdapter<String>(mContext, R.layout.spinner_layout);
	    mSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    Log.e("CustomSpinner","count:"+this.getAdapter().getCount());
	    for(int i=0;i<sp.getCount();i++)
	    {
	    	mSpinnerAdapter.add(sp.getItem(i).toString());
	    }
	    this.setAdapter(mSpinnerAdapter);
	    
	}
	
}
