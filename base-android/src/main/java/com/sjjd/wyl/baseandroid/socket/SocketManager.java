package com.sjjd.wyl.baseandroid.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.sjjd.wyl.baseandroid.thread.I;
import com.sjjd.wyl.baseandroid.utils.LogUtils;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by wyl on 2018/11/22.
 */
public class SocketManager {
    private static volatile SocketManager instance = null;
    private static String TAG = "SocketManager";
    private UDPSocket udpSocket;
    private TCPSocket tcpSocket;
    private Context mContext;
    private Handler mHandler;

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

    public void startUdpConnection() {

        if (udpSocket == null) {
            udpSocket = new UDPSocket(mContext);
        }

        // 注册接收消息的接口
        udpSocket.addOnMessageReceiveListener(new OnMessageReceiveListener() {
            @Override
            public void onMessageReceived(String message) {
                handleUdpMessage(message);
            }
        });

        udpSocket.startUDPSocket();

    }

    public TCPSocket getTcpSocket() {
        return tcpSocket;
    }

    /**
     * 处理 udp 收到的消息
     *
     * @param message
     */
    private void handleUdpMessage(String message) {
        try {
            JSONObject jsonObject = new JSONObject(message);
            String ip = jsonObject.optString(Config.TCP_IP);
            String port = jsonObject.optString(Config.TCP_PORT);
            if (!TextUtils.isEmpty(ip) && !TextUtils.isEmpty(port)) {
                startTcpConnection(ip, port);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始 TCP 连接
     *
     * @param ip
     * @param port
     */
    public void startTcpConnection(String ip, String port) {
        tcpSocket = new TCPSocket(mContext);
        tcpSocket.startTcpSocket(ip, port);
        tcpSocket.setOnConnectionStateListener(new OnConnectionStateListener() {
            @Override
            public void onSuccess() {// tcp 创建成功
                //udpSocket.stopHeartbeatTimer();
                LogUtils.e(TAG, "onSuccess: tcp 创建成功");
            }

            @Override
            public void onFailed(int errorCode) {// tcp 异常处理

                switch (errorCode) {
                    case Config.ErrorCode.CREATE_TCP_ERROR:
                        LogUtils.e(TAG, "onFailed: 连接失败");
                        tcpSocket = null;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(Config.ErrorCode.CREATE_TCP_ERROR);
                        }
                        startTcpConnection(Config.IP, Config.PORT);
                        break;
                    case Config.ErrorCode.PING_TCP_TIMEOUT:
                        LogUtils.e(TAG, "onFailed: 连接超时");
                        tcpSocket = null;
                        if (mHandler != null) {
                            mHandler.sendEmptyMessage(Config.ErrorCode.PING_TCP_TIMEOUT);
                        }
                        startTcpConnection(Config.IP, Config.PORT);
                        break;
                }
            }
        });
        tcpSocket.addOnMessageReceiveListener(new OnMessageReceiveListener() {
            @Override
            public void onMessageReceived(String message) {
                if (mHandler != null) {
                    Message msg = Message.obtain();
                    msg.what = I.LOAD_DATA_SUCCESS;
                    msg.obj = message;
                    mHandler.sendMessage(msg);
                } else {
                    LogUtils.e(TAG, "onMessageReceived: " + message);
                }
            }
        });

    }

    public void stopSocket() {

        if (udpSocket != null) {
            udpSocket.stopUDPSocket();
            udpSocket = null;
        }
        if (tcpSocket != null) {
            tcpSocket.stopTcpConnection();
            tcpSocket = null;
        }
    }
}
