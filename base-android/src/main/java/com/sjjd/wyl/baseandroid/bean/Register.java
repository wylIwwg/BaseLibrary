package com.sjjd.wyl.baseandroid.bean;

/**
 * Created by wyl on 2019/4/1.
 */
public class Register {
    String identity;//标识 如MAC值
    String date;//注册时间
    String limit;//注册限制/天数
    String mark;//标记


    public String getIdentity() {
        return identity == null ? "" : identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public String getDate() {
        return date == null ? "" : date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLimit() {
        return limit == null ? "" : limit;
    }

    public void setLimit(String limit) {
        this.limit = limit;
    }

    public String getMark() {
        return mark == null ? "" : mark;
    }

    public void setMark(String mark) {
        this.mark = mark;
    }
}
