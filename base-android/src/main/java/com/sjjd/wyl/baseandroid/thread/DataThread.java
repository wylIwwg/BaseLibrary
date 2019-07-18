package com.sjjd.wyl.baseandroid.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.model.Response;
import com.sjjd.wyl.baseandroid.utils.Configs;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;

/**
 * Created by wyl on 2018/4/24.
 */

public class DataThread<T> extends BaseThread {


    private Class<T> clazz;
    private String url = "";
    private int what = 0;//自定义的what

    public void setClazz(Class<T> clazz) {
        this.clazz = clazz;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public DataThread(Handler handler, Context context, Class<T> clazz, HashMap<String, Object> parms) {
        super(handler, context);
        this.clazz = clazz;
        url = (String) parms.get("url");
        what = (int) parms.get("what");
    }

    public DataThread(Handler handler, Context context, Class<T> clazz, String url, int what) {
        super(handler, context);
        this.clazz = clazz;
        this.url = url;
        this.what = what;
    }


    @Override
    protected void initData() {

        if (clazz.getName().equals(String.class.getName())) {
            OkGo.<String>get(url)
                    .tag(this)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String t = response.body();
                            if (t != null) {
                                Message msg = Message.obtain();
                                msg.what = what > 0 ? what : Configs.NET_LOAD_DATA_SUCCESS;
                                msg.obj = t;
                                mHandler.sendMessage(msg);
                            } else {
                                mHandler.sendEmptyMessage(Configs.NET_LOAD_DATA_FAILED);
                            }
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Throwable mException = response.getException();
                            if (mException != null) {
                                mException.printStackTrace();
                            }
                            Message error = Message.obtain();
                            if (mException != null) {

                                if (mException instanceof SocketTimeoutException) {
                                    error.what = Configs.NET_TIMEOUT;
                                    error.obj = "网络连接超时" + mException.getMessage();
                                } else if (mException instanceof UnknownHostException || mException instanceof ConnectException) {
                                    error.what = Configs.NET_CONNECT_ERROR;
                                    error.obj = "域名解析失败，请检查网络是否连接，或域名是否存在！" + mException.getMessage();
                                } else if (mException instanceof HttpException) {
                                    error.what = Configs.NET_SERVER_ERROR;
                                    error.obj = "服务器异常！";
                                } else {
                                    error.what = Configs.NET_UNKNOWN_ERROR;
                                    error.obj = "未知错误！" + mException.getMessage();
                                }
                            } else {
                                error.what = Configs.NET_UNKNOWN_ERROR;
                                error.obj = "未知错误！";

                            }
                            mHandler.sendMessage(error);
                        }
                    });
        } else {
            OkGo.<T>get(url)
                    .tag(this)
                    .execute(new JsonCallBack<T>(clazz) {
                        @Override
                        public void onSuccess(Response<T> response) {
                            T t = response.body();
                            if (t != null) {
                                Message msg = Message.obtain();

                                msg.what = what > 0 ? what : Configs.NET_LOAD_DATA_SUCCESS;
                                msg.obj = t;
                                mHandler.sendMessage(msg);
                            } else {
                                mHandler.sendEmptyMessage(Configs.NET_LOAD_DATA_FAILED);
                            }

                        }

                        @Override
                        public void onError(Response<T> response) {
                            super.onError(response);
                            Throwable mException = response.getException();
                            if (mException != null) {
                                mException.printStackTrace();
                            }
                            Message error = Message.obtain();
                            if (mException != null) {

                                if (mException instanceof SocketTimeoutException) {
                                    error.what = Configs.NET_TIMEOUT;
                                    error.obj = "网络连接超时" + mException.getMessage();
                                } else if (mException instanceof UnknownHostException || mException instanceof ConnectException) {
                                    error.what = Configs.NET_CONNECT_ERROR;
                                    error.obj = "域名解析失败，请检查网络是否连接，或域名是否存在！" + mException.getMessage();
                                } else if (mException instanceof HttpException) {
                                    error.what = Configs.NET_SERVER_ERROR;
                                    error.obj = "服务器异常！";
                                } else {
                                    error.what = Configs.NET_UNKNOWN_ERROR;
                                    error.obj = "未知错误！" + mException.getMessage();
                                }
                            } else {
                                error.what = Configs.NET_UNKNOWN_ERROR;
                                error.obj = "未知错误！";

                            }
                            mHandler.sendMessage(error);

                        }
                    });
        }

    }
}
