package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.papermelody.R;
import com.papermelody.fragment.MusicHallFragment;
import com.papermelody.model.OnlineMusic;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class OnlineListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 音乐圈中网络作品的试听页面，其中试听部分是用Fragment实现，音乐信息、评论等内容布局在Activity中
     */

    private OnlineMusic onlineMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        // 获取从音乐圈传入的onlineMusic实例
        onlineMusic = (OnlineMusic) intent.getSerializableExtra(MusicHallFragment.SERIAL_ONLINEMUSIC);
        Log.d("TESTMUSIC", onlineMusic.getMusicName());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_online_listen;
    }
}
