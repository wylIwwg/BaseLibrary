package com.sjjd.wyl.baseandroid.socket;

import android.content.Context;
import android.util.Log;

import com.sjjd.wyl.baseandroid.utils.IConfigs;
import com.sjjd.wyl.baseandroid.utils.LogUtils;
import com.sjjd.wyl.baseandroid.utils.WifiUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wyl on 2018/11/22.
 */
public class UDPSocket {
    private static final String TAG = "UDPSocket";

    private static final int BUFFER_LENGTH = 1024;
    private byte[] receiveByte = new byte[BUFFER_LENGTH];

    private static String BROADCAST_IP = "192.168.2.135";

    // 端口号，飞鸽协议默认端口2425
    public static final int CLIENT_PORT = 8282;

    private boolean isThreadRunning = false;

    private Context mContext;
    private DatagramSocket client;
    private DatagramPacket receivePacket;

    private long lastReceiveTime = 0;
    private static final long TIME_OUT = 60 * 1000;
    private static final long HEARTBEAT_MESSAGE_DURATION = 5 * 1000;

    private ExecutorService mThreadPool;
    private Thread clientThread;
    private HeartbeatTimer timer;
    private final List<OnMessageReceiveListener> messageReceiveList;

    public UDPSocket(Context context) {

        this.mContext = context;

        int cpuNumbers = Runtime.getRuntime().availableProcessors();
        // 根据CPU数目初始化线程池
        mThreadPool = Executors.newFixedThreadPool(cpuNumbers * IConfigs.POOL_SIZE);
        // 记录创建对象时的时间
        lastReceiveTime = System.currentTimeMillis();

        messageReceiveList = new ArrayList<>();

        LogUtils.e(TAG, "创建 UDP 对象");
//        createUser();
    }

    public void addOnMessageReceiveListener(OnMessageReceiveListener listener) {
        messageReceiveList.add(listener);
    }



    public void startUDPSocket() {
        if (client != null) return;
        try {
            // 表明这个 Socket 在设置的端口上监听数据。
            client = new DatagramSocket(CLIENT_PORT);
            client.setReuseAddress(true);
            if (receivePacket == null) {
                // 创建接受数据的 packet
                receivePacket = new DatagramPacket(receiveByte, BUFFER_LENGTH);
            }

            startSocketThread();
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    /**
     * 开启接收数据的线程
     */
    private void startSocketThread() {
        clientThread = new Thread(new Runnable() {
            @Override
            public void run() {
                receiveMessage();
            }
        });
        isThreadRunning = true;
        clientThread.start();
        LogUtils.e(TAG, "开启 UDP 数据接收线程");

        startHeartbeatTimer();
    }

    /**
     * 处理接受到的消息
     */
    private void receiveMessage() {
        while (isThreadRunning) {
            try {
                if (client != null) {
                    client.receive(receivePacket);
                }
                lastReceiveTime = System.currentTimeMillis();
                LogUtils.e(TAG, "receive packet success...");
            } catch (IOException e) {
                Log.e(TAG, "UDP数据包接收失败！线程停止");
                stopUDPSocket();
                e.printStackTrace();
                return;
            }

            if (receivePacket == null || receivePacket.getLength() == 0) {
                Log.e(TAG, "无法接收UDP数据或者接收到的UDP数据为空");
                continue;
            }

            String strReceive = new String(receivePacket.getData(), receivePacket.getOffset(), receivePacket.getLength());
            LogUtils.e(TAG, strReceive + " from " + receivePacket.getAddress().getHostAddress() + ":" + receivePacket.getPort());

            //解析接收到的 json 信息
            notifyMessageReceive(strReceive);
            // 每次接收完UDP数据后，重置长度。否则可能会导致下次收到数据包被截断。
            if (receivePacket != null) {
                receivePacket.setLength(BUFFER_LENGTH);
            }
        }
    }

    /**
     * 将消息通过接口发送到每个页面
     *
     * @param strReceive
     */
    private void notifyMessageReceive(String strReceive) {
        for (OnMessageReceiveListener listener : messageReceiveList) {
            if (listener != null) {
                listener.onMessageReceived(strReceive);
            }
        }
    }

    public void stopUDPSocket() {
        isThreadRunning = false;
        receivePacket = null;
        stopHeartbeatTimer();
        if (clientThread != null) {
            clientThread.interrupt();
        }
        if (mThreadPool != null) {
            mThreadPool.shutdown();
        }
        if (client != null) {
            client.close();
            client = null;
        }
        if (timer != null) {
            timer.exit();
        }
    }

    /**
     * 启动心跳，timer 间隔十秒
     */
    public void startHeartbeatTimer() {
        if (timer == null) {
            timer = new HeartbeatTimer();
        }
        timer.setOnScheduleListener(new HeartbeatTimer.OnScheduleListener() {
            @Override
            public void onSchedule() {
                LogUtils.e(TAG, "timer is onSchedule...");
                long duration = System.currentTimeMillis() - lastReceiveTime;
                LogUtils.e(TAG, "duration:" + duration);
                if (duration > TIME_OUT) {//若超过两分钟都没收到我的心跳包，则认为对方不在线。
                    LogUtils.e(TAG, "超时，对方已经下线");
                    // 刷新时间，重新进入下一个心跳周期
                    lastReceiveTime = System.currentTimeMillis();
                } else if (duration > HEARTBEAT_MESSAGE_DURATION) {//若超过十秒他没收到我的心跳包，则重新发一个。
                    JSONObject jsonObject = new JSONObject();
                    try {
                        jsonObject.put(IConfigs.TYPE, IConfigs.HEARTBREAK);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    sendMessage(jsonObject.toString());
                }
            }

        });
        timer.startTimer(0, 1000 * 5);
    }

    public void stopHeartbeatTimer() {
        if (timer != null) {
            timer.exit();
            timer = null;
        }
    }

    /**
     * 发送心跳包
     *
     * @param message
     */
    public void sendMessage(final String message) {
        mThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    BROADCAST_IP = WifiUtil.getBroadcastAddress();
                    LogUtils.e(TAG, "BROADCAST_IP:" + BROADCAST_IP);
                    InetAddress targetAddress = InetAddress.getByName(BROADCAST_IP);

                    DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), targetAddress, CLIENT_PORT);

                    client.send(packet);

                    // 数据发送事件
                    LogUtils.e(TAG, "数据发送成功");

                } catch (UnknownHostException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });
    }
}
