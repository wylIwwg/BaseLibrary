package com.sjjd.wyl.baseandroid.socket;
/**
 * Created by wyl on 2019/12/17.
 */

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.sjjd.wyl.baseandroid.tools.IConfigs;
import com.sjjd.wyl.baseandroid.tools.ToolLog;


/**
 * Created by wyl on 2018/11/22.
 */
public class SocketManager {
    private static volatile SocketManager instance = null;
    private static final String TAG = "SocketManager";
    private TCPSocket tcpSocket;
    private Context mContext;
    private Handler mHandler;
    private String IP;
    private String PORT;
    private String PING = "{\"type\":\"ping\"}";
    private int delayRequest = 5000;//延迟发送
    private long TIME_OUT = 15 * 1000;//心跳超时时间
    private long HEARTBEAT_RATE = 5 * 1000;//心跳间隔

    public void setTIME_OUT(long TIME_OUT) {
        this.TIME_OUT = TIME_OUT;
    }

    public long getTIME_OUT() {
        return TIME_OUT;
    }

    public long getHEARTBEAT_RATE() {
        return HEARTBEAT_RATE;
    }

    public void setHEARTBEAT_RATE(long HEARTBEAT_RATE) {
        this.HEARTBEAT_RATE = HEARTBEAT_RATE;
    }

    public void setPING(String ping) {
        this.PING = ping;
        ToolLog.e(TAG, "setPING: " + ping);
    }

    public String getPING() {
        return PING == null ? "" : PING;
    }

    public void setDelayRequest(int delayRequest) {
        this.delayRequest = delayRequest;
    }

    private SocketManager(Context context) {
        mContext = context.getApplicationContext();
    }

    public static SocketManager getInstance(Context context) {
        if (instance == null) {
            synchronized (SocketManager.class) {
                if (instance == null) {
                    instance = new SocketManager(context);
                }
            }
        }
        return instance;
    }

    public SocketManager setHandler(Handler handler) {
        mHandler = handler;
        return this;
    }


    public TCPSocket getTcpSocket() {
        if (tcpSocket == null) {
            tcpSocket = new TCPSocket(mContext);
        }
        return tcpSocket;
    }

    public synchronized void startTcpConnection(String ip, String port) {
        startTcpConnection(ip, port, PING);
    }

    /**
     * 开始 TCP 连接
     *
     * @param ip
     * @param port
     */
    public synchronized void startTcpConnection(String ip, String port, String ping) {
        IP = ip;
        PORT = port;
        PING = ping;
        tcpSocket = new TCPSocket(mContext);
        if (tcpSocket != null) {
            //先设置监听
            tcpSocket.setOnConnectionStateListener(new OnConnectionStateListener() {
                @Override
                public void onSuccess() {// tcp 创建成功
                    //udpSocket.stopHeartbeatTimer();
                    ToolLog.e(TAG, "onSuccess: tcp 创建成功");
                }

                @Override
                public void onFailed(int errorCode) {// tcp 异常处理

                    switch (errorCode) {
                        case IConfigs.MSG_CREATE_TCP_ERROR:
                            ToolLog.e(TAG, "onFailed: 连接失败");
                            tcpSocket = null;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(IConfigs.MSG_CREATE_TCP_ERROR);
                                //延迟时间去连接
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startTcpConnection(IP, PORT, PING);
                                    }
                                }, delayRequest);
                            }
                            break;
                        case IConfigs.MSG_PING_TCP_TIMEOUT:
                            ToolLog.e(TAG, "onFailed: 连接超时");
                            tcpSocket = null;
                            if (mHandler != null) {
                                mHandler.sendEmptyMessage(IConfigs.MSG_PING_TCP_TIMEOUT);
                                mHandler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        startTcpConnection(IP, PORT, PING);
                                    }
                                }, delayRequest);
                            }
                            break;
                    }
                }
            });
            tcpSocket.addOnMessageReceiveListener(new OnMessageReceiveListener() {
                @Override
                public void onMessageReceived(String message) {
                    if (mHandler != null) {
                        Message msg = Message.obtain();
                        msg.what = IConfigs.MSG_SOCKET_RECEIVED;
                        msg.obj = message;
                        mHandler.sendMessage(msg);
                    } else {
                        ToolLog.e(TAG, "onMessageReceived: " + message);
                    }
                }
            });
            //再连接
            tcpSocket.startTcpSocket(ip, port);
        }


    }

    public void destroy() {
        stopSocket();
        instance = null;
    }

    public void stopSocket() {

        if (tcpSocket != null) {
            tcpSocket.stopHeartbeatTimer();
            tcpSocket.stopTcpConnection();
            tcpSocket = null;
        }
    }
}
