package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class PlayListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 弹奏完之后的用户试听页面，其中试听部分是用Fragment实现，其它的按钮布局在Activity中
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play_listen;
    }
}
