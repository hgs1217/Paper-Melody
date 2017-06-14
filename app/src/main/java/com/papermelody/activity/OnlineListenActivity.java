package com.papermelody.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.fragment.CommentFragment;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.model.response.UpvoteResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static android.os.Environment.getExternalStorageDirectory;

public class OnlineListenActivity extends BaseActivity {

    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.music_view_num)
    TextView textViewNum;
    @BindView(R.id.music_upvote_num)
    TextView textUpvoteNum;
    @BindView(R.id.btn_music_upvote)
    Button btnUpvote;
    @BindView(R.id.btn_play_backward)
    Button btnPlayBack;
    @BindView(R.id.btn_play_control)
    Button btnPlayCtrl;
    @BindView(R.id.btn_play_forward)
    Button btnPlayFor;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.layout_play_control)
    LinearLayout layoutPlayControl;
    @BindView(R.id.container_online_listen)
    LinearLayout containerOnlineListen;

    @BindView(R.id.upload_comment_btn)
    Button button;
    @BindView(R.id.add_new_comment)
    EditText editText;
    @BindView(R.id.refocusCorr)
    CoordinatorLayout refocusPos;


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
    private View.OnClickListener startPlay, pausePlay, startPlayFirst;
    private boolean isUpvoted = false;
    private Integer userID = -1;
    private int upvoteNum = 0;

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
        onlineMusic = (OnlineMusic) intent.getSerializableExtra(OnlineMusic.SERIAL_ONLINEMUSIC);

        api = RetrofitClient.getSocialSystemAPI();
        dmReceiver = new DMReceiver();
        intentFilter = new IntentFilter();
        intentFilter.addAction(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        registerReceiver(dmReceiver, intentFilter);

        startPlayFirst = (View v) -> {
            fragment.starPlay();
            fab.setOnClickListener(pausePlay);
            fab.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));

            int cx = (fab.getLeft() + fab.getRight()) / 2;
            int cy = (fab.getTop() + fab.getBottom()) / 2;
            int finalRadius1 = Math.max(layoutPlayControl.getWidth(), layoutPlayControl.getHeight());
            int finalRadius2 = Math.max(containerOnlineListen.getWidth(), containerOnlineListen.getHeight());
            Animator anim1 = ViewAnimationUtils.createCircularReveal(layoutPlayControl,
                    cx, 0, 0, finalRadius1);
            Animator anim2 = ViewAnimationUtils.createCircularReveal(containerOnlineListen,
                    cx, cy, 0, finalRadius2);
            anim1.setDuration(500);
            anim2.setDuration(500);
            anim1.setInterpolator(new AccelerateInterpolator());
            anim2.setInterpolator(new AccelerateInterpolator());
            layoutPlayControl.setVisibility(View.VISIBLE);
            containerOnlineListen.setVisibility(View.VISIBLE);
            AnimatorSet animatorSet = new AnimatorSet();
            animatorSet.playTogether(anim1, anim2);
            animatorSet.start();
            fab.setVisibility(View.INVISIBLE);
            btnPlayCtrl.setOnClickListener(pausePlay);
            btnPlayCtrl.setBackground(getDrawable(android.R.drawable.ic_media_pause));
        };
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
        ctl.setExpandedTitleColor(getResources().getColor(R.color.colorAccent));
        ctl.setCollapsedTitleTextColor(getResources().getColor(R.color.white));

        if (App.getUser() != null) {
            userID = App.getUser().getUserID();
        }

        getUpvoteStatus();
        initView();
        initComment();

    }

    private void initView() {
        // FIXME: 点赞数和浏览数只有每次重进后才会刷新
        textViewNum.setText(String.valueOf(onlineMusic.getViewNum()));
        textUpvoteNum.setText(String.valueOf(onlineMusic.getUpvoteNum()));

        addViewNum();

        btnUpvote.setOnClickListener((view) -> {
            if (userID >= 0) {
                if (isUpvoted) {
                    cancelUpvote();
                    isUpvoted = false;
                } else {
                    addUpvote();
                    isUpvoted = true;
                }
                updateUpvoteIcon();
            } else {
                ToastUtil.showShort(getString(R.string.not_logged_in));
            }
        });
        File file = new File(getExternalStorageDirectory() + "/Download/" + fileName);
        fileExist = file.exists();
        if (fileExist) {
            initListenFragment();
        } else {
            downloadMusic();
//            downloadFile();       // TODO: 已经改用Retrofit获取格式，暂时没用，可以删除
//            timer.schedule(timerTask, 0, 100);
        }

        btnPlayBack.setOnClickListener((View v) -> {
            fragment.backwardPlay();
        });
        btnPlayFor.setOnClickListener((View v) -> {
            fragment.forwardPlay();
        });
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

    private void addUpvote() {
        addSubscription(api.addUpvote(userID, onlineMusic.getMusicID())
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((HttpResponse) response))
                .subscribe(
                        response -> {
                            textUpvoteNum.setText(String.valueOf(++upvoteNum));
                            ToastUtil.showShort("收藏成功");
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void cancelUpvote() {
        addSubscription(api.cancelUpvote(userID, onlineMusic.getMusicID())
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((HttpResponse) response))
                .subscribe(
                        response -> {
                            textUpvoteNum.setText(String.valueOf(--upvoteNum));
                            ToastUtil.showShort("取消收藏成功");
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void getUpvoteStatus() {
        addSubscription(api.getUpvoteStatus(userID, onlineMusic.getMusicID())
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> (UpvoteResponse) response)
                .subscribe(
                        result -> {
                            isUpvoted = result.isStatus();
                            upvoteNum = result.getUpvoteNum();
                            textUpvoteNum.setText(String.valueOf(result.getUpvoteNum()));
                            textViewNum.setText(String.valueOf(result.getViewNum()));
                            updateUpvoteIcon();
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void updateUpvoteIcon() {
        if (isUpvoted) {
            btnUpvote.setBackground(getDrawable(R.drawable.ic_thumb_up_white_18dp));  // FIXME: 白色图标和背景重合，会消失
        } else {
            btnUpvote.setBackground(getDrawable(R.drawable.ic_thumb_up_black_18dp));
        }
    }

    private void downloadMusic() {
        ToastUtil.showShort("下载中");
        addSubscription(api.downloadMusic(fileName)   // FIXME: fileName 需要修改
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> (ResponseBody) response)
                .subscribe(
                        response -> {
                            Log.d("DownloadOnlineListen", response.contentLength() + "");
                            boolean b = writeResponseBodyToDisk(response);
                            Log.d("DownloadOnlineListen", b + "");
                            initListenFragment();
                        }, NetworkFailureHandler.basicErrorHandler
                ));
    }

    /**
     * 将response获取到的文件写入到手机里
     *
     * @param body 获取到的response
     * @return 成功状态
     */
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File file = new File(getExternalStorageDirectory() + "/Download/" + fileName);
            if (file.isFile() && file.exists()) {
                file.delete();
            }
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[4096];
                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                    fileSizeDownloaded += read;
                    Log.d("DownloadOnlineListen", "file download: " + fileSizeDownloaded + " of " + fileSize);
                }
                outputStream.flush();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d("DownloadOnlineListen", "Error1");
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            Log.d("DownloadOnlineListen", "Error2");
            return false;
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
        if (App.getServerIP().endsWith("/")) {
            sourceURL = App.getServerIP() + "downloadmusic/" + fileName;
        } else {
            sourceURL = App.getServerIP() + "/downloadmusic/" + fileName;
        }
        ToastUtil.showShort(R.string.downloading);
        download_2(sourceURL, dataPath, fileName);
    }

    private void initListenFragment() {
        ToastUtil.showShort("下载完成");
        fragment = ListenFragment.newInstance(fileName);
        fragmentManager.beginTransaction().add(R.id.container_online_listen, fragment).commit();
        fab.setOnClickListener(startPlayFirst);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_online_listen;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(dmReceiver);
    }

    private void hideInput(/*Context context, View view*/) {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        /*InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    */
    }

    private void initComment() {

        button.setOnClickListener((View v) ->
                {
                    String comment = editText.getText().toString();
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String createtime = sDateFormat.format(new java.util.Date());
                    Log.d("TAG", createtime);
                    String author = "AnonymousUser"; //FIXME: 这里先方便上传，不然每次要登录
                    String musicID = String.valueOf(onlineMusic.getMusicID());
                    boolean hasUser = true;
                    try {
                        author = App.getUser().getUsername();
                    } catch (NullPointerException e) {
                        ToastUtil.showShort("登录了发表评论可以保存记录哦！");
                        hasUser = false;
                    }


                    addSubscription(api.uploadComment(musicID, author, comment, createtime)
                            .flatMap(NetworkFailureHandler.httpFailureFilter)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(response -> (HttpResponse) response)
                            .subscribe(
                                    upload_com_res -> {
                                        ToastUtil.showShort(R.string.upload_comment_success);
                                    },
                                    NetworkFailureHandler.basicErrorHandler
                            ));

                    try {
                        Thread.sleep(10);
                        editText.setText("");
                        editText.setHint(R.string.add_new_comment_here);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    hideInput();
                    refocusPos.requestFocus();
                    Log.d("TAG-ref", "OKKKKKK");
                }
        );
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
}

