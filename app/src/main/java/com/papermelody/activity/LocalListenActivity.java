package com.papermelody.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.papermelody.R;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.model.LocalMusic;
import com.papermelody.util.ToastUtil;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class LocalListenActivity extends BaseActivity {
    /**
     * 用例：用户试听
     * 本地作品的试听页面，其中试听部分是用Fragment实现，音乐信息等内容布局在Activity中
     */

    @BindView(R.id.toolbar_local_listen)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.fab_local_listen)
    FloatingActionButton fab;
    @BindView(R.id.btn_play_backward)
    Button btnPlayBack;
    @BindView(R.id.btn_play_control)
    Button btnPlayCtrl;
    @BindView(R.id.btn_play_forward)
    Button btnPlayFor;
    @BindView(R.id.layout_local_listen)
    RelativeLayout layoutLocalListen;
    @BindView(R.id.container_local_listen)
    LinearLayout containerLocalListen;
    @BindView(R.id.tip_listen_first)
    LinearLayout tipListenFirst;

    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private ListenFragment fragment = null;
    private View.OnClickListener startPlay, pausePlay;
    private String fileName = "";
    private long lastClickTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        fileName = intent.getStringExtra(PlayActivity.FILENAME);

        initView();
    }

    private void initView() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((View v) -> {
            finish();
        });
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        fragment = ListenFragment.newInstance(getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath() + "/" + fileName, null);

        transaction.add(R.id.container_local_listen, fragment);
        transaction.commit();
        startPlay = (View v) -> {
            fragment.starPlay();
            btnPlayCtrl.setOnClickListener(pausePlay);
            btnPlayCtrl.setBackground(getDrawable(R.drawable.ic_pause_circle_outline_white_48dp));
        };
        pausePlay = (View v) -> {
            fragment.pausePlay();
            btnPlayCtrl.setOnClickListener(startPlay);
            btnPlayCtrl.setBackground(getDrawable(R.drawable.ic_play_circle_outline_white_48dp));
        };
        btnPlayCtrl.setOnClickListener(pausePlay);
        btnPlayBack.setOnClickListener((View v) -> {
            fragment.backwardPlay();
        });
        btnPlayFor.setOnClickListener((View v) -> {
            fragment.forwardPlay();
        });
        fab.setOnClickListener((View v) -> {
            tipListenFirst.setVisibility(View.INVISIBLE);
            fragment.starPlay();
            int cx = (fab.getLeft() + fab.getRight()) / 2;
            int cy = (fab.getTop() + fab.getBottom()) / 2;
            int finalRadius1 = Math.max(layoutLocalListen.getWidth(), layoutLocalListen.getHeight());
            Animator anim1 = ViewAnimationUtils.createCircularReveal(layoutLocalListen,
                    cx, 0, 0, finalRadius1);
            int finalRadius2 = Math.max(containerLocalListen.getWidth(), containerLocalListen.getHeight());
            Animator anim2 = ViewAnimationUtils.createCircularReveal(containerLocalListen,
                    cx, cy, 0, finalRadius2);
            anim1.setDuration(500);
            anim2.setDuration(500);
            anim1.setInterpolator(new AccelerateInterpolator());
            anim2.setInterpolator(new AccelerateInterpolator());
            fab.setVisibility(View.INVISIBLE);
            layoutLocalListen.setVisibility(View.VISIBLE);
            containerLocalListen.setVisibility(View.VISIBLE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(anim1, anim2);
            animatorSet.start();
            btnPlayCtrl.setBackground(getDrawable(R.drawable.ic_pause_circle_outline_white_48dp));
        });
        ctl.setTitle(getString(R.string.play_listen));
        ctl.setExpandedTitleMargin(10, 0, 0, 15);
        ctl.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        ctl.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_local_listen;
    }
}
