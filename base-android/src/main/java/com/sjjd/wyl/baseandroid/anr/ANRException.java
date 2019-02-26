package com.sjjd.wyl.baseandroid.anr;

import android.os.Environment;
import android.os.Looper;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wyl on 2019/2/26.
 */
public class ANRException extends RuntimeException {
    public ANRException() {
        super("ANR...");
        Thread mThread = Looper.getMainLooper().getThread();
        setStackTrace(mThread.getStackTrace());
        writeFile(mThread.getStackTrace());
    }

    private void writeFile(final StackTraceElement[] stackTrace) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/sjjd/anr/");
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String name = new SimpleDateFormat("yyyy-MM-dd HH-mm-ss").format(new Date(System.currentTimeMillis())) + ".txt";
                File anr = new File(dir, name);
                try {
                    FileOutputStream fos = new FileOutputStream(anr);
                    for (StackTraceElement trace : stackTrace) {
                        byte[] b = (trace.toString() + "\n").getBytes();
                        fos.write(b);
                    }
                    fos.flush();
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

    }
}
