package com.sjjd.wyl.baseandroid.thread;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by wyl on 2018/4/23.
 */

public class TimeThread extends BaseThread {

    private SimpleDateFormat mDateFormat;
    private SimpleDateFormat mTimeFormat;
    private SimpleDateFormat mWeekFormat;
    HashMap<String, String> result;

    public TimeThread(Context context, Handler handler) {
        this(context, handler, "yyyy年MM月dd日", "HH:mm:ss", "EEEE");
    }

    public TimeThread(Context context, Handler handler, String dateFormat, String timeFormat, String weekFormat) {
        super(handler, context);
        result = new HashMap<>();
        mTimeFormat = new SimpleDateFormat(timeFormat, Locale.CHINA);
        mDateFormat = new SimpleDateFormat(dateFormat, Locale.CHINA);
        mWeekFormat = new SimpleDateFormat(weekFormat, Locale.CHINA);

    }


    /**
     * long time=System.currentTimeMillis();
     * Date date=new Date(time);
     * SimpleDateFormat format=new SimpleDateFormat(yyyy年MM月dd日 HH时mm分ss秒 EEEE);
     * Log.e(time,time1=+format.format(date));
     * format=new SimpleDateFormat(yyyy-MM-dd HH:mm:ss);
     * Log.e(time,time2=+format.format(date));
     * format=new SimpleDateFormat(yyyy/MM/dd);
     * Log.e(time,time3=+format.format(date));
     * format=new SimpleDateFormat(HH:mm:ss);
     * Log.e(time,time4=+format.format(date));
     * format=new SimpleDateFormat(EEEE);
     * Log.e(time,time5=+format.format(date));
     * format=new SimpleDateFormat(E);
     * Log.e(time,time6=+format.format(date));
     */


    @Override
    protected void initData() {

        long l = System.currentTimeMillis();

        Date mDate = new Date(l);
        //时间
        String timeStr = mTimeFormat.format(mDate);
        //日期
        String dateStr = mDateFormat.format(mDate);
        //星期
        String week = mWeekFormat.format(mDate);

        result.put("week", week);
        result.put("date", dateStr);
        result.put("time", timeStr);

        Message mMessage = Message.obtain();
        mMessage.obj = result;
        mMessage.what = I.TIME_CHANGED;
        mHandler.sendMessage(mMessage);


    }
}
