package com.sjjd.wyl.baseandroid.view;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.IBinder;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.sjjd.wyl.baseandroid.utils.DisplayUtil;

/**
 * Created by wyl on 2019/10/11.
 * 自定义弹窗dialog
 * 弹窗时禁止导航栏出现
 * 点击dialog非editview区域隐藏软键盘
 */
public class TipsDialog extends Dialog {

    Context mContext;

    public TipsDialog(Context context) {
        super(context);
        mContext = context;
    }

    public TipsDialog(Context context, int theme) {
        super(context, theme);
        mContext = context;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                View view = getCurrentFocus();
                hideKeyboard(ev, view);//调用方法判断是否需要隐藏键盘
                break;

            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    public void hideKeyboard(MotionEvent event, View view) {
        try {
            if (view != null && view instanceof EditText) {
                int[] location = {0, 0};
                view.getLocationInWindow(location);
                int left = location[0], top = location[1], right = left
                        + view.getWidth(), bootom = top + view.getHeight();
                // 判断焦点位置坐标是否在空间内，如果位置在控件外，则隐藏键盘
                if (event.getRawX() < left || event.getRawX() > right
                        || event.getY() < top || event.getRawY() > bootom) {
                    // 隐藏键盘
                    hideKeyboard(view);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void hideKeyboard(View view) {
        IBinder token = view.getWindowToken();
        InputMethodManager inputMethodManager = (InputMethodManager)
                mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null)
            inputMethodManager.hideSoftInputFromWindow(token,
                    InputMethodManager.HIDE_NOT_ALWAYS);

        //隐藏导航栏
        DisplayUtil.hideBottomUIMenu((Activity) mContext);
    }


    @Override
    public void show() {
        //在show之前添加禁止获取焦点
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        super.show();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_FULLSCREEN;
            this.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
        }
        //在show之后取消禁止获取焦点属性，否则会导致dialog无法处理点击
        this.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
    }


}
