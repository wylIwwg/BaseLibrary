package com.sjjd.wyl.baseandroid.thread;

import android.content.Context;
import android.os.Handler;

import com.sjjd.wyl.baseandroid.tools.IConfigs;
import com.sjjd.wyl.baseandroid.tools.ToolLog;

import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by wyl on 2019/3/13.
 */
public class NetThread extends BaseThread {

    public String url;//检测当前链接是否能正常访问

    public void setUrl(String url) {
        this.url = url;
    }

    public NetThread(Handler handler, Context context, String url) {
        super(handler, context);
        this.url = url;
    }

    @Override
    protected void initData() {
        ToolLog.e(TAG, "initData: " + url);
        try {

            if (isConnected(url)) {
                mHandler.sendEmptyMessage(IConfigs.NET_URL_SUCCESS);
            } else {
                mHandler.sendEmptyMessage(IConfigs.NET_URL_ERROR);
            }
        } catch (Exception e) {
            ToolLog.e(TAG, "initData: " + e.getMessage());
        }

    }


    public synchronized boolean isConnected(String urlStr) {
        if (urlStr == null || urlStr.length() <= 0) {
            return false;
        }
        URL url;
        HttpURLConnection con;
        int state;
        try {

            url = new URL(urlStr);
            con = (HttpURLConnection) url.openConnection();
            state = con.getResponseCode();
            con.disconnect();
            con = null;
            url = null;

            ToolLog.e(TAG, "isConnect:  state = " + state);
            if (state == 200) {
                return true;
            }

        } catch (Exception ex) {
            ToolLog.e(TAG, "isConnect: Exception = " + ex.getMessage());
            con = null;
            url = null;
            return false;
        }
        return false;
    }
}
