package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class SplashActivity extends BaseActivity {
    /**
     * 用例：启动应用
     * 启动加载页面
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {

                Intent mainIntent = new Intent(SplashActivity.this, TutorialActivity.class);
                SplashActivity.this.startActivity(mainIntent);//跳转到MainActivity
                SplashActivity.this.finish();//结束SplashActivity
            }
        }, 1000);//给postDelayed()方法传递延迟参数
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_splash;
    }
}
