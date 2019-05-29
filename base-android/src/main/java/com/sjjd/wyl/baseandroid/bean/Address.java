package com.sjjd.wyl.baseandroid.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wyl on 2019/5/16.
 */
public class Address {
    public class Unit {
        /// <summary>
        /// Unitid
        /// </summary>
        public int unitid;

        /// <summary>
        /// 垫江政务中心
        /// </summary>
        public String name;

        public int getUnitid() {
            return unitid;
        }

        public void setUnitid(int unitid) {
            this.unitid = unitid;
        }

        public String getName() {
            return name == null ? "" : name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    public class Floor {
        /// <summary>
        /// Unitid
        /// </summary>
        public int unitid;

        /// <summary>
        /// 1
        /// </summary>
        public String floor;

        public int getUnitid() {
            return unitid;
        }

        public void setUnitid(int unitid) {
            this.unitid = unitid;
        }

        public String getFloor() {
            return floor == null ? "" : floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }
    }

    public class Area {
        /// <summary>
        /// Unitid
        /// </summary>
        public int unitid;

        /// <summary>
        /// 1
        /// </summary>
        public String floor;

        /// <summary>
        /// A
        /// </summary>
        public String area;

        public int getUnitid() {
            return unitid;
        }

        public void setUnitid(int unitid) {
            this.unitid = unitid;
        }

        public String getFloor() {
            return floor == null ? "" : floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getArea() {
            return area == null ? "" : area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }

    public class Window {
        /// <summary>
        /// Unitid
        /// </summary>
        public int unitid;

        /// <summary>
        /// 1
        /// </summary>
        public String floor;

        /// <summary>
        /// A
        /// </summary>
        public String area;
        public String windownum;

        public String getWindownum() {
            return windownum == null ? "" : windownum;
        }

        public void setWindownum(String windownum) {
            this.windownum = windownum;
        }

        public int getUnitid() {
            return unitid;
        }

        public void setUnitid(int unitid) {
            this.unitid = unitid;
        }

        public String getFloor() {
            return floor == null ? "" : floor;
        }

        public void setFloor(String floor) {
            this.floor = floor;
        }

        public String getArea() {
            return area == null ? "" : area;
        }

        public void setArea(String area) {
            this.area = area;
        }
    }

    public class Data {
        /// <summary>
        /// Unit
        /// </summary>
        public List<Unit> unit;

        /// <summary>
        /// Floor
        /// </summary>
        public List<Floor> floor;

        /// <summary>
        /// Area
        /// </summary>
        public List<Area> area;
        public List<Window> window;

        public List<Window> getWindow() {
            if (window == null) {
                return new ArrayList<>();
            }
            return window;
        }

        public void setWindow(List<Window> window) {
            this.window = window;
        }

        public List<Unit> getUnit() {
            if (unit == null) {
                return new ArrayList<>();
            }
            return unit;
        }

        public void setUnit(List<Unit> unit) {
            this.unit = unit;
        }

        public List<Floor> getFloor() {
            if (floor == null) {
                return new ArrayList<>();
            }
            return floor;
        }

        public void setFloor(List<Floor> floor) {
            this.floor = floor;
        }

        public List<Area> getArea() {
            if (area == null) {
                return new ArrayList<>();
            }
            return area;
        }

        public void setArea(List<Area> area) {
            this.area = area;
        }
    }

    /// <summary>
    /// 1
    /// </summary>
    public String code;


    /// <summary>
    /// 数据获取成功
    /// </summary>
    public String msg;


    /// <summary>
    /// Data
    /// </summary>
    public Data data;

    public String getCode() {
        return code == null ? "" : code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg == null ? "" : msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }
}
