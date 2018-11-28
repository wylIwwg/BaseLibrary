package com.sjjd.wyl.basedemo;

import android.os.Bundle;
import android.view.View;

import com.sjjd.wyl.baseandroid.base.BaseActivity;
import com.sjjd.wyl.baseandroid.socket.SocketManager;
import com.sjjd.wyl.baseandroid.thread.SingleThread;
import com.sjjd.wyl.baseandroid.view.AutoPollRecyclerView;

public class MainActivity extends BaseActivity {
    AutoPollRecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.rlv);

        mRecyclerView.setTimeAutoPoll(100);
        setLeftTitleName("左边标题");
        setMiddleTitleName("中间标题");

        mTvTitleMiddle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showSettingPop(view);
            }
        });

        SocketManager.getInstance(mContext).setHandler(null).startTcpConnection("192.168.2.135", "8282");

        SingleThread.getInstance(String.class).what(0).url("").excute(null);
    }

    @Override
    public void shouldDestory() {
        super.shouldDestory();

    }
}
