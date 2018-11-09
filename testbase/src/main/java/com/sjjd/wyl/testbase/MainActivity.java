package com.sjjd.wyl.testbase;

import android.os.Bundle;

import com.sjjd.wyl.baseandroid.BaseActivity;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setTitleName("我是主页");
    }
}
