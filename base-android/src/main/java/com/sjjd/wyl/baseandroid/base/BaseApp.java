package com.sjjd.wyl.baseandroid.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.sjjd.wyl.baseandroid.R;
import com.sjjd.wyl.baseandroid.anr.ANRThread;
import com.sjjd.wyl.baseandroid.crash.config.CrashConfig;
import com.sjjd.wyl.baseandroid.tools.ToolApp;
import com.sjjd.wyl.baseandroid.tools.ToolSP;
import com.sjjd.wyl.baseandroid.tools.ToolTts;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.runtime.Permission;

import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

/**
 * Created by wyl on 2018/11/13.
 */
public class BaseApp extends Application {

    private static final String TAG = " BaseApp ";
    public Context mContext;
    public String LOG_PATH = Environment.getExternalStorageDirectory().getAbsolutePath() + "/sjjd/log";

    @Override
    public void onCreate() {
        super.onCreate();
        new ANRThread().start();
        mContext = this;
        ToolSP.init(this);
    }


    /**
     * 初始化tts语音
     *
     * @param dir 目标文件存放路径
     */
    public void initTTs(final String dir) {
        AndPermission.with(this)
                .runtime().permission(Permission.Group
                .STORAGE).permission(Permission.READ_PHONE_STATE).onGranted(
                new Action<List<String>>() {
                    @Override
                    public void onAction(List<String> data) {
                        if (!ToolTts.getInstance(mContext).existsTTsFile(dir)) {
                            ToolTts.getInstance(mContext).copyFile();
                        }
                    }
                }
        ).onDenied(new Action<List<String>>() {
            @Override
            public void onAction(List<String> data) {
                Toasty.error(mContext, "权限拒绝，将无法播放语音", Toast.LENGTH_LONG, true).show();
            }
        }).start();
    }


    public void initDebug(Class<Activity> activity) {
        CrashConfig.Builder.create()
                .backgroundMode(CrashConfig.BACKGROUND_MODE_SILENT) //default: CrashConfig.BACKGROUND_MODE_SHOW_CUSTOM
                .enabled(true) //default: true
                .showErrorDetails(true) //default: true
                .showRestartButton(true) //default: true
                .logErrorOnRestart(true) //default: true
                .trackActivities(false) //default: false
                .minTimeBetweenCrashesMs(2000) //default: 3000
                .errorDrawable(R.drawable.error) //default: bug image
                .restartActivity(activity) //default: null (your app's launch activity)
                .errorActivity(activity) //default: null (default error activity)
                .eventListener(null) //default: null
                .apply();
    }


    public void initOkGO() {

        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        //log相关
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor("OkGo");
        //log打印级别，决定了log显示的详细程度
        loggingInterceptor.setPrintLevel(HttpLoggingInterceptor.Level.BODY);
        //log颜色级别，决定了log在控制台显示的颜色
        loggingInterceptor.setColorLevel(Level.INFO);
        //添加OkGo默认debug日志
        builder.addInterceptor(loggingInterceptor);

        //第三方的开源库，使用通知显示当前请求的log，不过在做文件下载的时候，这个库好像有问题，对文件判断不准确
        //builder.addInterceptor(new ChuckInterceptor(this));


        //超时时间设置，默认60秒
        //全局的读取超时时间
        builder.readTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的写入超时时间
        builder.writeTimeout(OkGo.DEFAULT_MILLISECONDS, TimeUnit.MILLISECONDS);
        //全局的连接超时时间
        builder.connectTimeout(5000L, TimeUnit.MILLISECONDS);


        // 其他统一的配置
        // 详细说明看GitHub文档：https://github.com/jeasonlzy/
        OkGo.getInstance().init(this)                           //必须调用初始化
                .setOkHttpClient(builder.build())               //建议设置OkHttpClient，不设置会使用默认的
                .setCacheMode(CacheMode.NO_CACHE)               //全局统一缓存模式，默认不使用缓存，可以不传
                .setCacheTime(CacheEntity.CACHE_NEVER_EXPIRE)   //全局统一缓存时间，默认永不过期，可以不传
                .setRetryCount(1)                               //全局统一超时重连次数，默认为三次，那么最差的情况会请求4次(一次原始请求，三次重连请求)，不需要可以设置为0
        ;
    }


    public void initCrashRestart() {
        Thread.setDefaultUncaughtExceptionHandler(restartHandler); // 程序崩溃时触发线程

    }

    // 创建服务用于捕获崩溃异常
    public Thread.UncaughtExceptionHandler restartHandler = new Thread.UncaughtExceptionHandler() {
        public void uncaughtException(Thread thread, Throwable ex) {
            handleException(ex);
            try {
                Thread.sleep(500 * 1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    //保存错误日志
    private void handleException(Throwable ex) {
        String s = formatCrashInfo(ex);
        saveLogFile2SDcard(s, true);
        ToolApp.restartApp(mContext);
    }


    private String formatCrashInfo(Throwable ex) {
        String lineSeparator = "\r\n";

        StringBuilder sb = new StringBuilder();
        String logTime = "logTime:" + getCurrentTime();

        Writer info = new StringWriter();
        PrintWriter printWriter = new PrintWriter(info);
        ex.printStackTrace(printWriter);

        String dump = info.toString();
        String exception = "exception:" + "{\n" + dump + "\n}";
        printWriter.close();

        sb.append("----------------------------").append(lineSeparator);
        sb.append(logTime).append(lineSeparator);
        sb.append(getPackageName()).append(lineSeparator);
        sb.append(ToolApp.getAppVersionCode(mContext, getPackageName())).append(lineSeparator);
        sb.append(exception).append(lineSeparator);
        sb.append("----------------------------").append(lineSeparator).append(lineSeparator);

        return sb.toString();

    }


    public String getCurrentTime() {
        long currentTime = System.currentTimeMillis();
        Date date = new Date(currentTime);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String time = sdf.format(date);
        return time;
    }


    public static final String LOG_SUFFIX = ".log";
    private static final String CHARSET = "UTF-8";

    public boolean saveLogFile2SDcard(String logString, boolean isAppend) {
        if (!isSDcardExsit()) {
            return false;
        }
        try {
            File logDir = new File(LOG_PATH);
            if (!logDir.exists()) {
                logDir.mkdirs();
            }
            File logFile = new File(LOG_PATH, getLogDate() + LOG_SUFFIX);
            FileOutputStream fos = new FileOutputStream(logFile, isAppend);
            fos.write(logString.getBytes(CHARSET));
            fos.close();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    private boolean isSDcardExsit() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }


    private String getLogDate() {
        long l = System.currentTimeMillis();
        SimpleDateFormat mDateFormat;
        mDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        Date mDate = new Date(l);
        //时间
        String timeStr = mDateFormat.format(mDate);
        return timeStr;
    }


    public static String getMD5Str(String str) {
        MessageDigest messageDigest = null;
        try {
            messageDigest = MessageDigest.getInstance("MD5");
            messageDigest.reset();
            messageDigest.update(str.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return "";
        }
        byte[] byteArray = messageDigest.digest();
        StringBuffer md5StrBuff = new StringBuffer();
        for (int i = 0; i < byteArray.length; i++) {
            if (Integer.toHexString(0xFF & byteArray[i]).length() == 1)
                md5StrBuff.append("0").append(Integer.toHexString(0xFF & byteArray[i]));
            else
                md5StrBuff.append(Integer.toHexString(0xFF & byteArray[i]));
        }

        return md5StrBuff.toString();
    }
}
