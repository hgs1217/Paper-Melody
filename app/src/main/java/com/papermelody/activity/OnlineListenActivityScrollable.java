package com.papermelody.activity;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.fragment.CommentFragment;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.fragment.MusicHallFragment;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.os.Environment.getExternalStorageDirectory;

public class OnlineListenActivityScrollable extends BaseActivity {

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.music_view_num)
    TextView viewNum;
    @BindView(R.id.music_upvote_num)
    TextView upvoteNum;
    @BindView(R.id.btn_music_upvote)
    Button btnUpvote;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private FragmentManager fragmentManager;
    private String fileName;    // 从intent中得到文件名称，下载到本地然后播放
    private SocialSystemAPI api;
    private OnlineMusic onlineMusic;
    private ListenFragment fragment = null;
    private boolean downloadSuccess = false;
    private boolean fileExist = false;
    private TimerTask timerTask;
    private Timer timer;
    private BroadcastReceiver dmReceiver;
    private IntentFilter intentFilter;
    private View.OnClickListener startPlay, pausePlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((View v) -> {
            finish();
        });

        Intent intent = getIntent();
//         获取从音乐圈传入的onlineMusic实例
        onlineMusic = (OnlineMusic) intent.getSerializableExtra(MusicHallFragment.SERIAL_ONLINEMUSIC);
        api = RetrofitClient.getSocialSystemAPI();
        dmReceiver = new DMReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(dmReceiver, intentFilter);
        startPlay = (View v) -> {
            fragment.starPlay();
            fab.setOnClickListener(pausePlay);
            fab.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
        };
        pausePlay = (View v) -> {
            fragment.pausePlay();
            fab.setOnClickListener(startPlay);
            fab.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
        };
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (downloadSuccess && fragment == null) {
                    initListenFragment();
                } else if (downloadSuccess) {
                    timer.cancel();
                }
            }
        };
        timer = new Timer();
        fileName = "Kissbye.mid";
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().add(R.id.container_comment, CommentFragment.newInstance(onlineMusic)).commit();
        ctl.setTitle(onlineMusic.getMusicName());
        ctl.setExpandedTitleMargin(10, 0, 0, 15);
        ctl.setExpandedTitleColor(getResources().getColor(R.color.black));
        initView();

    }

    private void initView() {
        // FIXME: 点赞数和浏览数只有每次重进后才会刷新
        viewNum.setText(String.valueOf(onlineMusic.getViewNum()));
        upvoteNum.setText(String.valueOf(onlineMusic.getUpvoteNum()));
        addViewNum();
        btnUpvote.setOnClickListener((view) -> {
            addUpvoteNum();  // FIXME: 存在可以多次点赞的bug
            btnUpvote.setBackground(getDrawable(R.drawable.ic_thumb_up_white_18dp));
        });
        File file = new File(getExternalStorageDirectory() + "/Download/" + fileName);
        fileExist = file.exists();
        if (fileExist) {
            initListenFragment();
        } else {
            downloadFile();
            timer.schedule(timerTask, 0, 100);
        }
    }

    private void addViewNum() {
        addSubscription(api.addView(onlineMusic.getMusicID())
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((HttpResponse) response))
                .subscribe(
                        response -> {
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void addUpvoteNum() {
        addSubscription(api.addUpvote(onlineMusic.getMusicID())
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((HttpResponse) response))
                .subscribe(
                        response -> {
                            upvoteNum.setText(String.valueOf(onlineMusic.getUpvoteNum() + 1));
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    // 这是监听是否下载完成的类
    public class DMReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                downloadSuccess = true;
            }
        }
    }

    // 调用系统的下载器下载
    private void download_2(String strurl, String path, String fileName) {
        File file = new File(getExternalStorageDirectory() + "/Download/" + fileName);
        if (file.exists()) {
            return;
        }
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(strurl));
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        downloadManager.enqueue(request);
//        Log.i("nib", Environment.DIRECTORY_DOWNLOADS + "/" + fileName);
    }

    private void downloadFile() {
        String dataPath = getApplicationContext().getFilesDir().getAbsolutePath() + "/";
        String sourceURL;
//            防止server_ip忘记加/导致无法下载的情况
        if (getString(R.string.server_ip).endsWith("/")) {
            sourceURL = getString(R.string.server_ip) + "uploaded/" + fileName;
        } else {
            sourceURL = getString(R.string.server_ip) + "/uploaded/" + fileName;
        }
        ToastUtil.showShort(R.string.downloading);
        download_2(sourceURL, dataPath, fileName);
    }

    private void initListenFragment() {
        fragment = ListenFragment.newInstance(fileName);
        fragmentManager.beginTransaction().add(R.id.container_online_listen, fragment).commit();
        fab.setOnClickListener(startPlay);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_online_listen_scrollable;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dmReceiver);
    }
}
