package com.sjjd.wyl.baseandroid.thread;

import com.lzy.okgo.callback.AbsCallback;
import com.sjjd.wyl.baseandroid.utils.GsonUtil;

import java.lang.reflect.Type;

import okhttp3.Response;
import okhttp3.ResponseBody;


public abstract class JsonCallBack<T> extends AbsCallback<T> {

    private Type type;
    private Class<T> clazz;


    public JsonCallBack(Type type) {
        this.type = type;
    }

    public JsonCallBack(Class<T> clazz) {
        this.clazz = clazz;
    }

    /**
     * 该方法是子线程处理，不能做ui相关的工作
     * 主要作用是解析网络返回的 response 对象,生产onSuccess回调中需要的数据对象
     * 这里的解析工作不同的业务逻辑基本都不一样,所以需要自己实现,以下给出的时模板代码,实际使用根据需要修改
     */
    @Override
    public T convertResponse(Response response) throws Throwable {

        ResponseBody body = response.body();
        if (body == null) return null;

        T t = null;

        if (type != null)
            t = GsonUtil.fromJson(body.charStream(), type);

        if (clazz != null)
            t = GsonUtil.fromJson(body.charStream(), clazz);

        return t;

    }


}
