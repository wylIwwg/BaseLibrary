package com.sjjd.wyl.baseandroid.socket;

/**
 * Created by wyl on 2018/11/22.
 */
public class Config {
    public static final String MSG = "type";
    public static final String HEARTBREAK = "ping";
    public static final String PING = "ping";

    public static final String TCP_IP = "ip";
    public static final String IP = "192.168.2.135";
    public static final String TCP_PORT = "port";
    public static final String PORT = "8282";

    // 单个CPU线程池大小
    public static final int POOL_SIZE = 5;

    /**
     * 错误处理
     */
    public static class ErrorCode {

        public static final int CREATE_TCP_ERROR = 1;

        public static final int PING_TCP_TIMEOUT = 2;
    }

}
