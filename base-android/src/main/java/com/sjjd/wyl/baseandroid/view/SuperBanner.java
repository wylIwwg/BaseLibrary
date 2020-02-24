package com.sjjd.wyl.baseandroid.view;

/**
 * Created by wyl on 2018/5/4.
 */

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.MotionEvent;

import java.lang.ref.WeakReference;

public class SuperBanner extends RecyclerView {
    private static final String TAG = " SuperBanner ";
    private long rollTime = 100;//默认像素滚动时间 ms
    AutoPollTask autoPollTask;
    private boolean running; //标示是否正在自动轮询
    private boolean canRun;//标示是否可以自动轮询,可在不需要的是否置false

    private int rollType;
    public static final int ROLL_PIXEL = 1;
    public static final int ROLL_ITEM = 2;

    /**
     * 设置滚动类型和间隔
     *
     * @param rollType
     * @param time
     */
    public void setTypeTime(int rollType, int time) {
        this.rollType = rollType;
        this.rollTime = time;
    }

    /**
     * 设置滚动类型 像素滚动或是item滚动
     *
     * @param rollType
     */
    public void setRollType(int rollType) {
        this.rollType = rollType;
    }

    public SuperBanner(Context context) {
        this(context, null);
    }

    public SuperBanner(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        autoPollTask = new AutoPollTask(this);
    }

    class AutoPollTask implements Runnable {
        private final WeakReference<SuperBanner> mReference;

        //使用弱引用持有外部类引用->防止内存泄漏
        public AutoPollTask(SuperBanner reference) {
            this.mReference = new WeakReference<SuperBanner>(reference);
        }

        @Override
        public void run() {
            SuperBanner recyclerView = mReference.get();
            if (recyclerView != null && recyclerView.running && recyclerView.canRun) {

                //判断是像素滚动还是item滚动
                if (rollType == ROLL_PIXEL) {
                    recyclerView.scrollBy(1, 1);

                } else {
                    LinearLayoutManager mLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                    int index = mLayoutManager.findLastVisibleItemPosition();
                    if (index < mLayoutManager.getItemCount() - 1)
                        recyclerView.smoothScrollToPosition(index + 1);
                }
                recyclerView.postDelayed(recyclerView.autoPollTask, recyclerView.rollTime);
            }
        }
    }

    /**
     * 设置滚动间隔
     *
     * @param timeAutoPoll
     */
    public void setRollTimeSpan(long timeAutoPoll) {
        rollTime = timeAutoPoll;
    }


    public void start() {
        start(false);
    }

    /**
     * 开启:如果正在运行,先停止->再开启
     *
     * @param immediate 是否立刻滚动
     */
    public void start(boolean immediate) {
        if (running)
            stop();
        canRun = true;
        running = true;
        postDelayed(autoPollTask, immediate ? 100 : rollTime);
    }

    public void stop() {
        running = false;
        removeCallbacks(autoPollTask);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        switch (e.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (running)
                    stop();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_OUTSIDE:
                if (canRun)
                    start();
                break;
        }
        return super.onTouchEvent(e);
    }
}
