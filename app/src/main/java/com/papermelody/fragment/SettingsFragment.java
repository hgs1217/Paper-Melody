package com.papermelody.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.papermelody.R;
import com.papermelody.activity.AboutActivity;
import com.papermelody.activity.SettingsPlayActivity;
import com.papermelody.activity.SettingsPrivacyActivity;
import com.papermelody.activity.TutorialActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class SettingsFragment extends BaseFragment {
    /**
     * 用例：修改设置
     * 设置主页面
     */

    @BindView(R.id.btn_settings_play)
    Button btnSettingsPlay;
    @BindView(R.id.btn_settings_privacy)
    Button btnSettingsPrivacy;
    @BindView(R.id.btn_about)
    Button btnAbout;
    @BindView(R.id.btn_tutorial)
    Button btnTutorial;

    public static SettingsFragment newInstance() {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        btnSettingsPlay.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), SettingsPlayActivity.class);
            startActivity(intent);
        });
        btnSettingsPrivacy.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), SettingsPrivacyActivity.class);
            startActivity(intent);
        });
        btnAbout.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });
        btnTutorial.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), TutorialActivity.class);
            startActivity(intent);
        });
    }
}
