package com.sjjd.wyl.baseandroid;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BaseActivity extends AppCompatActivity {

    public Context mContext;
    public ImageView mImgBack;
    public TextView mTvBack;
    public TextView mTvTitle;
    public LinearLayout mBaseLlRoot;
    public LinearLayout mLlTopBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext = this;
        mImgBack = findViewById(R.id.imgBack);
        mTvBack = findViewById(R.id.tvBack);
        mTvTitle = findViewById(R.id.tvTitle);
        mBaseLlRoot = findViewById(R.id.baseLlRoot);
        mLlTopBar = findViewById(R.id.llTopBar);
        mTvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });

        mImgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                close();
            }
        });
    }

    private void close() {
        this.finish();
    }


    @Override
    protected void onResume() {
        super.onResume();
        hideBottomUIMenu();
    }

    @Override
    public void setContentView(int layoutResID) {
        setContentView(View.inflate(this, layoutResID, null));
    }

    public void setTitleName(String title) {
        if (mTvTitle != null && title != null) {
            mTvTitle.setText(title);
        }
    }

    @Override
    public void setContentView(View view) {

        if (mBaseLlRoot == null) return;
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        mBaseLlRoot.addView(view, lp);
    }

    protected void hideBottomUIMenu() {
        //隐藏虚拟按键，并全屏
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB && Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            View view = getWindow().getDecorView();
            view.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //for new api versions
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;

            decorView.setSystemUiVisibility(uiOptions);
        }
    }


}
