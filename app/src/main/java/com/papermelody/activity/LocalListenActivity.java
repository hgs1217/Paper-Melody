package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.papermelody.R;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.util.ToastUtil;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class LocalListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 本地作品的试听页面，其中试听部分是用Fragment实现，音乐信息等内容布局在Activity中
     */

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_play, ListenFragment.newInstance());
        transaction.commit();
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_local_listen;
    }
}
