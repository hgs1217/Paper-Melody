package com.papermelody.activity;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;

import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public abstract class BaseActivity extends AppCompatActivity {

    static{ System.loadLibrary("opencv_java3"); }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getContentViewId());
        ButterKnife.bind(this);
        setStatusBarTransparent();
    }

    abstract protected int getContentViewId();

    private void setStatusBarTransparent() {
        /**
         * 设置手机状态栏为透明，注意activity的layout需设置fitsSystemWindows参数为true，否则界面会占用状态栏
         */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams = getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);
        }
    }
}
