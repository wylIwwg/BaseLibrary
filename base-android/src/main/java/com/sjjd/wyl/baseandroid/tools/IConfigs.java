package com.sjjd.wyl.baseandroid.tools;

import android.os.Environment;

/**
 * Created by wyl on 2019/5/29.
 */
public interface IConfigs {


    public static final String appKey = "medtrhg7qrnnhkxploclzxezjumq667zc3l3rkaf";
    public static final String secret = "bbe919b0d4234c4b0f13ebfeb4e7173f";

    String PATH_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sjjd/";
    String PATH_APK = PATH_ROOT + "apk";
    String PATH_TTS = PATH_ROOT + "tts";
    String PATH_LOG = PATH_ROOT + "log";
    String PATH_MAC =  Environment.getExternalStorageDirectory().getAbsolutePath() + "/Android/mac";

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

    String HOST = "http://%1$s:%2$s";//192.168.2.188:8080


    ///*socket*//
    String TYPE = "type";
    String HEARTBREAK = "ping";
    String PING = "{\"type\":\"ping\"}";

    // 单个CPU线程池大小
    int POOL_SIZE = 5;
    int MSG_SOCKET_RECEIVED = 2000;//接收socket通知
    int MSG_CREATE_TCP_ERROR = 2001;//tcp创建失败
    int MSG_PING_TCP_TIMEOUT = 2002;//tcp连接超时

    ///设备注册
    int REGISTER_FORBIDDEN = 0;//禁止注册
    int REGISTER_FOREVER = -1;//永久注册
    int REGISTER_LIMIT = 1;//注册时间剩余

    String APK_VERSION_CODE = "version_code";

    /*配置相关*/
    String SP_IP = "ip";
    String SP_PORT_HTTP = "port_http";
    String SP_PORT_SOCKET = "port_socket";//

    String SP_WINDOW_NUM = "win_num";//窗口号
    String SP_WINDOW_ID = "win_id";//窗口id
    String SP_ROOM_NUM = "room_num";//房间号
    String SP_ROOM_ID = "room_id";//房间id
    String SP_APP_TYPE = "app_type";


    String SP_DEPART_NAME = "depart_n";//科室名
    String SP_DEPART_ID = "depart_id";//科室id

    String SP_CLINIC_NAME = "clinic_n";//诊室名
    String SP_CLINIC_ID = "clinic_id";//诊室id

    String SP_SETTING_SCROLL_TIME = "scroll";//轮播滚动时间
    String SP_SETTING_DELAY_TIME = "delay";//延迟滚动时间
    String SP_SETTING_BACK_TIME = "back";//界面操作返回时间


    int DEVICE_FORBIDDEN = 0;//未注册
    int DEVICE_REGISTERED = 1;//已注册
    int DEVICE_OUTTIME = 2;//已过期


}
