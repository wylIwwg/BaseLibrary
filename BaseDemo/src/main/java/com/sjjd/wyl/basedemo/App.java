package com.sjjd.wyl.basedemo;

import com.sjjd.wyl.baseandroid.base.BaseApp;

/**
 * Created by wyl on 2019/2/25.
 */
public class App extends BaseApp {
    @Override
    public void onCreate() {
        super.onCreate();

       // initTTs(Environment.getExternalStorageDirectory().getAbsolutePath() + "/test/tts/");
        initDebug(null);
        initOkGO();

        initCrashRestart();
    }
}
