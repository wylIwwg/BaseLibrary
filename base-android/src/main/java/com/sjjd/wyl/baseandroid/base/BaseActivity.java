package com.sjjd.wyl.baseandroid.base;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;
import com.sjjd.wyl.baseandroid.R;
import com.sjjd.wyl.baseandroid.bean.Address;
import com.sjjd.wyl.baseandroid.bean.Register;
import com.sjjd.wyl.baseandroid.register.RegisterUtils;
import com.sjjd.wyl.baseandroid.thread.JsonCallBack;
import com.sjjd.wyl.baseandroid.utils.AppUtils;
import com.sjjd.wyl.baseandroid.utils.Configs;
import com.sjjd.wyl.baseandroid.utils.DisplayUtil;
import com.sjjd.wyl.baseandroid.utils.LogUtils;
import com.sjjd.wyl.baseandroid.utils.SPUtils;
import com.sjjd.wyl.baseandroid.utils.ToastUtils;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class BaseActivity extends AppCompatActivity implements BaseDataHandler.ErrorListener {
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mBaseLlRoot = findViewById(R.id.baseLlRoot);
        mDataHandler = new BaseDataHandler(this);
        mDataHandler.setErrorListener(this);

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
        ToastUtils.showToast(mContext, error, 2000);
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
        ToastUtils.clearToast();
    }

    public void isDeviceRegistered() {

        int mRegistered = RegisterUtils.getInstance(mContext).isDeviceRegistered();

        LogUtils.e(TAG, "onCreate: " + mRegistered);
        switch (mRegistered) {
            case Configs.REGISTER_FORBIDDEN://禁止注册/未注册
                //请求注册
                //请求信息密文
                REGISTER_STR = RegisterUtils.getInstance(mContext).register2Base64(false, MARK);
                RegisterCode = 1;
                break;
            case Configs.REGISTER_FOREVER://永久注册
                RegisterCode = 2;
                isRegistered = true;
                break;
            default://注册时间
                Register mRegister = RegisterUtils.getInstance(mContext).getRegister();
                if (mRegister != null) {
                    isRegistered = true;
                    RegisterCode = 2;
                    String mDate = mRegister.getDate();//获取注册时间
                    long rt = Long.parseLong(mDate);
                    long mMillis = System.currentTimeMillis();//本地时间

                    Date newDate2 = new Date(rt + (long) mRegistered * 24 * 60 * 60 * 1000);

                    LogUtils.e(TAG, "onCreate: 注册 " + rt);
                    LogUtils.e(TAG, "onCreate: 现在 " + mMillis);
                    LogUtils.e(TAG, "onCreate: 新的 " + newDate2.getTime());

                    //到期了 再次申请注册
                    if (newDate2.getTime() < mMillis) {
                        ToastUtils.showToast(mContext, "设备注册已过期！", 2000);
                        RegisterCode = 2;
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


    public Address mAddress;
    public CommonAdapter<Address.Area> mAreaAdapter;
    public CommonAdapter<Address.Floor> mFloorAdapter;

    public CommonAdapter<Address.Unit> mUnitAdapter;
    public CommonAdapter<Address.Window> mWindAdapter;

    public TextView tvUnit = null;
    public TextView tvFloor = null;
    public TextView tvArea = null;
    public TextView tvWindow = null;

    public void showSetting(View parent) {

        View view = View.inflate(mContext, R.layout.item_setting, null);
        final PopupWindow pop = new PopupWindow(view, 600,
                LinearLayout.LayoutParams.WRAP_CONTENT, true);
        pop.setFocusable(true);// 点击back退出pop
        pop.setOutsideTouchable(true);
        pop.setClippingEnabled(false);
        pop.setAnimationStyle(com.sjjd.wyl.baseandroid.R.style.PopupAnimation);
        pop.setBackgroundDrawable(new ColorDrawable(0x00ffffff));
        if (!pop.isShowing()) {
            //pop.showAsDropDown(mImgLogo);
            pop.showAtLocation(parent, Gravity.CENTER, 1, 1);
        }
        DisplayUtil.hideBottomUIMenu(this);
        pop.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                DisplayUtil.hideBottomUIMenu(BaseActivity.this);
            }
        });

        final EditText etIp = view.findViewById(R.id.etIp);
        // final EditText etNum = view.findViewById(R.id.etWinNum);
        final EditText etPort = view.findViewById(R.id.etPort);
        final EditText etPsw = view.findViewById(R.id.etAdmin);

        final RecyclerView wvUnit = view.findViewById(R.id.wvRegion);
        final RecyclerView wvFloor = view.findViewById(R.id.wvVillage);
        final RecyclerView wvArea = view.findViewById(R.id.wvBuilding);
        final RecyclerView wvWinds = view.findViewById(R.id.wvWindow);

        final Button mBtnCommit = view.findViewById(R.id.btnConfirm);
        final Button mBtnPswCommit = view.findViewById(R.id.btnPswConfirm);
        final Button mBtnArea = view.findViewById(R.id.btnGetArea);
        final Button mBtnClose = view.findViewById(R.id.btnClose);


        final LinearLayout llPsw = view.findViewById(R.id.llPsw);
        final LinearLayout llSetting = view.findViewById(R.id.llSetting);
        final LinearLayout llArea = view.findViewById(R.id.llArea);
        llSetting.setVisibility(View.GONE);
        llArea.setVisibility(View.GONE);
        llPsw.setVisibility(View.VISIBLE);

        etIp.setText(SPUtils.init(mContext).getDIYString(Configs.SP_IP));
        //etNum.setText(SPUtils.init(mContext).getDIYString(I.SP.WINDOW_NUM));
        etPort.setText(SPUtils.init(mContext).getDIYString(Configs.SP_PORT));

        mBtnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Process.killProcess(Process.myPid());
            }
        });

        final List<Address.Unit> mUnits = new ArrayList<>();
        final List<Address.Floor> mFloors = new ArrayList<>();
        final List<Address.Area> mAreas = new ArrayList<>();
        final List<Address.Window> mWinds = new ArrayList<>();


        mUnitAdapter = new CommonAdapter<Address.Unit>(mContext, R.layout.item_area, mUnits) {
            @Override
            protected void convert(ViewHolder holder, final Address.Unit Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getName());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvUnit != null) {
                            tvUnit.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvUnit = tv;
                        tvUnit.setTag(Unit.getUnitid());

                        if (mAddress != null) {
                            int id = Unit.getUnitid();
                            List<Address.Floor> mFloor = mAddress.getData().getFloor();
                            mFloors.clear();
                            for (Address.Floor v : mFloor) {
                                if (v.getUnitid() == id) {
                                    mFloors.add(v);
                                }
                            }
                            tvFloor = null;
                            tvArea = null;

                            mAreas.clear();

                            mFloorAdapter.notifyDataSetChanged();
                            mAreaAdapter.notifyDataSetChanged();
                        }
                    }
                });

            }
        };
        mFloorAdapter = new CommonAdapter<Address.Floor>(mContext, R.layout.item_area, mFloors) {
            @Override
            protected void convert(ViewHolder holder, final Address.Floor Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getFloor());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvFloor != null) {
                            tvFloor.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvFloor = tv;
                        tvFloor.setTag(Unit.getFloor());

                        if (mAddress != null) {
                            List<Address.Area> mArea = mAddress.getData().getArea();
                            mAreas.clear();
                            for (Address.Area v : mArea) {
                                if (v.getFloor().equals(Unit.getFloor())) {
                                    mAreas.add(v);
                                }
                            }
                            tvArea = null;
                            mAreaAdapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        };
        mAreaAdapter = new CommonAdapter<Address.Area>(mContext, R.layout.item_area, mAreas) {
            @Override
            protected void convert(ViewHolder holder, final Address.Area Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getArea());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvArea != null) {
                            tvArea.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvArea = tv;
                        tvArea.setTag(Unit.getArea());

                        if (mAddress != null) {
                            List<Address.Window> mWindows = mAddress.getData().getWindow();
                            mWinds.clear();

                            for (Address.Window v : mWindows) {
                                if (v.getArea().equals(Unit.getArea())) {
                                    mWinds.add(v);
                                }
                            }
                            tvWindow = null;
                            mWindAdapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        };
        mWindAdapter = new CommonAdapter<Address.Window>(mContext, R.layout.item_area, mWinds) {
            @Override
            protected void convert(ViewHolder holder, final Address.Window Unit, int position) {
                holder.setText(R.id.tvArea, Unit.getWindownum());
                final TextView tv = holder.getView(R.id.tvArea);
                tv.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                holder.setOnClickListener(R.id.tvArea, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (tvWindow != null) {
                            tvWindow.setBackgroundColor(getResources().getColor(R.color.main_theme2));
                        }
                        tv.setBackgroundColor(getResources().getColor(R.color.main_theme3));
                        tvWindow = tv;
                        tvWindow.setTag(Unit.getWindownum());
                    }
                });
            }
        };


        wvUnit.setLayoutManager(new LinearLayoutManager(mContext));
        wvUnit.setAdapter(mUnitAdapter);
        wvFloor.setLayoutManager(new LinearLayoutManager(mContext));
        wvFloor.setAdapter(mFloorAdapter);
        wvArea.setLayoutManager(new LinearLayoutManager(mContext));
        wvArea.setAdapter(mAreaAdapter);
        wvWinds.setLayoutManager(new LinearLayoutManager(mContext));
        wvWinds.setAdapter(mWindAdapter);

        mBtnArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ip = etIp.getText().toString().trim();
                if (!TextUtils.isEmpty(ip)) {
                    //HOST = String.format(, ip);
                    OkGo.<Address>post(HOST)
                            .params("method", METHOD_AREA)
                            .params("checkinfo", "{\"timestamp\":\"" + System.currentTimeMillis() + "\",\"token\":\"" + System.currentTimeMillis() + "\"}")
                            .params("content", "{}")
                            .execute(new JsonCallBack<Address>(Address.class) {
                                @Override
                                public void onSuccess(Response<Address> response) {
                                    if (response != null) {
                                        llArea.setVisibility(View.VISIBLE);
                                        llSetting.setVisibility(View.GONE);
                                        mAddress = response.body();
                                        if (mAddress != null && mAddress.getCode().equals("1")) {
                                            LogUtils.e(TAG, "onSuccess: ");
                                            mUnits.clear();
                                            mAreas.clear();
                                            mFloors.clear();
                                            mWinds.clear();
                                            mUnits.addAll(mAddress.getData().getUnit());
                                            mUnitAdapter.notifyDataSetChanged();
                                            mAreaAdapter.notifyDataSetChanged();
                                            mFloorAdapter.notifyDataSetChanged();
                                            mWindAdapter.notifyDataSetChanged();
                                        } else {
                                            ToastUtils.showToast(mContext, "获取失败！", 2000);
                                        }

                                    } else
                                        ToastUtils.showToast(mContext, "获取失败！", 2000);
                                }

                                @Override
                                public void onError(Response<Address> response) {
                                    super.onError(response);
                                    ToastUtils.showToast(mContext, "获取失败！", 2000);
                                }
                            });
                }

            }
        });


        mBtnPswCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!TextUtils.isEmpty(etPsw.getText().toString())) {
                    String psw = etPsw.getText().toString().trim();
                    if (psw.equals("sjjd")) {
                        llPsw.setVisibility(View.GONE);
                        llSetting.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        //提交
        mBtnCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //重新启动应用
                SPUtils.init(mContext).putDIYString(Configs.SP_IP, etIp.getText().toString().trim());

                SPUtils.init(mContext).putDIYString(Configs.SP_PORT, etPort.getText().toString().trim());


                if (tvUnit == null || tvArea == null || tvFloor == null || tvWindow == null) {
                    ToastUtils.showToast(mContext, "请选择区域！", 2000);
                    return;
                }

                LogUtils.e(TAG, "onClick: tvFloor= " + tvFloor.getTag() + "  tvUnit= " + tvUnit.getTag() + "  tvArea= " + tvArea.getTag());

                SPUtils.init(mContext).putDIYString(Configs.SP_FLOOR, tvFloor == null ? "" : tvFloor.getTag().toString());
                SPUtils.init(mContext).putDIYString(Configs.SP_AREA, tvArea == null ? "" : tvArea.getTag().toString());
                SPUtils.init(mContext).putDIYString(Configs.SP_UNITID, tvUnit == null ? "" : tvUnit.getTag().toString());
                SPUtils.init(mContext).putDIYString(Configs.SP_WINDOW_NUM, tvWindow == null ? "" : tvWindow.getTag().toString());

                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppUtils.restartApp(mContext);
                    }
                }, 500);

                if (pop.isShowing()) {
                    pop.dismiss();
                }

            }

        });
    }


}
