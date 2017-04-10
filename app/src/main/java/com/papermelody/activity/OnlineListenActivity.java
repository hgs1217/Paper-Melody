package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class OnlineListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 音乐圈中网络作品的试听页面，其中试听部分是用Fragment实现，音乐信息、评论等内容布局在Activity中
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_online_listen;
    }
}
