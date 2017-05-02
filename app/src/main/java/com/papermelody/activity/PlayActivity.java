package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public class PlayActivity extends BaseActivity {
    /**
     * 用例：演奏乐器
     * 弹奏乐器时的界面，可能存在虚拟乐器或曲谱等内容
     */
    private int mode, instrument, category, opern;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        instrument = intent.getIntExtra("instrument", 0);
        category = intent.getIntExtra("category", 0);
        if (mode == 0) {
            opern = intent.getIntExtra("opern", 0);
        }
        // TODO;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
