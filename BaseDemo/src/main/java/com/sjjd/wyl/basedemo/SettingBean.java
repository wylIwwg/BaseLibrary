package com.sjjd.wyl.basedemo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyl on 2019/8/31.
 */
public class SettingBean {
    private int state;
    private String message;
    private List<Data> data;


    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public String getMessage() {
        return message == null ? "" : message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Data> getData() {
        if (data == null) {
            return new ArrayList<>();
        }
        return data;
    }

    public void setData(List<Data> data) {
        this.data = data;
    }

    public static class Sublevel {

        private int id;
        private String Name;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getName() {
            return Name == null ? "" : Name;
        }

        public void setName(String name) {
            Name = name;
        }
    }

    public static class Data {

        private int id;
        private String departName;
        private List<Sublevel> sublevel;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getDepartName() {
            return departName == null ? "" : departName;
        }

        public void setDepartName(String departName) {
            this.departName = departName;
        }

        public List<Sublevel> getSublevel() {
            if (sublevel == null) {
                return new ArrayList<>();
            }
            return sublevel;
        }

        public void setSublevel(List<Sublevel> sublevel) {
            this.sublevel = sublevel;
        }
    }
}