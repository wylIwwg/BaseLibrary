package com.sjjd.wyl.baseandroid.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

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

    public static void closeApp() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * 获取当前应用版本
     *
     * @param context
     * @return
     */
    public static int getAppVersionCode(Context context, String pgName) {
        int mVersionCode = 0;
        try {
            mVersionCode = context.getPackageManager().getPackageInfo(pgName, 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return mVersionCode;
    }

    //获取目标apk文件包名
    public static String getPackageName(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        if (info != null) {
            ApplicationInfo appInfo = info.applicationInfo;
            return appInfo.packageName; //获取安装包名称 
        }
        return "";
    }

}
