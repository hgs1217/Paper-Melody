package com.papermelody.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.model.Message;
import com.papermelody.model.response.MessageInfo;
import com.papermelody.model.response.MessageResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/6/15.
 */

public class MessageFragment extends BaseFragment {
    /**
     * 查看评论回复和系统通知
     */

    @BindView(R.id.text_msg)
    TextView textMsg;

    private ArrayList<Message> messages;

    public static MessageFragment newInstance() {
        MessageFragment fragment = new MessageFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        ButterKnife.bind(this, view);

        if (App.getUser() != null) {
            getMessage();
        }

        return view;
    }

    private void getMessage() {
        SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.getMessages(App.getUser().getUserID(), true)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((MessageResponse) response).getResult())
                .subscribe(
                        result -> {
                            List<MessageInfo> infoList = result.getMessages();
                            messages = new ArrayList<>();
                            for (MessageInfo info : infoList) {
                                messages.add(new Message(info));
                            }

                            String tmp = "";
                            for (Message msg : messages) {
                                tmp += msg.getMessage();
                                tmp += "\n";
                            }

                            textMsg.setText(tmp);
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }
}
