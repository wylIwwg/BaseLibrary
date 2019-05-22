package com.sjjd.wyl.baseandroid.base;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;

import com.sjjd.wyl.baseandroid.thread.I;

import java.lang.ref.WeakReference;

/**
 * Created by wyl on 2019/5/22.
 */
public class BaseDataHandler extends Handler {
    WeakReference<Activity> mReference;

    public interface ErrorListener {
        void showError(String error);

        void userHandler(Message msg);
    }

    public ErrorListener mErrorListener;

    public void setErrorListener(ErrorListener errorListener) {
        mErrorListener = errorListener;
    }

    public BaseDataHandler(Activity reference) {
        mReference = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        //常用消息处理
        switch (msg.what) {
            case I.NET_ERROR:
            case I.SERVER_ERROR:
            case I.UNKNOWN_ERROR:
            case I.TIMEOUT:
                if (mErrorListener != null)
                    mErrorListener.showError(msg.obj == null ? "处理异常" : (String) msg.obj);
                break;

        }
        //自定义消息处理
        if (mErrorListener != null)
            mErrorListener.userHandler(msg);
    }
}
