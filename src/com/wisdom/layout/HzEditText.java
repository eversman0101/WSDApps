package com.wisdom.layout;

import com.wisdom.app.activity.R;
import com.wisdom.app.utils.Utils;

import android.content.Context;
import android.text.InputType;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

public class HzEditText extends EditText{
	private Context mContext;
	public HzEditText(Context context) {
		super(context);
		mContext=context;
		init();
	}

	public HzEditText(Context context, AttributeSet attrs, int defStyleAttr,
			int defStyleRes) {
		super(context, attrs, defStyleAttr, defStyleRes);
		mContext=context;
		init();
	}

	public HzEditText(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		mContext=context;
		init();
	}

	public HzEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext=context;
		init();
	}
	public void init()
	{
		this.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL|InputType.TYPE_CLASS_NUMBER);
	}
	@Override
	protected void onTextChanged(CharSequence text, int start,
			int lengthBefore, int lengthAfter) {
		// TODO Auto-generated method stub
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		try
		{
		Log.e("HzEditText","charsequence:"+text+" start:"+start);
		
		String str_editI=text.toString();
		if(str_editI.equals(""))
			return;
		if(text.charAt(0)=='.')
		this.setText("");
		
		if(Utils.getNumberDecimalDigits(str_editI)>2)
		{
			this.setText("0");
			Toast.makeText(mContext.getApplicationContext(),mContext.getText(R.string.toast_max_precision),Toast.LENGTH_SHORT).show(); 
		}
		
			double d_edit=Double.valueOf(str_editI);
			if(d_edit>60)
			{
				this.setText("60");
				Toast.makeText(mContext.getApplicationContext(),mContext.getText(R.string.range_40_60),Toast.LENGTH_SHORT).show(); 		
			}
		}catch(Exception ex)
		{
			ex.printStackTrace();
		}
	}
	
	
}
