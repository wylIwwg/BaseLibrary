package com.sjjd.wyl.baseandroid.thread;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.SystemClock;

import com.lzy.okgo.OkGo;
import com.sjjd.wyl.baseandroid.tools.IConfigs;


/**
 * Created by wyl on 2018/4/24.
 */

public abstract class BaseThread extends Thread {

    public String TAG = this.getClass().getSimpleName();
    public boolean loop = true;//是否循环
    public boolean pause = false;//是否暂停

    public int load_times = 0;//默认请求几次网络

    public long sleep_time = 1000 * 2;//默认2秒请求一次
    public int call_times = 0;//网络状态异常请求次数
    public int CALL_TIMES = 3;

    public Handler mHandler;//通知线程
    public Context mContext;

    private Object lock = new Object();

    public BaseThread(Handler handler, Context context) {
        mHandler = handler;
        mContext = context;
    }

    public void OnPause() {
        pause = true;
    }

    public void OnResume() {
        pause = false;
        synchronized (lock) {
            lock.notify();
        }
    }

    private void onPause() {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {

        while (loop) {

            if (pause) {//是否阻塞线程
                onPause();
            }
            if (load_times == 1) {//是否只需要请求一次网络  默认一直请求
                loop = false;
            }
            if (isNetworkConnected()) {
                call_times = 0;
                initData();//网络请求

            } else {
                call_times++;
                if (call_times > CALL_TIMES) {
                    mHandler.sendEmptyMessage(IConfigs.NET_CONNECT_ERROR);
                }
            }
            SystemClock.sleep(sleep_time);//沉睡sleep time
        }

    }

    protected abstract void initData();

    /**
     * 判断网络是否链接
     *
     * @return
     */
    protected boolean isNetworkConnected() {
        ConnectivityManager connMgr =
                (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public void onDestroy() {
        call_times = 0;
        pause = true;
        loop = false;
        OkGo.getInstance().cancelTag(this);
    }
}
