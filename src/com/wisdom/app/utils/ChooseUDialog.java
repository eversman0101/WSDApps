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
    private Button yes;//ȷ����ť  
    private Button no;//ȡ����ť  
    private Spinner chooseUb;    //��ѹ��ѡֵ
    private Spinner chooseUr;    //��ѹ�ٷֱ�
    private EditText editU;       //��ѹ�Զ���
    private TextView titleTv;//��Ϣ�����ı�  

    private String titleStr;//��������õ�title�ı�  
    private String yesStr, noStr;
    private double chooseUbStr=0;
    private double chooseUrStr=0;
    private String editUStr;

  
    private onNoOnclickListener noOnclickListener;//ȡ����ť������˵ļ�����  
    private onYesOnclickListener yesOnclickListener;//ȷ����ť������˵ļ�����  
  
    /** 
     * ����ȡ����ť����ʾ���ݺͼ��� 
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
     * ����ȷ����ť����ʾ���ݺͼ��� 
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
        //���հ״�����ȡ������  
        setCanceledOnTouchOutside(false);  
  
        //��ʼ������ؼ�  
        initView();  
        //��ʼ����������  
        initData();  
        //��ʼ������ؼ����¼�  
        initEvent();  
    }  
  
    /** 
     * ��ʼ�������ȷ����ȡ�������� 
     */  
    private void initEvent() { 
    	chooseUb.setOnItemSelectedListener(new OnItemSelectedListener(){

			@Override
			public void onItemSelected(AdapterView<?> parent, View view,int position, long id) {
				// TODO Auto-generated method stub
				//Activity.this.getResources().getStringArray(R.array.acnl_Ub)[position];
				double data =Double.valueOf(chooseUb.getSelectedItem().toString());//��spinner�л�ȡ��ѡ�������
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
				double data =Double.valueOf(chooseUr.getSelectedItem().toString());//��spinner�л�ȡ��ѡ�������
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
        //����ȷ����ť�������������ṩ����  
        yes.setOnClickListener(new View.OnClickListener() {  
            @Override  
            public void onClick(View v) {  
                if (yesOnclickListener != null) {  
                    yesOnclickListener.onYesClick();  
                }  
            }  
        });  
        //����ȡ����ť�������������ṩ����  
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
     * ��ʼ������ؼ�����ʾ���� 
     */  
    private void initData() {  
        //����û��Զ���title��message  
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
        //������ð�ť������  
        if (yesStr != null) {  
            yes.setText(yesStr);  
        }  
        if (noStr != null) {  
            no.setText(noStr);  
        }  
    }  
  
    /** 
     * ��ʼ������ؼ� 
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
     * �����ActivityΪDialog���ñ��� 
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
     * �����ActivityΪDialog����dialog��message 
     * 
     * @param message 
     */  
//    public void setMessage(String message) {  
//        messageStr = message;  
//    }  
  
    /** 
     * ����ȷ����ť��ȡ��������Ľӿ� 
     */  
    public interface onYesOnclickListener {  
        public void onYesClick();  
    }  
  
    public interface onNoOnclickListener {  
        public void onNoClick();  
    }  
}  
