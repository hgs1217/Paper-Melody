package com.papermelody.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.papermelody.R;
import com.papermelody.model.Comment;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.CommentInfo;
import com.papermelody.model.response.CommentResponse;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;
import com.papermelody.widget.CommentRecyclerViewAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import static com.papermelody.fragment.MusicHallFragment.SERIAL_ONLINEMUSIC;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CommentFragment extends BaseFragment {
    /**
     * 用例：用户评论
     * 用户评论页面，作为Fragment放置于OnlineListenActivity中
     */

    @BindView(R.id.upload_comment_btn)
    Button button;
    @BindView(R.id.add_new_comment)
    EditText editText;
    @BindView(R.id.current_comment_list)
    RecyclerView commentList;


    private SocialSystemAPI api;
    private OnlineMusic onlineMusic;
    private Context context;
    private CommentRecyclerViewAdapter adapter;
    private CommentRecyclerViewAdapter.OnItemClickListener commentOnItemClickListener = new
            CommentRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick() {
                    // TODO：
                }
            };

    public static CommentFragment newInstance(OnlineMusic onlineMusic) {
        CommentFragment fragment = new CommentFragment();
        Bundle bundle = new Bundle();
        bundle.putSerializable(SERIAL_ONLINEMUSIC, onlineMusic);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        onlineMusic = (OnlineMusic) getArguments().getSerializable(SERIAL_ONLINEMUSIC);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_comment, container, false);
        ButterKnife.bind(this, view);

        context = getActivity();
        api = RetrofitClient.getSocialSystemAPI();
        initGetCommentList();
        initView();
        return view;
    }

    private ArrayList<String> getData() {
        String musicID = "music1";
        ArrayList<String> listx = new ArrayList<>();

        listx.add("I like it!");
        listx.add("This is good.");
        return listx;
    }

    private void initGetCommentList() {
        String musicID = "music1";   // FIXME: 现在仅供测试，之后需修改为真实的musicID
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
                                    Comment a = (Comment) o1;
                                    Comment b = (Comment) o2;
                                    return b.getCreateTime().toString().compareTo(
                                            a.getCreateTime().toString());
                                }
                            });
                            System.out.print("sorted!");
                            initRecyclerView(comments);
                        },
                        NetworkFailureHandler.loginErrorHandler
                ));
    }

    private void initRecyclerView(List<Comment> comments) {
        adapter = new CommentRecyclerViewAdapter(context, comments);
        adapter.setOnItemClickListener(commentOnItemClickListener);
        commentList.setAdapter(adapter);
        commentList.setLayoutManager(new LinearLayoutManager(context));
        commentList.setItemAnimator(new DefaultItemAnimator());
    }

    private void initView() {
        /*refreshbtn.setOnClickListener((View vx)->
        {
            //commentList.setAdapter(new ArrayAdapter<String>(HistoryActivity.this,
              //      android.R.layout.simple_list_item_1, getData()));
        }
        );
        */

        button.setOnClickListener((View v) -> {
                    String comment = editText.getText().toString();
                    SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    String createtime = sDateFormat.format(new java.util.Date());
                    String author = "AnonymousUser"; //FIXME: 这里先方便上传，不然每次要登录
                    String musicID = "music1";  // FIXME: 需修改为真实的ID
                    boolean hasUser = true;
                    try {
                        author = ((App) getActivity().getApplication()).getUser().getUsername();
                    } catch (NullPointerException e) {
                        ToastUtil.showShort("登录了发表评论可以保存记录哦！");
                        //hasUser = false;
                    }

                    if (hasUser) {
                        addSubscription(api.uploadComment(musicID, author, comment, createtime)
                                .flatMap(NetworkFailureHandler.httpFailureFilter)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(response -> (HttpResponse) response)
                                .subscribe(
                                        upload_com_res -> {
                                            ToastUtil.showShort(R.string.upload_comment_success);
                                        },
                                        NetworkFailureHandler.loginErrorHandler
                                ));
                    }

                    try {
                        Thread.sleep(700);
                        editText.setText("");
                        editText.setHint(R.string.add_new_comment_here);
                        initGetCommentList();

                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
        );
    }
}
