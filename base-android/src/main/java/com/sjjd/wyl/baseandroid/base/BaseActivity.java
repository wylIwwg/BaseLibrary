package com.sjjd.wyl.baseandroid.base;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.sjjd.wyl.baseandroid.R;
import com.sjjd.wyl.baseandroid.utils.ToastUtils;

public class BaseActivity extends AppCompatActivity implements BaseDataHandler.ErrorListener{
    public String TAG = this.getClass().getSimpleName();
    public Context mContext;
    public LinearLayout mBaseLlRoot;//根布局
    public BaseDataHandler mDataHandler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.activity_base);
        mContext=this;
        mBaseLlRoot = findViewById(R.id.baseLlRoot);
        mDataHandler=new BaseDataHandler(this);
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
        ToastUtils.clearTasot();
    }
}
