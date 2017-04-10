package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class LogInActivity extends BaseActivity {
    /**
     * 用例：注册、登录
     * 注册于登录页面，此页面包含2个Fragment
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_about;
    }
}
