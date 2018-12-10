package com.wisdom.app.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class SharepreferencesUtil {
	/**
	 * ≤Œ ˝…Ë÷√
	 * */
	private static final String TERMINAL_PARAM="terminal_param";
	private static final String TEMPERATURE="temperature";
	public static String getTemperature(Context context) {
		SharedPreferences preferences = context.getSharedPreferences(
				TERMINAL_PARAM, 0);
        if (preferences.contains(TEMPERATURE)) {
            return preferences.getString(TEMPERATURE, "");
        } else {
            return "";
        }
	}
	public static void setTemperature(Context context,String temp)
	{
		SharedPreferences.Editor editor = context.getSharedPreferences(
				TERMINAL_PARAM, 0).edit();
        editor.putString(TEMPERATURE, temp);
        editor.commit();
	}
	
}
