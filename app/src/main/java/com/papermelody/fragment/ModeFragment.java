package com.papermelody.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.papermelody.R;
import com.papermelody.activity.MainActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ModeFragment extends BaseFragment {
    /**
     * 用例：演奏乐器（流程一）
     * 主菜单开始演奏入口
     */

    @BindView(R.id.img_mode_poster)
    ImageView imgPoster;
    @BindView(R.id.btn_mode_free)
    Button btnModeFree;
    @BindView(R.id.btn_mode_opern)
    Button btnModeOpern;

    public static ModeFragment newInstance() {
        ModeFragment fragment = new ModeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    public void initView() {
        btnModeFree.setOnClickListener((View v) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateFragment(MainActivity.MODE_FREE);
        });
        btnModeOpern.setOnClickListener((View v) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateFragment(MainActivity.MODE_OPERN);
        });
    }
}
