package com.sunday.slidetabfragment.blue;

import android.util.Log;

import com.wisdom.app.activity.BuildConfig;

/**
 * log日志打印控制工具类
 *  
 *
 * @author ShiPengHao
 * @date 2017/10/25
 */
public class LogUtil {
    /**
     * 是否显示线程信息
     */
    private static final boolean SHOW_THREAD = true;

    public static void i(String tag, String msg) {
        if (BuildConfig.DEBUG) {
            if (SHOW_THREAD) {
                msg = msg + ". Thread：" + Thread.currentThread().getName();
            }
            Log.i(tag, msg);
        }
    }

    public static void i(Class clazz, String msg) {
        i(clazz.getSimpleName(), msg);
    }
}
