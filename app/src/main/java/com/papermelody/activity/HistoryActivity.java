package com.papermelody.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.papermelody.R;
import com.papermelody.widget.HistoryItemRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;


public class HistoryActivity extends BaseActivity {
    /**
     * 用于展示历史作品（保存在本地的作品）
     */
    @BindView(R.id.collapsing_toolbar_history)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.history_item_list)
    RecyclerView mRecyclerView;
    private String[] datas = new String[]{"MUSIC 1","MUSIC 2", "A","B"};

    private Context context;
    private HistoryItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ctl.setExpandedTitleMarginBottom(5);
        initRecyclerView();
    }



    private void initRecyclerView() {
        adapter = new HistoryItemRecyclerViewAdapter(datas);
        //// TODO: 2017-6-10 0010
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_history;
    }
}
