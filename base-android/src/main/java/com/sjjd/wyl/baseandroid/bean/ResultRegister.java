package com.sjjd.wyl.baseandroid.bean;

/**
 * Created by wyl on 2019/5/16.
 */
public class ResultRegister {
    public class Data {
        /// <summary>
        /// 注册码
        /// </summary>
        public String register_code;
        public String apply;
        public int version;

        public String getApply() {
            return apply == null ? "" : apply;
        }

        public void setApply(String apply) {
            this.apply = apply;
        }

        public int getVersion() {
            return version;
        }

        public void setVersion(int version) {
            this.version = version;
        }

        public String getRegister_code() {
            return register_code == null ? "" : register_code;
        }

        public void setRegister_code(String register_code) {
            this.register_code = register_code;
        }
    }

    /// <summary>
    /// 201
    /// </summary>
    public String code;
    /// <summary>
    /// 绑定成功
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
