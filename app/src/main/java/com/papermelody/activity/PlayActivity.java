package com.papermelody.activity;

import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public class PlayActivity extends BaseActivity {
    /**
     * 弹奏乐器时的界面，可能存在虚拟乐器或曲谱等内容
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
