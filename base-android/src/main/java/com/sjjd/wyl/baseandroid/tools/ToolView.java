package com.sjjd.wyl.baseandroid.tools;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.util.Base64;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by wyl on 2020/3/17.
 */
public class ToolView {
    public static String getBitmapString(View view) {

        String bitmapString = "";
        view.setDrawingCacheEnabled(true);

        view.buildDrawingCache();

        Bitmap bitmap = view.getDrawingCache();

        if (bitmap != null) {
            Bitmap copy = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            Bitmap result = ScaleBitmap(copy, 0.5f, 0.5f);
            bitmapString = bitmapToBase64(result);

        } else {
            bitmapString = "";
        }
        view.setDrawingCacheEnabled(false);
        return bitmapString;

    }


    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private static Bitmap ScaleBitmap(Bitmap bitmap, float x, float y) {
        Matrix matrix = new Matrix();
        matrix.postScale(x, y); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }
}
