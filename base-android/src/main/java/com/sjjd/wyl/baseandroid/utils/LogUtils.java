package com.sjjd.wyl.baseandroid.utils;

import android.util.Log;

/**
 * Created by wyl on 2018/5/22.
 */

public class LogUtils {

    static boolean isDebug = true;

    public static void e(String tag, String msg) {
        if (isDebug)
            Log.e(tag, msg);
    }
}
