package com.sjjd.wyl.baseandroid.utils;

import android.os.Environment;

/**
 * Created by wyl on 2019/5/29.
 */
public interface Configs {
    String APK_PATH = Environment.getExternalStorageDirectory() + "/sjjd/apk";
    public static final String appKey = "medtrhg7qrnnhkxploclzxezjumq667zc3l3rkaf";
    public static final String secret = "bbe919b0d4234c4b0f13ebfeb4e7173f";

    public static String API = "http://%1$S/queue_system_dj/public/api/";
    int MSG_SOCKET_RECEIVED = 2000;

    int COUNTDOWM_TIME = 60 * 1000 + 100;
    int REMARK_SCROLL_TIME = 8 * 1000;

    int MSG_GET_APK = 3435;

    int REGISTER_FORBIDDEN = 0;//禁止注册
    int REGISTER_FOREVER = -1;//永久注册
    int REGISTER_LIMIT = 1;//注册时间剩余

    String APK_VERSION_CODE = "version_code";

    interface SP {
        String IP = "ip";
        String WINDOW_NUM = "window_num";
        String PORT = "port";

        String UNITID = "unitId";
        String FLOOR = "floor";
        String AREA = "area";

    }
}
