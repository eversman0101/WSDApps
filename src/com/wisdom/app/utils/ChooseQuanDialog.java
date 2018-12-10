package com.wisdom.app.utils;

import com.wisdom.app.activity.R;

import android.R.integer;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;  
import android.widget.Toast;
  

public class ChooseQuanDialog extends Dialog {  
    private Context mContext;
    private Button yes;//确定按钮  
    private Button no;//取消按钮  
    private Spinner chooseQuan;    //功率因数
    private EditText editQuan;       //圈数
    private TextView titleTv;//消息标题文本  

    private String titleStr;//从外界设置的title文本  
    private String yesStr, noStr;
    private double chooseQuanStr=0;
    private String editQuanStr;

  
    public String getEditQuanStr() {
		return editQuan.getText().toString();
	}

	public void setEditQuanStr(String editQuanStr) {
		this.editQuanStr = editQuanStr;
	}

	private onNoOnclickListener noOnclickListener;//取消按钮被点击了的监听器  
    private onYesOnclickListener yesOnclickListener;//确定按钮被点击了的监听器  
  
    /** 
     * 设置取消按钮的显示内容和监听 
     * 
     * @param str 
     * @param onNoOnclickListener 
     */  
    public void setNoOnclickListener(String str, onNoOnclickListener onNoOnclickListener) {  
        if (str != null) {  
            noStr = str;  
        }  
        this.noOnclickListener = onNoOnclickListener;  
    }  
  
    /** 
     * 设置确定按钮的显示内容和监听 
     * 
     * @param str 
     * @param onYesOnclickListener 
     */  
    public void setYesOnclickListener(String str, onYesOnclickListener onYesOnclickListener) {  
        if (str != null) {  
            yesStr = str;  
        }  
        this.yesOnclickListener = onYesOnclickListener;  
    }  
  
    public ChooseQuanDialog(Context context) {  
        super(context, R.style.MyDialog);  
        mContext=context;
    }  
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.dialog_choose_quan);  
        //按空白处不能取消动画  
        setCanceledOnTouchOutside(false);  
  
        //初始化界面控件  
        initView();  
        //初始化界面数据  
        initData();  
        //初始化界面控件的事件  
        initEvent();  
    }  
  
    /** 
     * 初始化界面的确定和取消监听器 
     */  
    private void initEvent() { 
    	chooseQuan.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				//Activity.this.getResources().getStringArray(R.array.acnl_Ub)[position];
				String str =chooseQuan.getSelectedItem().toString();//从spinner中获取被选择的数据
				if(str.equalsIgnoreCase("1.0")){
					editQuanStr="0";
				}else if(str.equalsIgnoreCase("0.25L")){
					editQuanStr="75.5";
				}else if(str.equalsIgnoreCase("0.5L")){
					editQuanStr="60";
				}else if(str.equalsIgnoreCase("0.8L")){
					editQuanStr="36.9";
				}else if(str.equalsIgnoreCase("0.25C")){
					editQuanStr="284.5";
				}else if(str.equalsIgnoreCase("0.5C")){
					editQuanStr="300";
				}else if(str.equalsIgnoreCase("0.8C")){
					editQuanStr="323.1";
				}
				 editQuan.setText(editQuanStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	editQuan.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				String str_editI=s.toString();
				if(str_editI.equals(""))
					return;
				if(s.charAt(0)=='.')
					editQuan.setText("");
				if(Utils.getNumberDecimalDigits(str_editI)>4)
				{
					editQuan.setText("");
					Toast.makeText(mContext,mContext.getText(R.string.toast_max_precision_10000),Toast.LENGTH_SHORT).show(); 
				}
				try
				{
				double d_quan=Double.valueOf(str_editI);
				if(d_quan<0||d_quan>360)
				{
					if(d_quan<0)
						editQuan.setText("0");
					else if(d_quan>360)
						editQuan.setText("360");
					Toast.makeText(mContext,mContext.getText(R.string.range_0_360),Toast.LENGTH_SHORT).show();	
				}
				}catch(Exception ex)
				{
					ex.printStackTrace();
				}
				
			}

			@Override
			public void afterTextChanged(Editable s) {
				
			}
    		
    	});
        //设置确定按钮被点击后，向外界提供监听  
        yes.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if (yesOnclickListener != null) {  
                    yesOnclickListener.onYesClick();  
                }  
            }  
        });  
        //设置取消按钮被点击后，向外界提供监听  
        no.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if (noOnclickListener != null) {  
                    noOnclickListener.onNoClick();  
                }  
            }  
        });  
    }  
  
    /** 
     * 初始化界面控件的显示数据 
     */  
    private void initData() {  
        //如果用户自定了title和message  
        if (titleStr != null) {  
            titleTv.setText(titleStr);  
        }  
        
//        if(editUStr!=null){
//        	editU.setText(editUStr);
//        }
        //如果设置按钮的文字  
        if (yesStr != null) {  
            yes.setText(yesStr);  
        }  
        if (noStr != null) {  
            no.setText(noStr);  
        }  
    }  
  
    /** 
     * 初始化界面控件 
     */  
    private void initView() {  
        yes = (Button) findViewById(R.id.yes);  
        no = (Button) findViewById(R.id.no);  
        titleTv = (TextView) findViewById(R.id.title);  
        chooseQuan =(Spinner) findViewById(R.id.sp_option_quan); 
        editQuan=(EditText) findViewById(R.id.et_quan);
    }  
  
    /** 
     * 从外界Activity为Dialog设置标题 
     * 
     * @param title 
     */  
    public void setTitle(String title) {  
        titleStr = title;  
    }  
    
  
    /** 
     * 从外界Activity为Dialog设置dialog的message 
     * 
     * @param message 
     */  
//    public void setMessage(String message) {  
//        messageStr = message;  
//    }  
  
    /** 
     * 设置确定按钮和取消被点击的接口 
     */  
    public interface onYesOnclickListener {  
        public void onYesClick();  
    }  
  
    public interface onNoOnclickListener {  
        public void onNoClick();  
    }  
}  
