package com.papermelody.activity;

import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.response.HttpResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class HistoryActivity extends BaseActivity {
    /**
     * 用于展示历史作品（保存在本地的作品）
     */

    @BindView(R.id.upload_comment_btn)
    Button button;
    @BindView(R.id.add_new_comment)
    EditText editText;
    @BindView(R.id.current_comment_list)
    ListView commentList;
    @BindView(R.id.textView1)
    TextView com1;
    @BindView(R.id.textView11)
    TextView com11;
    @BindView(R.id.textView2)
    TextView com2;
    @BindView(R.id.textView22)
    TextView com22;

    private SocialSystemAPI api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
            String author = "AnonymousUser";
            String musicID = "music1";
            boolean hasUser = true;
            try {
                author = ((App) getApplication()).getUser().getUsername();
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
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            com2.setText(com1.getText());
            com22.setText(com11.getText());
            com1.setText(comment);
            com11.setText(createtime + " by " + author);
        });
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_history;
    }
}
