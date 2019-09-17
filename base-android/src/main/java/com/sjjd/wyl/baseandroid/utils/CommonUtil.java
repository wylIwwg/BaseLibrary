package com.sjjd.wyl.baseandroid.utils;

/**
 * Created by wyl on 2019/9/2.
 */
public class CommonUtil {

    //姓名星号处理
    public static String SplitStarName(String name, String ch, int start, int end) {
        String result = "";
        if (name.length() > 1)
            result = name.replaceFirst(name.substring(start, end), ch);
        else result = name;
        return result;
    }
}
