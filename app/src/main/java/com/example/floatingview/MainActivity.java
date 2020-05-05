package com.example.floatingview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.xiaohy.floatingview.CFloatingManager;
import com.xiaohy.floatingview.CFloatingView;

public class MainActivity extends AppCompatActivity {

    private CFloatingManager.FloatingImp floatingImp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (null == floatingImp) {
            floatingImp = CFloatingManager.build()
                    .setLayout(R.layout.en_floating_view)//设置布局
                    .setInitViews(new CFloatingView.IFloatingViews() { // 可以在此处对布局内的子控件单独进行控制，todo：设置点击事件，则会影响到拖动事件
                        @Override
                        public void onInitViews(CFloatingView cFloatingView) {
                        }
                    })
                    .setListener(new CFloatingView.MagnetViewListener() {//设置监听事件
                        @Override
                        public void onRemove(CFloatingView cFloatingView) {
                            Toast.makeText(MainActivity.this, "我没了", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onClick(CFloatingView cFloatingView) {
                            Toast.makeText(MainActivity.this, "点到我了", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onEndAppear(CFloatingView cFloatingView) {
                            //((ImageView)cFloatingView.view.findViewById(R.id.icon)).setImageResource(R.drawable.jy_sdk_float_window_normal);
                           // Toast.makeText(MainActivity.this, "又出现了", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onEndHide(CFloatingView cFloatingView) {
                           // ((ImageView)cFloatingView.view.findViewById(R.id.icon)).setImageResource(R.drawable.jy_sdk_float_window_transparent);
                            Toast.makeText(MainActivity.this, "隐藏了一半", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .setIsMovable(true)//控制是否可移动
                    .setIsHideEdge(false)//控制是否需要隐藏
                    .create();//创建实体
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        floatingImp.attach(this);//绑定实体至页面
    }

    @Override
    protected void onStop() {
        super.onStop();
        floatingImp.detach(this);//移除实体离开页面
    }
}