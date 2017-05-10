package com.papermelody.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.papermelody.R;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CommentActivity extends BaseActivity {
    /**
     * 用例：用户评论
     * 用户评论页面
     */
     /*表明一下自己开始码代码了*/

    @BindView(R.id.upload_comment_btn)
    Button button;
    @BindView(R.id.add_new_comment)
    EditText editText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Log.i("TEST", "button onClick");
            }
        });
    }


    @Override
    protected int getContentViewId() {

        return R.layout.activity_comment;
    }
}
