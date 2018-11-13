package com.sjjd.wyl.basedemo;

import android.os.Bundle;
import android.view.View;

import com.sjjd.wyl.baseandroid.base.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setLeftTitleName("左边标题");
        setMiddleTitleName("中间标题");

        mTvTitleMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingPop(view);
            }
        });
    }
}
