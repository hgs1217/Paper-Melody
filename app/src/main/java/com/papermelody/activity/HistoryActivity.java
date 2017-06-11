package com.papermelody.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.papermelody.R;
import com.papermelody.model.HistoryMusic;
import com.papermelody.util.ToastUtil;
import com.papermelody.widget.HistoryItemRecyclerViewAdapter;

import java.io.File;
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

    private String[] datas = new String[]{"MUSIC 1", "MUSIC 2", "A", "B"};

    private Context context;
    private HistoryItemRecyclerViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ctl.setExpandedTitleMarginBottom(5);
        initRecyclerView();
        getFileDir(getFilesDir().getAbsolutePath());
    }


    private void initRecyclerView() {
        adapter = new HistoryItemRecyclerViewAdapter(datas);
        //// TODO: 2017-6-10 0010
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    private List<HistoryMusic> historyMusic = new ArrayList<HistoryMusic>();

    public void getFileDir(String filePath) {
        try {
            File f = new File(filePath);
            Log.d("FILEE", "1");
            File[] files = f.listFiles();// 列出所有文件
            Log.d("FILEE", "2");
            if (files != null) {
                int count = files.length;// 文件个数
                for (int i = 0; i < count; i++) {
                    File file = files[i];
                    Log.d("FILEE", "3");
                    try {
                        if (true || file.getName().split(".")[-1].equals("mp3")) {
                            Log.d("FILEE", "4");
                            historyMusic.add(new HistoryMusic(
                                    file.getName(), file.lastModified(), file.length()));
                        }
                    } catch (Exception e) {
                        ToastUtil.showShort("Read this error, but continuing");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("FILEE", e.toString());

            ToastUtil.showShort("Wrong Reading");
        }
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_history;
    }
}
