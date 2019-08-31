package com.sjjd.wyl.baseandroid.base;

import android.content.Context;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sjjd.wyl.baseandroid.R;
import com.sjjd.wyl.baseandroid.bean.Register;
import com.sjjd.wyl.baseandroid.register.RegisterUtils;
import com.sjjd.wyl.baseandroid.socket.SocketManager;
import com.sjjd.wyl.baseandroid.utils.Configs;
import com.sjjd.wyl.baseandroid.utils.DisplayUtil;
import com.sjjd.wyl.baseandroid.utils.LogUtils;
import com.sjjd.wyl.baseandroid.utils.SPUtils;
import com.sjjd.wyl.baseandroid.utils.ToastUtils;
import com.sjjd.wyl.baseandroid.view.LoadingView;
import com.sjjd.wyl.baseandroid.view.MEditView;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;

import java.util.Date;
import java.util.List;

import es.dmoral.toasty.Toasty;

public class BaseActivity2 extends AppCompatActivity implements BaseDataHandler.ErrorListener {
    public String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public BaseDataHandler mDataHandler;
    public String HOST = "";
    public boolean isRegistered = false;
    public String MARK = "";//软件类型

    public String REGISTER_STR = "";
    public String Client_ID = "";
    public int RegisterCode = 0;
    public String METHOD_AREA;
    public String[] PERMISSIONS;

    public LinearLayout mllContentRoot;//内容根布局
    public DrawerLayout mDrawer;//抽屉根布局
    public LinearLayout navigationView;//侧滑布局

    public MEditView mEtServerIp;
    public MEditView mEtServerPort;
    public Button mBtnConnect;

    public RelativeLayout rlLoadingRoot;
    public LoadingView mLoadingView;

    public LinearLayout mLayoutArea;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity2_base);

        mContext = this;

        mllContentRoot = findViewById(R.id.llContentRoot);
        mDrawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.llLeftDrawer);

        mEtServerIp = findViewById(R.id.etServerIp);
        mEtServerPort = findViewById(R.id.etServerPort);
        mBtnConnect = findViewById(R.id.btnConnect);

        rlLoadingRoot = findViewById(R.id.rlLoading);
        mLoadingView = findViewById(R.id.loading);

        mLayoutArea = findViewById(R.id.llArea);


        mDataHandler = new BaseDataHandler(this);
        mDataHandler.setErrorListener(this);


      /*  ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, null, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();*/

        // hasPermission();

    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    @Override
    public void setContentView(View view) {

        if (mllContentRoot == null) return;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mllContentRoot.addView(view, lp);
    }


    @Override
    public void onBackPressed() {
        if (mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        DisplayUtil.hideBottomUIMenu(this);
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

    public void initListener() {
        mBtnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectServer();
            }
        });

        mDrawer.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                String ip = SPUtils.init(mContext).getDIYString(Configs.SP_IP);
                String port = SPUtils.init(mContext).getDIYString(Configs.SP_PORT);
                mEtServerPort.setText(port);
                mEtServerIp.setText(ip);
            }

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                mLayoutArea.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void showError(String error) {
        Toasty.error(mContext, error, Toast.LENGTH_LONG, true).show();
    }

    @Override
    public void userHandler(Message msg) {
        switch (msg.what) {
            case Configs.MSG_CREATE_TCP_ERROR:
            case Configs.MSG_PING_TCP_TIMEOUT:
                mBtnConnect.setEnabled(true);
                break;
        }
    }

    public void connectServer() {
        mBtnConnect.setEnabled(false);
        final String port = mEtServerPort.getText().toString();
        final String ip = mEtServerIp.getText().toString();
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (SocketManager.getInstance(mContext).getTcpSocket().startTcpConnection(ip, Integer.valueOf(port))) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toasty.success(mContext, "服务器连接成功！", Toast.LENGTH_SHORT, true).show();
                            SPUtils.init(mContext).putDIYString(Configs.SP_PORT, mEtServerPort.getText().toString());
                            SPUtils.init(mContext).putDIYString(Configs.SP_IP, mEtServerIp.getText().toString());
                            mLayoutArea.setVisibility(View.VISIBLE);
                            mBtnConnect.setEnabled(true);
                            SocketManager.getInstance(mContext).destroy();
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mBtnConnect.setEnabled(true);

                        }
                    });
                }
            }
        }).start();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mDataHandler != null) {
            mDataHandler.removeCallbacksAndMessages(null);
        }
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
