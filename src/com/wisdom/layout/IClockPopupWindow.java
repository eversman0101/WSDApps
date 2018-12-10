package com.wisdom.layout;

import com.wisdom.app.activity.ManualCheckLoadActivity;
import com.wisdom.app.activity.R;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupWindow.OnDismissListener;
/**
 * @author jinjingyun
 * �Զ���PopupWindow
 * */
public class IClockPopupWindow extends PopupWindow{
	private View mMenuView;
	private TextView tv_shebei;
	private TextView tv_gps;
	private int[] arr_clock;
	private int[] arr_gpsclock;
	public IClockPopupWindow(final Activity context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popupwindow_clock, null);
        tv_shebei=(TextView)mMenuView.findViewById(R.id.tv_zhijiejieru);
        tv_gps=(TextView)mMenuView.findViewById(R.id.tv_100A);
        

        //����SelectPicPopupWindow��View
        this.setContentView(mMenuView);
       //����SelectPicPopupWindow��������Ŀ�
        this.setWidth(LayoutParams.FILL_PARENT);
        //����SelectPicPopupWindow��������ĸ�
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //����SelectPicPopupWindow��������ɵ��
        this.setFocusable(true);
        //����SelectPicPopupWindow�������嶯��Ч��
        this.setAnimationStyle(R.style.AnimBottom);
        //ʵ����һ��ColorDrawable��ɫΪ��͸��
        ColorDrawable dw = new ColorDrawable(0x000000);
        //����SelectPicPopupWindow��������ı���
        this.setBackgroundDrawable(dw);
        //����������ٵ�����
        mMenuView.setOnTouchListener(new View.OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int height = mMenuView.findViewById(R.id.pop_layout).getTop();
                int y=(int) event.getY();
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(y<height){
                        dismiss();
                    }
                }
                return true;
			}
		});
       tv_shebei.setOnClickListener(itemsOnClick);
       tv_gps.setOnClickListener(itemsOnClick);
       //tv_5A.setOnClickListener(itemsOnClick);
	}
	public void setClock(int[] arr)
	{
		this.arr_clock=arr;
		tv_shebei.setText("�豸ʱ�ӣ�"+arr_clock[0]+"��"+arr_clock[1]+"��"+arr_clock[2]+"��"+arr_clock[3]+"ʱ"+arr_clock[4]+"��"+arr_clock[5]+"��");
	}
	public void setGPSClock(int[] arr)
	{
		this.arr_gpsclock=arr;
		tv_gps.setText("GPSʱ�ӣ�"+arr_gpsclock[0]+"��"+arr_gpsclock[1]+"��"+arr_gpsclock[2]+"��"+arr_gpsclock[3]+"ʱ"+arr_gpsclock[4]+"��"+arr_gpsclock[5]+"��");
	}
	public int[] getClock()
	{
		return this.arr_clock;
	}
	public int[] getGPSClock()
	{
		return this.arr_gpsclock;
	}
}
