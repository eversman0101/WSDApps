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
 * 自定义PopupWindow
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
        

        //设置SelectPicPopupWindow的View
        this.setContentView(mMenuView);
       //设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.FILL_PARENT);
        //设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.WRAP_CONTENT);
        //设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        //设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.AnimBottom);
        //实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0x000000);
        //设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        //点击框外销毁弹出框
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
		tv_shebei.setText("设备时钟："+arr_clock[0]+"年"+arr_clock[1]+"月"+arr_clock[2]+"日"+arr_clock[3]+"时"+arr_clock[4]+"分"+arr_clock[5]+"秒");
	}
	public void setGPSClock(int[] arr)
	{
		this.arr_gpsclock=arr;
		tv_gps.setText("GPS时钟："+arr_gpsclock[0]+"年"+arr_gpsclock[1]+"月"+arr_gpsclock[2]+"日"+arr_gpsclock[3]+"时"+arr_gpsclock[4]+"分"+arr_gpsclock[5]+"秒");
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
