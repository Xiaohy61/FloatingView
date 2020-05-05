package com.xiaohy.floatingview;

import android.content.Context;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class CFloatingView extends FloatingMagnetView  {

    private MagnetViewListener mMagnetViewListener;
    private long mLastTouchDownTime;//用于判断点击
    private static final int TOUCH_TIME_THRESHOLD = 150;
    public View view;

    /**
     * 悬浮窗点击接口
     */
    public interface MagnetViewListener {

        void onRemove(CFloatingView cFloatingView);

        void onClick(CFloatingView cFloatingView);

        void onEndAppear(CFloatingView cFloatingView);

        void onEndHide(CFloatingView cFloatingView);
    }

    public interface IFloatingViews {
        void onInitViews(CFloatingView cFloatingView);
    }

    public CFloatingView(@NonNull Context context) {
        super(context, null);
    }

    public View setLayout(@NonNull Context context, int en_floating_view_id) {
        return view = inflate(context, en_floating_view_id, this);
    }

    public void setMagnetViewListener(MagnetViewListener magnetViewListener) {
        this.mMagnetViewListener = magnetViewListener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        if (event != null) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mLastTouchDownTime = System.currentTimeMillis();
                    clearAnimation();
                    break;
                case MotionEvent.ACTION_UP:
                    if (isOnClickEvent()) {
                        dealClickEvent();
                    }
                    break;
            }
        }
        return true;
    }

    protected void dealClickEvent() {
        if (mMagnetViewListener != null) {
            mMagnetViewListener.onClick(this);
        }
    }

    public void onRemove() {
        if (mMagnetViewListener != null) {
            mMagnetViewListener.onRemove(this);
        }
    }

    protected boolean isOnClickEvent() {
        return System.currentTimeMillis() - mLastTouchDownTime < TOUCH_TIME_THRESHOLD;
    }

    /**
     * 隐藏,点击又出现之后,由子类进行具体实现
     */
    @Override
    protected void onEndAppear() {
        super.onEndAppear();
        if (mMagnetViewListener != null) {
            mMagnetViewListener.onEndAppear(this);
        }
    }

    /**
     * 隐藏半边之后
     */
    @Override
    protected void onEndHide() {
        super.onEndHide();
        if (mMagnetViewListener != null) {
            mMagnetViewListener.onEndHide(this);
        }
    }

    @Override
    public void onAttach() {
        super.onAttach();
        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                hideEdgeAnima();
            }
        }, 2000);
    }
}
