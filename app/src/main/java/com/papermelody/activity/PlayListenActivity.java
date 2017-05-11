package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.Button;

import com.papermelody.R;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.util.ToastUtil;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class PlayListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 弹奏完之后的用户试听页面，其中试听部分是用Fragment实现，其它的按钮布局在Activity中
     */

    @BindView(R.id.btn_save_to_local)
    Button btnSaveToLocal;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.btn_quit_upload)
    Button btnQuitUpload;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        transaction.add(R.id.fragment_play, ListenFragment.newInstance());
        transaction.commit();
        btnSaveToLocal.setOnClickListener((View v) -> {
            copyToAndroidData();
            ToastUtil.showShort("保存成功（才怪嘞");
        });
        btnUpload.setOnClickListener((View v) -> {
            Intent intent = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent);
        });
        btnQuitUpload.setOnClickListener((View v)->{
            Intent intent = new Intent();
            intent.setClass(this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play_listen;
    }

//    从data/data/packagename复制到sdcard/Android/data/packagename
//    以便于其他应用访问，同时也属于应用数据
    private void copyToAndroidData() {
//        TODO: 以后再说吧
    }
}
