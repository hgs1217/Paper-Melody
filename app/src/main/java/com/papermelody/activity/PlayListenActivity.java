package com.papermelody.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.papermelody.R;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.model.LocalMusic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class PlayListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 弹奏完之后的用户试听页面，其中试听部分是用Fragment实现，其它的按钮布局在Activity中
     */

    @BindView(R.id.toolbar_play_listen)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.fab_play_listen)
    FloatingActionButton fab;
    @BindView(R.id.btn_play_backward)
    Button btnPlayBack;
    @BindView(R.id.btn_play_control)
    Button btnPlayCtrl;
    @BindView(R.id.btn_play_forward)
    Button btnPlayFor;
    @BindView(R.id.btn_upload)
    Button btnUpload;
    @BindView(R.id.btn_quit_upload)
    Button btnQuitUpload;
    @BindView(R.id.layout_play_listen)
    RelativeLayout layoutPlayListen;
    @BindView(R.id.container_play_listen)
    LinearLayout containerPlayListen;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private ListenFragment fragment = null;
    private View.OnClickListener startPlay, pausePlay;
    private String fileName;
    private LocalMusic localMusic;

// TODO:   没有经过调试，可能会有bug

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
//        localMusic = ???

        fileName = "Kissbye.mid";
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((View v) -> {
            finish();
        });
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragment = ListenFragment.newInstance(fileName);
        transaction.add(R.id.container_play_listen, fragment);
        transaction.commit();
        startPlay = (View v) -> {
            fragment.starPlay();
            btnPlayCtrl.setOnClickListener(pausePlay);
            btnPlayCtrl.setBackground(getDrawable(android.R.drawable.ic_media_pause));
        };
        pausePlay = (View v) -> {
            fragment.pausePlay();
            btnPlayCtrl.setOnClickListener(startPlay);
            btnPlayCtrl.setBackground(getDrawable(android.R.drawable.ic_media_play));
        };
        btnPlayCtrl.setOnClickListener(pausePlay);
        btnPlayBack.setOnClickListener((View v) -> {
            fragment.backwardPlay();
        });
        btnPlayFor.setOnClickListener((View v) -> {
            fragment.forwardPlay();
        });
        fab.setOnClickListener((View v) -> {
            fragment.starPlay();
            int cx = (fab.getLeft() + fab.getRight()) / 2;
            int cy = (fab.getTop() + fab.getBottom()) / 2;
            int finalRadius1 = Math.max(layoutPlayListen.getWidth(), layoutPlayListen.getHeight());
            Animator anim1 = ViewAnimationUtils.createCircularReveal(layoutPlayListen,
                    cx, 0, 0, finalRadius1);
            int finalRadius2 = Math.max(containerPlayListen.getWidth(), containerPlayListen.getHeight());
            Animator anim2 = ViewAnimationUtils.createCircularReveal(containerPlayListen,
                    cx, cy, 0, finalRadius2);
            anim1.setDuration(500);
            anim2.setDuration(500);
            anim1.setInterpolator(new AccelerateInterpolator());
            anim2.setInterpolator(new AccelerateInterpolator());
            fab.setVisibility(View.INVISIBLE);
            layoutPlayListen.setVisibility(View.VISIBLE);
            containerPlayListen.setVisibility(View.VISIBLE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(anim1, anim2);
            animatorSet.start();
            btnPlayCtrl.setBackground(getDrawable(android.R.drawable.ic_media_pause));
        });
        ctl.setTitle(getString(R.string.play_listen));
        ctl.setExpandedTitleMargin(10, 0, 0, 15);
        ctl.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        ctl.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
        btnUpload.setOnClickListener((View v) -> {
            Intent intent1 = new Intent(getApplicationContext(), UploadActivity.class);
            startActivity(intent1);
        });
        btnQuitUpload.setOnClickListener((View v) -> {
            Intent intent2 = new Intent();
            intent2.setClass(this, MainActivity.class);
            startActivity(intent2);
            finish();
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play_listen;
    }

    //    从data/data/packagename复制到sdcard/Android/data/packagename
//    以便于其他应用访问，同时也属于应用数据
    private void copyToAndroidData(String sourcePath, String destPath) {
        Log.i("nib", "dest: " + destPath + "\n" + "source:" + sourcePath);
        try {
            InputStream inputStream = new FileInputStream(sourcePath);
            OutputStream outputStream = new FileOutputStream(destPath);
            byte bt[] = new byte[1024];
            int c;
            while ((c = inputStream.read(bt)) > 0) {
                outputStream.write(bt, 0, c);
            }
            inputStream.close();
            outputStream.close();
            Log.i("nib", "复制完成");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
