package com.wisdom.app.utils;

import com.sunday.slidetabfragment.blue.BlueManager;

import android.app.Application;
import android.text.TextUtils;

public class MyApplication extends Application {
	public static final String TAG = MyApplication.class.getSimpleName();

	private static MyApplication instance;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		instance = this;
		BlueManager.init(instance);
	}

	public static MyApplication getInstance() {
		return instance;
	}
	
}
