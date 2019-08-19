package com.sjjd.wyl.baseandroid.view;

import android.content.Context;
import android.graphics.Rect;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;

/**
 * Created by wyl on 2019/1/21.
 */
public class MEditView extends android.support.v7.widget.AppCompatEditText implements TextWatcher {
    public MEditView(Context context) {
        this(context, null);
    }

    public MEditView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MEditView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        addTextChangedListener(this);
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null){
            imm.hideSoftInputFromWindow(this.getWindowToken(), 0);  //强制隐藏
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    protected void onFocusChanged(boolean focused, int direction, Rect previouslyFocusedRect) {
        super.onFocusChanged(focused, direction, previouslyFocusedRect);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        setFocusable(true);
        requestFocus();
        setFocusableInTouchMode(true);
        return super.onTouchEvent(event);
    }

    @Override
    public void afterTextChanged(Editable editable) {
        setSelection(getText().toString().length());
    }
}
