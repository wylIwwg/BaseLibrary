package com.sjjd.wyl.baseandroid.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import com.sjjd.wyl.baseandroid.tools.ToolLog;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by wyl on 2018/1/30.
 * 竖直方向内容可以滚动的Textview
 */

public class VerticalScrollTextView extends android.support.v7.widget.AppCompatTextView {

    private static final String TAG = " VerticalScrollTextView ";
    private float step1 = 0f;
    private float step2 = 0f;
    private float speed = 0.3f;//滚动速度
    private Paint mPaint = new Paint();
    private float txtSize = 30f;//字号
    private float lineSpace = 10f;//行间距
    private float graphSpace = 50f;//段落距离
    private List<String> textList = new ArrayList<String>();    //分行保存textview的显示信息。


    public VerticalScrollTextView(Context context) {
        super(context);
    }

    public VerticalScrollTextView(Context context, AttributeSet attrs) {
        super(context, attrs);

    }

    /**
     * 设置两段循环文字之间的距离
     *
     * @param graphSpace 段间距
     */
    public void setGraphSpace(float graphSpace) {
        this.graphSpace = graphSpace;
        invalidate();
    }

    /**
     * 设置行间距
     *
     * @param lineSpace 行间距
     */
    public void setLineSpace(float lineSpace) {
        this.lineSpace = lineSpace;
        invalidate();
    }

    /**
     * 设置滚动速度， 建议1.0f以下
     *
     * @param speed 滚动速度
     */
    public void setSpeed(float speed) {
        this.speed = speed;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float width = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (widthMode != MeasureSpec.EXACTLY || heightMode != MeasureSpec.EXACTLY) {
            throw new IllegalStateException("ScrollLayout only  run at EXACTLY mode!");
        }

        String txt = getText().toString();
        if (txt.length() == 0) {

            return;
        }

        txtSize = getTextSize();
        //根据宽度和字体大小，来计算textview显示的行数。
        int textLineNum = (int) (width / txtSize);//6
        ToolLog.e(TAG, "onMeasure: " + textLineNum + "  " + width + "  " + txt.length() + "  " + lineSpace);
        textList.clear();
        StringBuilder builder = null;
        for (int i = 0; i < txt.length(); i++) {
            if (i % textLineNum == 0) {
                builder = new StringBuilder();
            }
            if (i % textLineNum <= textLineNum - 1) {
                builder.append(txt.charAt(i));
            }
            if (i % textLineNum == textLineNum - 1) {
                textList.add(builder.toString());
            } else if (i == txt.length() - 1) {
                textList.add(builder.toString());
            }

        }
        ToolLog.e(TAG, "onMeasure: " + "textSize= " + txtSize + " 行数 = " + textList.size());

        step1 = 0;
        step2 = txtSize * textList.size() + lineSpace * textList.size() + graphSpace;
    }


    //利用上面计算的显示行数，将文字画在画布上，实时更新。
    @Override
    public void onDraw(Canvas canvas) {
        if (textList.size() == 0) return;

        mPaint.setTextSize(txtSize);//设置字体大小
        //判断是否一屏能显示完，显示不下才滚动
        if (textList.size() >= (getHeight()) / ((txtSize + lineSpace) * 1.0f)) {

            step1 += speed;
            step2 -= speed;

            //当第一个循环的文字绘制完之后，将第二个循环的赋值给第一个，并重置第二个
            if (step1 > txtSize * textList.size() + lineSpace * textList.size() + graphSpace) {
                step1 = step2;
                step2 = txtSize * textList.size() + lineSpace * textList.size() + graphSpace;
            }

            //绘制的第一幕的文字
            for (int i = 0; i < textList.size(); i++) {
                canvas.drawText(textList.get(i), getPaddingLeft(), (i + 1) * txtSize - step1 + lineSpace * (i), mPaint);

            }
            //添加个判断，避免不必要的绘制
            if (txtSize * textList.size() - (step1 + lineSpace * textList.size() + graphSpace) < getHeight()) {
                //紧接着 绘制第二幕文字
                for (int j = 0; j < textList.size(); j++) {
                    canvas.drawText(textList.get(j), getPaddingLeft(), (j + 1) * txtSize + step2 + lineSpace * (j), mPaint);
                }
            }

            invalidate();

        } else {
            for (int i = 0; i < textList.size(); i++) {
                canvas.drawText(textList.get(i), getPaddingLeft(), getPaddingTop() + (i + 1) * txtSize + lineSpace * (i), mPaint);
            }


        }
    }

}