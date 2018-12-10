package com.wisdom.app.utils;

import android.content.Context;
import android.os.Vibrator;

/**
 * 璁￠噺绠遍攣鐘舵�佸伐鍏风被
 * 聽
 *
 * @author ShiPengHao
 * @date 2018/1/25
 */
public class LockStateUtil {
    /**
     * 寮�閿佺姸鎬�
     */
    private static boolean sUnlock;

    /**
     * 鏄惁鏄紑閿佺姸鎬�
     *
     * @return 鏄痶rue锛屽惁鍒檉alse
     */
    public static boolean isUnlock() {
        return sUnlock;
    }

    /**
     * 璁剧疆璁￠噺绠遍攣鐘舵�佸埌鍐呭瓨涓�
     *
     * @param state 鐘舵�侊紝寮�閿乼rue锛屽惁鍒檉alse
     */
    public static void setUnlock(boolean state) {
        sUnlock = state;
    }

    /**
     * 閲嶇疆寮�閿佺姸鎬佷负false
     */
    public static void reset() {
        sUnlock = false;
    }

    /**
     * 妫�鏌ヨ閲忕閿佺殑鐘舵�侊紝濡傛灉涓哄紑閿侊紝鍒欓渿鍔ㄦ彁绀�
     *
     * @param context ctx
     */
    public static void checkLockState(Context context) {
        if (sUnlock) {
            Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            if (vibrator != null) {
                vibrator.vibrate(10000);
            }
        }
    }
}
