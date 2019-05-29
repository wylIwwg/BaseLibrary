package com.sjjd.wyl.baseandroid.bean;

/**
 * Created by wyl on 2019/5/29.
 */
public class BaseBean {
    public String state;
    public Object data;
    public String msg;

    public String getState() {
        return state == null ? "" : state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMsg() {
        return msg == null ? "" : msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
