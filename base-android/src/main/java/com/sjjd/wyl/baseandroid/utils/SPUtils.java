package com.sjjd.wyl.baseandroid.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by wyl on 2018/5/14.
 */

public class SPUtils {
    static SPUtils mSPUtils = new SPUtils();
    static SharedPreferences sp;

    public static SPUtils init(Context context) {
        if (sp == null) {
            sp = context.getSharedPreferences("sp", Context.MODE_PRIVATE);
        }
        return mSPUtils;
    }

    public SPUtils putDIYString(String key, String value) {

        sp.edit().putString(key, value).apply();
        return mSPUtils;
    }

    public SPUtils putDIYBoolean(String key, boolean value) {

        sp.edit().putBoolean(key, value).apply();
        return mSPUtils;
    }

    public SPUtils putDIYInt(String key, int value) {

        sp.edit().putInt(key, value).apply()
        ;
        return mSPUtils;
    }

    public String getDIYString(String key) {
        return sp.getString(key, null);
    }

    public boolean getDIYBoolean(String key) {
        return sp.getBoolean(key, false);
    }

    public int getDIYInt(String key) {
        return sp.getInt(key, -1);
    }
}
