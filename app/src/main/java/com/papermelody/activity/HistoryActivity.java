package com.papermelody.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.papermelody.R;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public class HistoryActivity extends BaseActivity {
    /**
     * 查看历史记录，听已有的本地录音
     */
    @BindView(R.id.upload_comment_btn2)
    Button button;

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
        return R.layout.activity_history;
    }
}
