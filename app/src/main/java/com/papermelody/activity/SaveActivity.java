package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.papermelody.R;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class SaveActivity extends BaseActivity {
    /**
     * 用例：保存作品
     * 保存作品页面
     */

    @BindView(R.id.btn_play_listen)
    Button btnPlayListen;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        btnPlayListen.setOnClickListener((View v)->{
            Intent intent = new Intent(getApplicationContext(), PlayListenActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_save;
    }
}
