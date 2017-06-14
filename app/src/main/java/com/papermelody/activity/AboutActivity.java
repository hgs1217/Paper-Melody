package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public class AboutActivity extends BaseActivity {
    /**
     * 用例：查看关于
     * 关于页面
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
