package com.xiaohy.floatingview;

import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

import androidx.core.view.ViewCompat;

public class CFloatingManager {
    public static FloatingImp build() {
        return new FloatingImp();
    }

    public static class FloatingImp {
        private Handler mHandler;
        private CFloatingView mCFloatingView;
        private FrameLayout mContainer;
        int layoutId = 0; //布局id
        ViewGroup.LayoutParams params; //布局初始参数（位置，大小等）
        CFloatingView.MagnetViewListener magnetViewListener;//监听
        CFloatingView.IFloatingViews iFloatingViews;//监听
        boolean isMovable = true;//是否可移动
        boolean isHideEdge = true;//是否隐藏边缘

        public FloatingImp create() {
            mHandler = new Handler(Looper.getMainLooper());
            synchronized (this) {
                if (mCFloatingView != null) {
                    return this;
                }
                mCFloatingView = new CFloatingView(EnContext.get().getApplicationContext());
                mCFloatingView.setLayout(EnContext.get().getApplicationContext(), layoutId);
                if (null != iFloatingViews) {
                    iFloatingViews.onInitViews(mCFloatingView);
                }
                mCFloatingView.setLayoutParams(null == params ? defaultParams() : params);
                mCFloatingView.setIsMovable(isMovable);
                mCFloatingView.setIsHideEdge(isHideEdge);
                if (mCFloatingView != null) {
                    mCFloatingView.setMagnetViewListener(magnetViewListener);
                }
                addViewToWindow(mCFloatingView);
            }
            return this;
        }

        /**
         * 设置布局控件
         */
        public FloatingImp setLayout(int layoutId) {
            this.layoutId = layoutId;
            return this;
        }

        /**
         * 设置布局初始参数
         */
        public FloatingImp setLayoutParams(ViewGroup.LayoutParams params) {
            this.params = params;
            return this;
        }

        /**
         * 监听事件
         */
        public FloatingImp setListener(CFloatingView.MagnetViewListener magnetViewListener) {
            this.magnetViewListener = magnetViewListener;
            return this;
        }

        public FloatingImp setIsMovable(boolean isMovable) {
            this.isMovable = isMovable;
            return this;
        }

        public FloatingImp setIsHideEdge(boolean isHideEdge) {
            this.isHideEdge = isHideEdge;
            return this;
        }

        public FloatingImp setInitViews(CFloatingView.IFloatingViews iFloatingViews) {
            this.iFloatingViews = iFloatingViews;
            return this;
        }

        public FloatingImp remove() {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    if (mCFloatingView == null) {
                        return;
                    }
                    if (ViewCompat.isAttachedToWindow(mCFloatingView) && mContainer != null) {
                        mContainer.removeView(mCFloatingView);
                    }
                    mCFloatingView = null;
                }
            });
            return this;
        }

        /**
         * 将view绑定到activity的布局中
         */
        public FloatingImp attach(Activity activity) {
            attach(getActivityRoot(activity));
            return this;
        }

        /**
         * 将view绑定到布局中
         */
        public FloatingImp attach(FrameLayout container) {
            if (container == null || mCFloatingView == null) {
                mContainer = container;
                return this;
            }
            if (mCFloatingView.getParent() == container) {
                return this;
            }
            if (mContainer != null && mCFloatingView.getParent() == mContainer) {
                mContainer.removeView(mCFloatingView);
            }
            mContainer = container;
            container.addView(mCFloatingView);
            mCFloatingView.onAttach();
            return this;
        }

        /**
         * 将view从activity的布局中解绑
         */
        public FloatingImp detach(Activity activity) {
            detach(getActivityRoot(activity));
            return this;
        }

        /**
         * 将view从布局中解绑
         */
        public FloatingImp detach(FrameLayout container) {
            if (mCFloatingView != null && container != null && ViewCompat.isAttachedToWindow(mCFloatingView)) {
                container.removeView(mCFloatingView);
            }
            if (mContainer == container) {
                mContainer = null;
            }
            mCFloatingView.onDetach();
            return this;
        }

        /**
         * 将view添加到当前窗口中
         */
        private void addViewToWindow(final CFloatingView view) {
            if (mContainer == null) {
                return;
            }
            mContainer.addView(view);
        }

        /**
         * 默認Params，用于设置最开始位置
         */
        private FrameLayout.LayoutParams defaultParams() {
            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                    RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.BOTTOM | Gravity.START;
            params.setMargins(13, params.topMargin, params.rightMargin, 56);
            return params;
        }

        /**
         * 获取activity所绑定的布局
         */
        private FrameLayout getActivityRoot(Activity activity) {
            if (activity == null) {
                return null;
            }
            try {
                return (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }

}
