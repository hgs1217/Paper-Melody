package com.papermelody.activity;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.papermelody.R;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CommentActivity extends BaseActivity {
    /**
     * 用例：用户评论
     * 用户评论页面
     */

    @BindView(R.id.upload_comment_btn)
    Button button;
    @BindView(R.id.add_new_comment)
    EditText editText;

    private SocialSystemAPI api;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        api = RetrofitClient.getSocialSystemAPI();
        initView();
    }


    private void initView() {
        button.setOnClickListener((View v) -> {
            String comment = editText.getText().toString();
            SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            String createtime = sDateFormat.format(new java.util.Date());
            String author = "USER";
            String musicID = "music1";
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
        });
    }


    @Override
    protected int getContentViewId() {

        return R.layout.activity_comment;
    }
}
