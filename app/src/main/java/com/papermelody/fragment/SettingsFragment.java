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

public class SettingsFragment extends BaseFragment {
    /**
     * 用例：修改设置
     * 设置主页面
     */


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
        //getFragmentManager().beginTransaction().replace(
        //       R.id.frameLayout_setting, new SettingsPreferenceFragment()).commit();
        ButterKnife.bind(this, view);
        return view;
    }


}
