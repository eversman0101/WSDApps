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
public class ITypePopupWindow extends PopupWindow{
	private View mMenuView;
	private TextView tv_zhijiejieru;
	private TextView tv_100A;
	private TextView tv_5A;
	public ITypePopupWindow(final Activity context, OnClickListener itemsOnClick) {
        super(context);
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mMenuView = inflater.inflate(R.layout.popupwindow, null);
        tv_zhijiejieru=(TextView)mMenuView.findViewById(R.id.tv_zhijiejieru);
        tv_100A=(TextView)mMenuView.findViewById(R.id.tv_100A);
        tv_5A=(TextView)mMenuView.findViewById(R.id.tv_5A);

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
       tv_zhijiejieru.setOnClickListener(itemsOnClick);
       tv_100A.setOnClickListener(itemsOnClick);
       tv_5A.setOnClickListener(itemsOnClick);
	}
	
}
