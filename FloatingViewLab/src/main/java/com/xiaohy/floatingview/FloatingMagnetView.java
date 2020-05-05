package com.xiaohy.floatingview;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public class FloatingMagnetView extends FrameLayout {
    public static final int MARGIN_EDGE = 13;
    private float mOriginalRawX;
    private float mOriginalRawY;
    private float mOriginalX;
    private float mOriginalY;
    protected MoveAnimator mMoveAnimator;
    protected int mScreenWidth;
    private int mScreenHeight;
    private int mStatusBarHeight;
    boolean isMovable = true;//是否可移动

    public FloatingMagnetView(Context context) {
        this(context, null);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FloatingMagnetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mMoveAnimator = new MoveAnimator();
        mStatusBarHeight = SystemUtils.getStatusBarHeight(getContext());
        setClickable(true);
        updateSize();
    }

    /**
     * 设置是否可移动
     */
    public void setIsMovable(boolean isMovable) {
        this.isMovable = isMovable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event == null) {
            return false;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                changeOriginalTouchParams(event);
                updateSize();
                mMoveAnimator.stop();
                removeRunable();
                onEndAppear();
                break;
            case MotionEvent.ACTION_MOVE:
                if (isMovable) {
                    updateViewPosition(event);
                }
                break;
            case MotionEvent.ACTION_UP:
                moveToEdge();
                break;
        }
        return true;
    }

    /**
     * 更新view的坐标位置
     */
    private void updateViewPosition(MotionEvent event) {
        setX(mOriginalX + event.getRawX() - mOriginalRawX);
        // 限制不可超出屏幕高度
        float desY = mOriginalY + event.getRawY() - mOriginalRawY;
        if (desY < 0) {
            desY = 0;
        }
        if (desY > mScreenHeight - getHeight() * 2) {
            desY = mScreenHeight - getHeight() * 2;
        }
        setY(desY);
    }

    private void changeOriginalTouchParams(MotionEvent event) {
        mOriginalX = getX();
        mOriginalY = getY();
        mOriginalRawX = event.getRawX();
        mOriginalRawY = event.getRawY();
    }

    protected void updateSize() {
        mScreenWidth = (SystemUtils.getScreenWidth(getContext()) - this.getWidth());
        mScreenHeight = SystemUtils.getScreenHeight(getContext());
    }

    public void moveToEdge() {
        float moveDistance = isNearestLeft() ? MARGIN_EDGE : mScreenWidth - MARGIN_EDGE;
        mMoveAnimator.start(moveDistance, getY());
    }

    protected boolean isNearestLeft() {
        int middle = mScreenWidth / 2;
        return getX() < middle;
    }

    protected class MoveAnimator implements Runnable {

        private Handler handler = new Handler(Looper.getMainLooper());
        private float destinationX;
        private float destinationY;
        private long startingTime;

        void start(float x, float y) {
            this.destinationX = x;
            this.destinationY = y;
            startingTime = System.currentTimeMillis();
            handler.post(this);
        }

        @Override
        public void run() {
            if (getRootView() == null || getRootView().getParent() == null) {
                return;
            }
            float progress = Math.min(1, (System.currentTimeMillis() - startingTime) / 400f);
            float deltaX = (destinationX - getX()) * progress;
            float deltaY = (destinationY - getY()) * progress;
            move(deltaX, deltaY);
            if (progress < 1) {
                handler.post(this);
            } else {
                hideEdgeAnima();//开始隐藏
            }
        }

        private void stop() {
            handler.removeCallbacks(this);
        }
    }

    private void move(float deltaX, float deltaY) {
        setX(getX() + deltaX);
        setY(getY() + deltaY);
    }

    public void onAttach() {
    }

    public void onDetach() {
    }

    //关于靠边隐藏==============================================================================================
    boolean isHideEdge = true;//是否隐藏边缘
    protected static final int HEDE_FLOAT_VIEW_TIME = 3000;//靠边隐藏时间
    protected static final int MES_ANIMA_LEFT = 0;
    protected static final int MES_ANIMA_RIGHT = 1;
    protected static final int VIEW_GONE = 2;
    protected static final int VIEW_Transparent = 3;

    protected AnimationSet animationleft;
    protected AnimationSet animationright;

    /**
     * 设置是否隐藏边缘
     */
    public void setIsHideEdge(boolean isHideEdge) {
        this.isHideEdge = isHideEdge;
    }

    /**
     * 隐藏边缘动画
     */
    protected void hideEdgeAnima() {
        if (isHideEdge) {
            if (isNearestLeft()) {
                handler.postDelayed(myRunnableLeft, HEDE_FLOAT_VIEW_TIME);
            } else {
                handler.postDelayed(myRunnableRigth, HEDE_FLOAT_VIEW_TIME);
            }
        }
    }

    private void removeRunable() {
        handler.removeCallbacksAndMessages(null);
    }

    protected Runnable runnableViewGone = new Runnable() {
        public void run() {
            Message message = handler.obtainMessage();
            message.what = VIEW_GONE;
            handler.sendMessage(message);

        }
    };

    protected Runnable runnableTransparent = new Runnable() {
        @Override
        public void run() {
            Message message = handler.obtainMessage();
            message.what = VIEW_Transparent;
            handler.sendMessage(message);
        }
    };

    protected Runnable myRunnableLeft = new Runnable() {
        public void run() {
            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_LEFT;
            handler.sendMessage(message);
        }
    };

    protected Runnable myRunnableRigth = new Runnable() {
        public void run() {
            Message message = handler.obtainMessage();
            message.what = MES_ANIMA_RIGHT;
            handler.sendMessage(message);
        }
    };

    protected Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) {

            switch (msg.what) {
                case MES_ANIMA_LEFT:
                    if (null == animationleft) {
                        animationleft = new AnimationSet(true);
                        animationleft.setDuration(250);
                        animationleft.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, -0.5f, 0, 0, 0, 0));
                        animationleft.setFillAfter(true);
                    }
                    startAnimation(animationleft);
                    animationleft.setAnimationListener(ainimaLeft);
                    break;

                case MES_ANIMA_RIGHT:
                    if (null == animationright) {
                        animationright = new AnimationSet(true);
                        animationright.setDuration(250);
                        animationright.addAnimation(new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f, 0, 0, 0, 0));
                        animationright.setFillAfter(true);
                    }
                    startAnimation(animationright);
                    animationright.setAnimationListener(ainimaRigth);
                    break;

//                case VIEW_GONE:
//
//                    imgFloatView.setVisibility(View.GONE);
//                    Log.d(TAG, "handleMessage: bingo");
//                    break;

                case VIEW_Transparent:
                    break;

                default:
                    break;
            }

        }
    };

    protected Animation.AnimationListener ainimaLeft = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onEndHide();
        }
    };

    protected Animation.AnimationListener ainimaRigth = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            onEndHide();
        }
    };

    /**
     * 隐藏,点击又出现之后,由子类进行具体实现
     */
    protected void onEndAppear() {
    }
    /**
     * 隐藏半边之后,由子类进行具体实现
     */
    protected void onEndHide() {
    }
}
