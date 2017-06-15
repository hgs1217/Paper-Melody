package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.Message;

import java.util.ArrayList;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/6/15.
 */

public class MessageActivity extends BaseActivity {
    /**
     * 查看评论回复和系统通知
     */

    @BindView(R.id.text_msg)
    TextView textMsg;

    private ArrayList<Message> messages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        messages = (ArrayList<Message>) intent.getSerializableExtra(Message.SERIAL_MESSAGE);

        String tmp = "";
        for (Message msg : messages) {
            tmp += msg.getMessage();
            tmp += "\n";
        }

        textMsg.setText(tmp);
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_message;
    }
}
