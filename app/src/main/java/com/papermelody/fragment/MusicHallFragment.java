package com.papermelody.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.papermelody.R;

import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class MusicHallFragment extends BaseFragment {
    /**
     * 用例：浏览音乐圈
     * 音乐圈页面，上传作品使用recyclerView排序
     */

    public static MusicHallFragment newInstance() {
        MusicHallFragment fragment = new MusicHallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_hall, container, false);
        ButterKnife.bind(this, view);
        return view;
    }
}
