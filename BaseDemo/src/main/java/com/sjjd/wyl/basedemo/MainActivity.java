package com.sjjd.wyl.basedemo;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sjjd.wyl.baseandroid.base.BaseActivity;
import com.sjjd.wyl.baseandroid.utils.DeviceUtil;
import com.sjjd.wyl.baseandroid.utils.WifiUtil;

public class MainActivity extends BaseActivity {
    TextView mTextView;
    Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mContext = this;
        mTextView = findViewById(R.id.tvMac);
        mTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mTextView.setText(DeviceUtil.getMacAddress(mContext) + "\n"
                        + DeviceUtil.getMacFromWlan0() + "\n"
                        + DeviceUtil.getMacFromCatOrder() + "\n"
                        + DeviceUtil.getMachineHardwareAddress() + "\n"
                        + "\n"
                        + WifiUtil.getBroadcastAddress() + "\n"
                );
            }
        });
        setLeftTitleName("左边标题");
        setMiddleTitleName("中间标题");

        mTvTitleMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingPop(view);
            }
        });


    }

    @Override
    public void shouldDestory() {
        super.shouldDestory();

    }
}
