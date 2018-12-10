package com.wisdom.app.utils;

import java.math.BigDecimal;

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
  

public class ChooseUDialog extends Dialog {  
    private Context mContext;
    private Button yes;//确定按钮  
    private Button no;//取消按钮  
    private Spinner chooseUb;    //电压可选值
    private Spinner chooseUr;    //电压百分比
    private EditText editU;       //电压自定义
    private TextView titleTv;//消息标题文本  

    private String titleStr;//从外界设置的title文本  
    private String yesStr, noStr;
    private double chooseUbStr=0;
    private double chooseUrStr=0;
    private String editUStr;

  
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
  
    public ChooseUDialog(Context context) {  
        super(context, R.style.MyDialog);  
        mContext=context;
    }
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.dialog_choose_u);  
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
    	chooseUb.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				//Activity.this.getResources().getStringArray(R.array.acnl_Ub)[position];
				double data =Double.valueOf(chooseUb.getSelectedItem().toString());//从spinner中获取被选择的数据
				 chooseUbStr =data;
				 editUStr=String.valueOf(chooseUbStr*chooseUrStr/100);
				 editU.setText(editUStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	chooseUr.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				//Activity.this.getResources().getStringArray(R.array.acnl_Ub)[position];
				double data =Double.valueOf(chooseUr.getSelectedItem().toString());//从spinner中获取被选择的数据
				 chooseUrStr=data;
				 BigDecimal decimal=new BigDecimal(String.valueOf(chooseUbStr*chooseUrStr/100));
				 decimal=decimal.setScale(2,BigDecimal.ROUND_DOWN);
				 double d=Double.valueOf(decimal.toString());
				 
				 
				 editUStr=String.valueOf(d);
				 editU.setText(editUStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	editU.addTextChangedListener(new TextWatcher(){

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				String str_editI=s.toString();
				if(str_editI.equals(""))
					return;
				if(s.charAt(0)=='.')
					editU.setText("");
				//double d=Double.valueOf(str_editI);
				if(Utils.getNumberDecimalDigits(str_editI)>2)
				{
					editU.setText("");
					Toast.makeText(mContext,mContext.getText(R.string.toast_max_precision),Toast.LENGTH_SHORT).show(); 
				}
			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub
				
			}});
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
        if(chooseUbStr!=0){
        	
        }
        if(chooseUrStr!=0){
        	
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
        chooseUb =(Spinner) findViewById(R.id.sp_option_Ub); 
        chooseUr =(Spinner) findViewById(R.id.sp_option_Ur);
        editU=(EditText) findViewById(R.id.et_u);
    }  
  
    /** 
     * 从外界Activity为Dialog设置标题 
     * 
     * @param title 
     */  
    public void setTitle(String title) {  
        titleStr = title;  
    }  
    
    public void setEditUStr(String editU){
    	editUStr=editU;
    }
    public String getEditUStr(){
    	return editU.getText().toString();
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
