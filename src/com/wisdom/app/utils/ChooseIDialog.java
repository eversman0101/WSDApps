package com.wisdom.app.utils;

import java.math.BigDecimal;

import com.wisdom.app.activity.R;

import android.app.Dialog;  
import android.content.Context;  
import android.os.Bundle;  
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemSelectedListener;  
  

public class ChooseIDialog extends Dialog {  
    private Context mContext;
    private Button yes;//确定按钮  
    private Button no;//取消按钮  
    private TextView titleTv;//消息标题文本  
    private Spinner chooseIb;    //电流可选值
    private Spinner chooseIr;    //电流百分比
    private EditText editI;       //电流自定义
    private String titleStr;//从外界设置的title文本  

    //确定文本和取消文本的显示内容  
    private String yesStr, noStr;  
    private double chooseIbStr=0;
    private double chooseIrStr=0;
    private String editIStr;
    
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
  
    public ChooseIDialog(Context context) {  
        super(context, R.style.MyDialog);  
        mContext=context;
    }
  
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.dialog_choose_i);  
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
    	chooseIb.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				//Activity.this.getResources().getStringArray(R.array.acnl_Ub)[position];
				double data =Double.valueOf(chooseIb.getSelectedItem().toString());//从spinner中获取被选择的数据
				 chooseIbStr =data;
				 editIStr=String.valueOf(chooseIbStr*chooseIrStr/100);
				 editI.setText(editIStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TDO Auto-generated method stub
				
			}
    		
    	});
    	chooseIr.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				//Activity.this.getResources().getStringArray(R.array.acnl_Ub)[position];
				 double data =Double.valueOf(chooseIr.getSelectedItem().toString());//从spinner中获取被选择的数据
				 chooseIrStr =data;
				 BigDecimal decimal=new BigDecimal(String.valueOf(chooseIbStr*chooseIrStr/100));
				 decimal=decimal.setScale(3,BigDecimal.ROUND_DOWN);
				 double d=Double.valueOf(decimal.toString());
				 
				 editIStr=String.valueOf(d);
				 editI.setText(editIStr);
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				// TODO Auto-generated method stub
				
			}
    		
    	});
    	editI.addTextChangedListener(new TextWatcher(){

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
					editI.setText("");
				//double d=Double.valueOf(str_editI);
				if(Utils.getNumberDecimalDigits(str_editI)>3)
				{
					editI.setText("");
					Toast.makeText(mContext,mContext.getText(R.string.toast_max_precision_1000),Toast.LENGTH_SHORT).show(); 
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
//        if (messageStr != null) {  
//            messageTv.setText(messageStr);  
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
        chooseIb =(Spinner) findViewById(R.id.sp_option_Ib); 
        chooseIr =(Spinner) findViewById(R.id.sp_option_Ir);
        editI=(EditText) findViewById(R.id.et_i);
    }  
  
    /** 
     * 从外界Activity为Dialog设置标题 
     * 
     * @param title 
     */  
    public void setTitle(String title) {  
        titleStr = title;  
    }  
  
    public void setEditIStr(String editI){
    	editIStr=editI;
    }
    public String getEditIStr(){
    	return editI.getText().toString();
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
