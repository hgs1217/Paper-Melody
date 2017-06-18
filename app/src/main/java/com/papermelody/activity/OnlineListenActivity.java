package com.papermelody.activity;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Environment;
import android.provider.ContactsContract;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AccelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.fragment.CommentFragment;
import com.papermelody.fragment.ListenFragment;
import com.papermelody.model.Comment;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.model.response.UpvoteResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import okhttp3.ResponseBody;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    private CommentFragment comment_fragment = null;
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
    private Comment replyToThisComment = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((View v) -> {
            finish();
        });

        Intent intent = getIntent();
        // 获取从音乐圈传入的onlineMusic实例
        onlineMusic = (OnlineMusic) intent.getSerializableExtra(OnlineMusic.SERIAL_ONLINEMUSIC);

        fileName = onlineMusic.getFilename();
        Log.i("nib", fileName);
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
            btnPlayCtrl.setBackground(getDrawable(R.drawable.ic_pause_circle_outline_white_48dp));
        };
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
        File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);
        Log.i("nib", file.getAbsolutePath());
        fileExist = file.exists();
        if (fileExist) {
            Log.i("nib", "file exsits");
            initListenFragment();
        } else {
            Log.i("nib", "downloading");
            downloadMusic();
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
            btnUpvote.setBackground(getDrawable(R.drawable.ic_favorite_black_48dp));
        } else {
            btnUpvote.setBackground(getDrawable(R.drawable.ic_favorite_border_black_48dp));
        }
    }

    private void downloadMusic() {
        ToastUtil.showShort("下载中");
        addSubscription(api.downloadMusic(fileName)
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
            // TODO: error code 404
            Log.i("nib", fileName);
            File file = new File(getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName);
            Log.i("nib", file.getAbsolutePath());
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

    private void initListenFragment() {
        ToastUtil.showShort("下载完成");
        fragment = ListenFragment.newInstance(
                getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS) + "/" + fileName,
                onlineMusic.getMusicPhotoUrl()
        );
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
        //   getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showInput() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.SHOW_FORCED);
    }


    private void checkIfIsReplyingToAComment(String comment) {
        if (replyToThisComment != null) {
            String header = '@' + replyToThisComment.getAuthor() + ":";
            if (!comment.contains(header))
                replyToThisComment = null;
        }
    }

    private void initComment() {

        button.setOnClickListener((View v) ->
                {
                    String __label = getString(R.string.__label);
                    String comment = editText.getText().toString();
                    Log.d("II", comment);
                    if (comment.contains("'"))
                        comment = comment.replaceAll("'", "\"");
                    Log.d("II", "NOW:" + comment);
                    if (comment.length() < 1) {
                        ToastUtil.showShort("请填写评论之后再提交");
                    } else if (comment.contains(__label))
                        ToastUtil.showShort("恭喜你解锁了内部标识符！" + __label + "是一个内部标记，用户不能使用哦");
                    else {
                        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                        String createtime = sDateFormat.format(new java.util.Date().getTime());
                        Log.d("TAG", createtime);
                        createtime = Long.toString(new java.util.Date().getTime());
                        Integer replyUserID = 0;
                        String author = "AnonymousUser"; // FIXME: 这里先方便上传，不然每次要登录
                        Integer authorID = 0;
                        String authorAvatar = ""; // FIXME: 设定一下初始头像
                        Integer musicID = onlineMusic.getMusicID();
                        boolean hasUser = true;
                        try {
                            author = App.getUser().getUsername();
                            authorID = App.getUser().getUserID();
                            authorAvatar = App.getUser().getAvatarName();
                        } catch (NullPointerException e) {
                            ToastUtil.showShort("登录了发表评论可以保存记录哦！");
                            hasUser = false;
                        }

                        checkIfIsReplyingToAComment(comment);

                        if (replyToThisComment != null) {
                            String replyToThisCommentContent = removeLabel(replyToThisComment.getContent(), __label);
                            replyUserID = replyToThisComment.getAuthorID();
                            comment = comment + __label + replyToThisComment.getAuthor() + __label +
                                    replyToThisComment.getCreateTime() + __label + replyToThisCommentContent;
                        } else {
                            replyUserID = 0;
                        }

                        addSubscription(api.uploadComment(musicID, author, authorID, authorAvatar, replyUserID, comment, createtime)
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
                        replyToThisComment = null;
                        hideInput();
                        Timer timer = new Timer();//实例化Timer类
                        timer.schedule(new TimerTask() {
                            public void run() {
                                Log.d("FILEE", "delay 800ms");
                                this.cancel();
                            }
                        }, 800);
                        freshCommentFragment();
                        refocusPos.requestFocus();
                        Log.d("TAG-ref", "OKKKKKK");
                    }
                }
        );
    }

    private String removeLabel(String res, String label) {
        int idx = res.indexOf(label);
        if (idx >= 0)
            return res.substring(0, idx);
        else
            return res;
    }

    private String timeLongToString(long m) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm", Locale.CHINA);
        String time = sdf.format(new Date(m));
        return time;
    }

    private void freshCommentFragment() {
        comment_fragment = CommentFragment.newInstance(onlineMusic);
        FragmentTransaction trans = fragmentManager.beginTransaction();
        trans.setCustomAnimations(R.anim.comment_show, R.anim.comment_gone);
        trans.replace(R.id.container_comment, comment_fragment).commit();
    }

    public void focusOnEdit(Comment replyToThis) {
        String generateText = "@" + replyToThis.getAuthor() + ": ";
        editText.setText(generateText);
        editText.setSelection(generateText.length());
        editText.requestFocus();
        showInput();
        replyToThisComment = replyToThis;
    }

    public void __test() {
        ;
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

