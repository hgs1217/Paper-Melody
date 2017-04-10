package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class TutorialActivity extends BaseActivity {
    /**
     * 用例：浏览教程
     * 教程页面，页面由4个fragment组成，通过滑动来实现页面跳转；第一次进入应用时也会跳出此页面
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_tutorial;
    }
}
