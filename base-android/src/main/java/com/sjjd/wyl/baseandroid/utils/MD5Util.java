package com.sjjd.wyl.baseandroid.utils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Created by wyl on 2018/4/24.
 */

public class MD5Util {

    /**
     * md5加密
     *
     * @return
     */
    public static String getMD5(String str) {

        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
            md5.reset();
            md5.update(str.getBytes("UTF-8"));

            byte[] encryption = md5.digest();
            StringBuffer buffer = new StringBuffer();

            for (int i = 0; i < encryption.length; i++) {
                if (Integer.toHexString(0xff & encryption[i]).length() == 1) {
                    buffer.append("0").append(Integer.toHexString(0xff & encryption[i]));
                } else {
                    buffer.append(Integer.toHexString(0xff & encryption[i]));
                }
            }

            return buffer.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String MD5EncodeLowercase(String s) {
        return getMD5(s).toLowerCase();

    }

    public static String MD5EncodeUpper(String temp) {
        return getMD5(temp).toUpperCase();
    }
}
