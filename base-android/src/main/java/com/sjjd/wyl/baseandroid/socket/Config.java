package com.sjjd.wyl.baseandroid.socket;

/**
 * Created by wyl on 2018/11/22.
 */
public interface Config {
     String MSG = "type";
     String HEARTBREAK = "ping";
     String PING = "ping";

     String TCP_IP = "ip";
     String IP = "192.168.2.135";
     String TCP_PORT = "port";
     String PORT = "8282";

     int MSG_SOCKET_RECEIVED = 1001;

    // 单个CPU线程池大小
     int POOL_SIZE = 5;

    /**
     * 错误处理
     */
    public interface ErrorCode {

        int CREATE_TCP_ERROR = 1;

        int PING_TCP_TIMEOUT = 2;
    }

}
