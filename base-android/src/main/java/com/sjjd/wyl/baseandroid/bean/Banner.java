package com.sjjd.wyl.baseandroid.bean;

import java.io.Serializable;

/**
 * Created by wyl on 2019/3/26.
 */
public class Banner implements Serializable {
    private String url;
    private String title;
    private String id;
    private String type;
    private int resId;

    public String getType() {
        return type == null ? "" : type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }

    public String getUrl() {
        return url == null ? "" : url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTitle() {
        return title == null ? "" : title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return id == null ? "" : id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
