package com.papermelody.activity;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.papermelody.R;
import com.papermelody.model.HistoryMusic;
import com.papermelody.util.ToastUtil;
import com.papermelody.widget.HistoryItemRecyclerViewAdapter;
import com.papermelody.widget.HistoryItemRecyclerViewDecoration;
import com.papermelody.widget.HistoryItemRecyclerViewLayoutManager;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class HistoryActivity extends BaseActivity {
    /**
     * 用于展示历史作品（保存在本地的作品）
     * 方法：直接在本地读取记录
     * 路径：(File)Environment.getExternalStorageDirectory
     */
    @BindView(R.id.collapsing_toolbar_history)
    CollapsingToolbarLayout ctl;
    @BindView(R.id.history_item_list)
    RecyclerView mRecyclerView;

    private String[] musicCaption = new String[]{"MUSIC 1", "MUSIC 2", "A", "B", "HGS", "ZB", "TTH"};
    private Context context;
    private String musicExtendedName = ".txt";
    private HistoryItemRecyclerViewAdapter adapter;
    private List<HistoryMusic> historyMusic = new ArrayList<HistoryMusic>();


    private void __TEST() {
        try {
            for (int i = 0; i < 5; ++i) {
                File file = new File(Environment.getExternalStorageDirectory(),
                        "Music" + Integer.toString(i) + musicExtendedName);
                FileOutputStream fos = new FileOutputStream(file);
                String info = "I am a chinese!";
                fos.write(info.getBytes());
                fos.close();
                Log.d("FILEE", "写入成功");
                Thread.sleep(1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ctl.setExpandedTitleMarginBottom(5);
        initRecyclerView();
        getFileDir(Environment.getExternalStorageDirectory().getAbsolutePath());
        //Log.d("FILEPATH", Environment.getExternalStorageDirectory().getAbsolutePath());
    }


    private void initRecyclerView() {
        adapter = new HistoryItemRecyclerViewAdapter(musicCaption, historyMusic);
        //// TODO: 2017-6-10
        mRecyclerView.addItemDecoration(new HistoryItemRecyclerViewDecoration(1));
        mRecyclerView.setAdapter(adapter);
        mRecyclerView.setLayoutManager(new HistoryItemRecyclerViewLayoutManager());
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
    }


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
                        String __name = file.getName();

                        if (__name.length() > 4 &&
                                __name.substring(__name.length() - 4, __name.length()).equals(
                                        musicExtendedName)) {
                            Log.d("FILEE", "4");
                            historyMusic.add(new HistoryMusic(
                                    file.getName(), file.lastModified(), file.length()));
                            historyMusic.get(historyMusic.size() - 1).__TEST();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        //  ToastUtil.showShort("Read this error, but continuing");
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
