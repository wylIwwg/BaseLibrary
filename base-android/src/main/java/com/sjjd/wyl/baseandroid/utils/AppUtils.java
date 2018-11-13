package com.sjjd.wyl.baseandroid.utils;

import android.content.Context;
import android.content.Intent;

/**
 * Created by wyl on 2018/11/13.
 */
public class AppUtils {

    //重启应用
    public static void restartApp(Context context) {
        final Intent intent = context.getPackageManager().getLaunchIntentForPackage(context.getPackageName());
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
        android.os.Process.killProcess(android.os.Process.myPid());
    }

}
