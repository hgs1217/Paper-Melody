package com.papermelody.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.papermelody.util.ToastUtil;
import com.papermelody.widget.MessageRecyclerViewAdapter;

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

//    @BindView(R.id.text_msg)
//    TextView textMsg;
    @BindView(R.id.layout_message_refresh)
    SwipeRefreshLayout layoutMessageRefresh;
    @BindView(R.id.recycler_message)
    RecyclerView recyclerViewMessage;

    private Context context;
    private ArrayList<Message> messages;
    private MessageRecyclerViewAdapter adapter;

    private MessageRecyclerViewAdapter.OnItemClickListener messageOnItemClickListener = new
            MessageRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(Message message) {
                    //TODO
                    ToastUtil.showShort("Message clicked");
                }
            };

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
        context = view.getContext();

        if (App.getUser() != null) {
            getMessage();
            initSwipeRefreshView();
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
                            initRecyclerView(messages);
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void initRecyclerView(List<Message> messages) {
        adapter = new MessageRecyclerViewAdapter(context, messages);
        adapter.setOnItemClickListener(messageOnItemClickListener);
        recyclerViewMessage.setAdapter(adapter);
        recyclerViewMessage.setLayoutManager(new LinearLayoutManager(context));
        recyclerViewMessage.setItemAnimator(new DefaultItemAnimator());
    }

    private void initSwipeRefreshView() {
        layoutMessageRefresh.setColorSchemeResources(R.color.colorAccent);
        layoutMessageRefresh.setProgressBackgroundColorSchemeResource(R.color.white);
        layoutMessageRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        layoutMessageRefresh.setProgressViewEndTarget(true, 100);
        layoutMessageRefresh.setOnRefreshListener(() -> {
            getMessage();
            layoutMessageRefresh.setRefreshing(false);
        });
    }
}
