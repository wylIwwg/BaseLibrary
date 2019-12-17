package com.sjjd.wyl.baseandroid.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sjjd.wyl.baseandroid.R;
import com.sjjd.wyl.baseandroid.bean.Register;
import com.sjjd.wyl.baseandroid.register.RegisterUtils;
import com.sjjd.wyl.baseandroid.socket.SocketManager;
import com.sjjd.wyl.baseandroid.thread.JsonCallBack;
import com.sjjd.wyl.baseandroid.utils.IConfigs;
import com.sjjd.wyl.baseandroid.utils.DisplayUtil;
import com.sjjd.wyl.baseandroid.utils.LogUtils;
import com.sjjd.wyl.baseandroid.utils.SPUtils;
import com.sjjd.wyl.baseandroid.utils.ToastUtils;
import com.sjjd.wyl.baseandroid.view.MEditView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BaseRegisterActivity extends AppCompatActivity implements BaseDataHandler.MessageListener {
    public String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public BaseDataHandler mDataHandler;
    public String HOST = "";
    public boolean isRegistered = false;
    public String MARK = "";//软件类型

    public String REGISTER_STR = "";
    public int RegisterCode = 0;
    public String[] PERMISSIONS;

    public MEditView mEtServerIp;
    public MEditView mEtServerPort;
    public Button mBtnConnect;
    public LinearLayout mLlSettingServer;
    public LinearLayout mLlSettingArea;
    public LinearLayout mLlBaseRegister;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base_register);
        mContext = this;

        mLlBaseRegister = findViewById(R.id.llBaseRegister);
        mLlSettingArea = findViewById(R.id.llSettingArea);
        mLlSettingServer = findViewById(R.id.llSettingServer);

        mEtServerIp = findViewById(R.id.etServerIp);
        mEtServerPort = findViewById(R.id.etServerPort);
        mBtnConnect = findViewById(R.id.btnConnect);


        mDataHandler = new BaseDataHandler(this);
        mDataHandler.setMessageListener(this);

        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //开始连接服务器
                connectServer();
            }
        });

        //检测是否有设置
        String port = SPUtils.init(mContext).getDIYString(IConfigs.SP_PORT);
        String ip = SPUtils.init(mContext).getDIYString(IConfigs.SP_IP);
        mEtServerIp.setText(ip);
        mEtServerPort.setText(port);


        //检测是否有注册

        //

    }

    private void connectServer() {
        mBtnConnect.setEnabled(false);
        String port = mEtServerPort.getText().toString();
        String ip = mEtServerIp.getText().toString();
        SocketManager.getInstance(mContext).setHandler(mDataHandler).startTcpConnection(ip, port, "{\"type\":\"ping\"}");

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

    public void initData() {

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {

        if (mLlBaseRegister == null) return;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mLlBaseRegister.addView(view, lp);
    }

    @Override
    public void showError(String error) {
        Toasty.error(mContext, error, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void userHandler(Message msg) {
        switch (msg.what) {
            case IConfigs.MSG_SOCKET_RECEIVED://处理socket消息
                String message = (String) msg.obj;
                if (message != null && message.contains("client")) {
                    //包含clientid说明连接成功
                    mBtnConnect.setEnabled(true);
                }
                break;

            case IConfigs.MSG_CREATE_TCP_ERROR:
            case IConfigs.MSG_PING_TCP_TIMEOUT:
                mBtnConnect.setEnabled(true);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayUtil.hideBottomUIMenu(this);
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
            case IConfigs.REGISTER_FORBIDDEN://禁止注册/未注册
                //请求注册
                //请求信息密文
                REGISTER_STR = RegisterUtils.getInstance(mContext).register2Base64(false, MARK);
                RegisterCode = IConfigs.DEVICE_FORBIDDEN;
                break;
            case IConfigs.REGISTER_FOREVER://永久注册
                RegisterCode = IConfigs.DEVICE_REGISTERED;
                isRegistered = true;
                break;
            default://注册时间
                Register mRegister = RegisterUtils.getInstance(mContext).getRegister();
                if (mRegister != null) {
                    isRegistered = true;
                    RegisterCode = IConfigs.DEVICE_REGISTERED;
                    String mDate = mRegister.getDate();//获取注册时间
                    long rt = Long.parseLong(mDate);
                    long mMillis = System.currentTimeMillis();//本地时间

                    Date newDate2 = new Date(rt + (long) mRegistered * 24 * 60 * 60 * 1000);

                    //到期了 再次申请注册
                    if (newDate2.getTime() < mMillis) {
                        ToastUtils.showToast(mContext, "设备注册已过期！", 2000);
                        RegisterCode = IConfigs.DEVICE_OUTTIME;
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
                    IBinder token = view.getWindowToken();
                    InputMethodManager inputMethodManager = (InputMethodManager)
                            mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (inputMethodManager != null)
                        inputMethodManager.hideSoftInputFromWindow(token,
                                InputMethodManager.HIDE_NOT_ALWAYS);

                    DisplayUtil.hideBottomUIMenu(this);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
