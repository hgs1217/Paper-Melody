package com.papermelody.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.Comment;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.CommentInfo;
import com.papermelody.model.response.CommentResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.widget.CommentRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.papermelody.model.OnlineMusic.SERIAL_ONLINEMUSIC;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CommentFragment extends BaseFragment {
    /**
     * 用例：用户评论
     * 用户评论页面，作为Fragment放置于OnlineListenActivity中
     */


    @BindView(R.id.current_comment_list)
    RecyclerView commentList;
    @BindView(R.id.user_newest_comment_not_exist)
    TextView userNoComment;
    @BindView(R.id.my_comment_name)
    TextView myCommentName;
    @BindView(R.id.my_comment_context)
    TextView myCommentContext;
    @BindView(R.id.my_comment_time)
    TextView myCommentTime;

    @BindView(R.id.my_comment_overall)
    LinearLayout my_comment_overall;
    @BindView(R.id.all_comment_title)
    TextView refocusPos;

    private boolean hasUser;
    private String author = "AnnonymousUser";
    private SocialSystemAPI api;
    private OnlineMusic onlineMusic;
    private Context context;
    private CommentRecyclerViewAdapter adapter;
    private CommentRecyclerViewAdapter.OnItemClickListener commentOnItemClickListener = new
            CommentRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick() {
                    //Click activated only on 'Comment Button'
                }
            };

    public static CommentFragment newInstance(OnlineMusic onlineMusic) {
        CommentFragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(OnlineMusic.SERIAL_ONLINEMUSIC, onlineMusic);
        fragment.setArguments(bundle);
        return fragment;
    }

    private void checkIfHasUser() {
        try {
            author = App.getUser().getUsername();
            hasUser = true;
        } catch (NullPointerException e) {
            Log.d("TAG_USER", "NO USER LOGGED");
            //ToastUtil.showShort("登录了发表评论可以保存记录哦！");
            hasUser = false;
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onlineMusic = (OnlineMusic) getArguments().getSerializable(SERIAL_ONLINEMUSIC);
        checkIfHasUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.bind(this, view);
        checkIfHasUser();
        my_comment_overall.setVisibility(View.GONE);
        userNoComment.setVisibility(View.GONE);
        context = getActivity();
        api = RetrofitClient.getSocialSystemAPI();
        checkIfHasUser();
        initGetCommentList();
        initView();
        return view;
    }


    public void initGetCommentList() {
        String musicID = String.valueOf(onlineMusic.getMusicID());
        addSubscription(api.getComment(musicID)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((CommentResponse) response).getResult().getComments()) /*list的commentinfo*/
                .subscribe(
                        commentList -> {
                            List<Comment> comments = new ArrayList<>();
                            for (CommentInfo info : commentList) {
                                comments.add(new Comment(info));
                            }
                            Collections.sort(comments, new Comparator() {
                                @Override
                                public int compare(Object o1, Object o2) {
                                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                                    Comment a = (Comment) o1;
                                    Comment b = (Comment) o2;
                                    return b.getCreateTime().compareTo(
                                            a.getCreateTime());
                                }
                            });
                            //System.out.print("sorted!");
                            SimpleDateFormat sDateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            try {
                                Log.d("TAG", "before init View: " + comments.get(0).getCreateTime());
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            Log.d("TAG2", "!!!!!");
                            initRecyclerView(comments);
                            refreshMyComment(comments);
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));

    }

    private void refreshMyComment(List<Comment> list) {
        if (!hasUser) {
            userNoComment.setText(R.string.not_logged_in);
            my_comment_overall.setVisibility(View.GONE);
            userNoComment.setVisibility(View.VISIBLE);
        } else {
            boolean hasCommented = false;
            for (Comment singleComment : list) {
                if (singleComment.getAuthor().equals(author)) {
                    hasCommented = true;
                    my_comment_overall.setVisibility(View.VISIBLE);
                    userNoComment.setVisibility(View.GONE);
                    myCommentContext.setText(singleComment.getContent());
                    myCommentTime.setText(timeLongToString(Long.parseLong(
                            singleComment.getCreateTime())));
                    myCommentName.setText(singleComment.getAuthor());
                    break;
                }
            }
            if (!hasCommented) {
                userNoComment.setText(R.string.user_comment_not_exist);
                my_comment_overall.setVisibility(View.GONE);
                userNoComment.setVisibility(View.VISIBLE);
            }
        }
    }

    private void initRecyclerView(List<Comment> comments) {
        adapter = new CommentRecyclerViewAdapter(context, comments);
        adapter.setOnItemClickListener(commentOnItemClickListener);
        commentList.setAdapter(adapter);
        commentList.setLayoutManager(new LinearLayoutManager(context));
        commentList.setItemAnimator(new DefaultItemAnimator());
    }

    private String timeLongToString(Long m) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd  HH:mm:ss");
        String time = sdf.format(new Date(m));
        return time;
    }

    private void initView() {
        /*refreshbtn.setOnClickListener((View vx)->
        {
            //commentList.setAdapter(new ArrayAdapter<String>(HistoryActivity.this,
              //      android.R.layout.simple_list_item_1, getData()));
        }
        );
        */
/*

        button.setOnClickListener((View v) -> {
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
                        initGetCommentList();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    if (hasUser) {
                        userNoComment.setVisibility(View.GONE);
                        myCommentContext.setText("");
                    }
                    hideInput(context, this.getView());
                    refocusPos.requestFocus();
                    Log.d("TAG-ref", "OKKKKKK");
                }
        );*/
    }

    private void hideInput(Context context, View view) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
