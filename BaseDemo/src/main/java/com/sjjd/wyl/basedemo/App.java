package com.sjjd.wyl.basedemo;

import android.os.Environment;

import com.sjjd.wyl.baseandroid.base.BaseApp;

/**
 * Created by wyl on 2019/2/25.
 */
public class App extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();

      //  initDebug(null);
        initOkGO();

       // initCrashRestart();
        initTTs(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/tts/");
    }
}
