package com.sjjd.wyl.baseandroid.thread;

import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.exception.HttpException;
import com.lzy.okgo.model.Response;
import com.sjjd.wyl.baseandroid.utils.Configs;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

/**
 * Created by wyl on 2018/4/24.
 */

public class SingleThread<T> {


    private static SingleThread instance;
    private Class<T> clazz;
    private int what = Configs.NET_LOAD_DATA_SUCCESS;
    private String url = null;

    public SingleThread(Class<T> clazz) {
        this.clazz = clazz;
    }

    public static <T> SingleThread getInstance(Class<T> tClass) {
        instance = new SingleThread<>(tClass);
        return instance;

    }


    public SingleThread what(int what) {
        this.what = what;
        return this;
    }

    public SingleThread url(String url) {
        this.url = url;
        return this;
    }

    public void excute(@NonNull final Handler handler) {
        if (url == null) {
            throw new RuntimeException("url 不能为空！");
        }
        if (clazz.getName().equals(String.class.getName())) {
            OkGo.<String>get(url)
                    .tag(this)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            String t = response.body();
                            if (t != null) {
                                Message msg = Message.obtain();
                                msg.what = what;
                                msg.obj = t;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendEmptyMessage(Configs.NET_LOAD_DATA_FAILED);
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
                            handler.sendMessage(error);
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

                                msg.what = what;
                                msg.obj = t;
                                handler.sendMessage(msg);
                            } else {
                                handler.sendEmptyMessage(Configs.NET_LOAD_DATA_FAILED);
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
                            if (mException == null) {
                                error.what = Configs.NET_UNKNOWN_ERROR;
                                error.obj = "未知错误！";
                            } else {

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
                            }
                            handler.sendMessage(error);

                        }
                    });
        }

    }

}
