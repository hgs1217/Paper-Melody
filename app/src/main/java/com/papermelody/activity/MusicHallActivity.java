package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class MusicHallActivity extends BaseActivity {
    /**
     * 用例：浏览音乐圈
     * 音乐圈页面，上传作品使用recyclerView排序
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
