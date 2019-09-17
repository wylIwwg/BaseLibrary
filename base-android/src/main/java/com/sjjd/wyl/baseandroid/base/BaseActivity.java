package com.sjjd.wyl.baseandroid.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sjjd.wyl.baseandroid.R;
import com.sjjd.wyl.baseandroid.bean.Register;
import com.sjjd.wyl.baseandroid.register.RegisterUtils;
import com.sjjd.wyl.baseandroid.thread.JsonCallBack;
import com.sjjd.wyl.baseandroid.utils.Configs;
import com.sjjd.wyl.baseandroid.utils.DisplayUtil;
import com.sjjd.wyl.baseandroid.utils.LogUtils;
import com.sjjd.wyl.baseandroid.utils.ToastUtils;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BaseActivity extends AppCompatActivity implements BaseDataHandler.MessageListener {
    public String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public LinearLayout mBaseLlRoot;//根布局
    public BaseDataHandler mDataHandler;
    public String HOST = "";
    public boolean isRegistered = false;
    public String MARK = "";//软件类型

    public String REGISTER_STR = "";
    public String Client_ID = "";
    public int RegisterCode = 0;
    public String METHOD_AREA;
    public String[] PERMISSIONS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseLlRoot = findViewById(R.id.baseLlRoot);
        mDataHandler = new BaseDataHandler(this);
        mDataHandler.setErrorListener(this);

    }

    public void hasPermission() {
        if (PERMISSIONS != null && PERMISSIONS.length > 0) {
            if (AndPermission.hasPermissions(mContext, PERMISSIONS)) {
                initData();
            } else {
                AndPermission.with(mContext)
                        .runtime()
                        .permission(PERMISSIONS)
                        .onGranted(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                initData();
                            }
                        })
                        .onDenied(new Action<List<String>>() {
                            @Override
                            public void onAction(List<String> data) {
                                showError("权限请求被拒绝将无法正常使用！");
                            }
                        })
                        .start();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayUtil.hideBottomUIMenu(this);
    }

    public void initData() {

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {

        if (mBaseLlRoot == null) return;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseLlRoot.addView(view, lp);
    }

    @Override
    public void showError(String error) {
        Toasty.error(mContext, error, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void userHandler(Message msg) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
        }
        //ToastUtils.clearToast();
    }

    public void isDeviceRegistered() {

        int mRegistered = RegisterUtils.getInstance(mContext).isDeviceRegistered();

        LogUtils.e(TAG, "onCreate: " + mRegistered);
        switch (mRegistered) {
            case Configs.REGISTER_FORBIDDEN://禁止注册/未注册
                //请求注册
                //请求信息密文
                REGISTER_STR = RegisterUtils.getInstance(mContext).register2Base64(false, MARK);
                RegisterCode = Configs.DEVICE_FORBIDDEN;
                break;
            case Configs.REGISTER_FOREVER://永久注册
                RegisterCode = Configs.DEVICE_REGISTERED;
                isRegistered = true;
                break;
            default://注册时间
                Register mRegister = RegisterUtils.getInstance(mContext).getRegister();
                if (mRegister != null) {
                    isRegistered = true;
                    RegisterCode = Configs.DEVICE_REGISTERED;
                    String mDate = mRegister.getDate();//获取注册时间
                    long rt = Long.parseLong(mDate);
                    long mMillis = System.currentTimeMillis();//本地时间

                    Date newDate2 = new Date(rt + (long) mRegistered * 24 * 60 * 60 * 1000);

                    //到期了 再次申请注册
                    if (newDate2.getTime() < mMillis) {
                        ToastUtils.showToast(mContext, "设备注册已过期！", 2000);
                        RegisterCode = Configs.DEVICE_OUTTIME;
                        REGISTER_STR = RegisterUtils.getInstance(mContext).register2Base64(false, MARK);
                        isRegistered = false;
                    }

                }

                break;
        }
    }

    public void addDevice(String method, String content) {
        OkGo.<String>post(HOST)
                .params("content", content)
                .params("checkinfo", "{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"token\":\"" + System.currentTimeMillis() + "\"}")
                .params("method", method)
                .tag(this).execute(new JsonCallBack<String>(String.class) {
            @Override
            public void onSuccess(Response<String> response) {
                LogUtils.e(TAG, "onSuccess: " + response.body());
                addDeviceResult(response.body());

            }

            @Override
            public void onError(Response<String> response) {
                super.onError(response);
                LogUtils.e(TAG, "onError: " + response.body());
                ToastUtils.showToast(mContext, "初始化失败！", 2000);
            }
        });
    }

    public void addDeviceResult(String body) {
        LogUtils.e(TAG, "addDeviceResult: " + body);
    }


}
