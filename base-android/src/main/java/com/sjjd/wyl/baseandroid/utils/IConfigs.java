package com.sjjd.wyl.baseandroid.utils;

import android.os.Environment;

/**
 * Created by wyl on 2019/5/29.
 */
public interface IConfigs {
    String APK_PATH = Environment.getExternalStorageDirectory() + "/sjjd/apk";
    public static final String appKey = "medtrhg7qrnnhkxploclzxezjumq667zc3l3rkaf";
    public static final String secret = "bbe919b0d4234c4b0f13ebfeb4e7173f";


    int COUNTDOWM_TIME = 60 * 1000 + 100;
    int REMARK_SCROLL_TIME = 8 * 1000;

    int MSG_GET_APK = 3435;

    /**
     * 网络请求相关
     */
    int NET_LOAD_DATA_SUCCESS = 200;//数据加载成功
    int NET_LOAD_DATA_FAILED = -1;//数据加载失败
    int NET_CONNECT_ERROR = 300;//网络错误
    int NET_SERVER_ERROR = 400;//服务器错误
    int NET_URL_SUCCESS = 500;//链接请求正常
    int NET_URL_ERROR = 501;//链接请求错误
    int NET_UNKNOWN_ERROR = 250;//未知错误
    int NET_TIMEOUT = 201;//请求超时
    int NET_TIME_CHANGED = 10001;//时间变化


    ///*socket*//
    String TYPE = "type";
    String HEARTBREAK = "ping";
    String PING = "{\"type\":\"ping\"}";

    // 单个CPU线程池大小
    int POOL_SIZE = 5;
    int MSG_SOCKET_RECEIVED = 2000;
    int MSG_CREATE_TCP_ERROR = 2001;
    int MSG_PING_TCP_TIMEOUT = 2002;

    ///设备注册
    int REGISTER_FORBIDDEN = 0;//禁止注册
    int REGISTER_FOREVER = -1;//永久注册
    int REGISTER_LIMIT = 1;//注册时间剩余

    String APK_VERSION_CODE = "version_code";

    String SP_IP = "ip";
    String SP_WINDOW_NUM = "window_num";
    String SP_PORT = "port";
    String SP_PORT2 = "port2";
    String SP_UNITID = "unitId";
    String SP_ROOM = "room";
    String SP_FLOOR = "floor";
    String SP_AREA = "area";

    int DEVICE_FORBIDDEN = 1;//未注册
    int DEVICE_REGISTERED = 2;//已注册
    int DEVICE_OUTTIME = 3;//已过期


}
