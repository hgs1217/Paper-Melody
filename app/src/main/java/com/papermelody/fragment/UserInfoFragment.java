package com.papermelody.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.papermelody.R;

import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/5/27.
 */

public class UserInfoFragment extends BaseFragment  {
    /**
     * 用例：查看用户信息
     * 查看用户个人信息页面
     */

    public static UserInfoFragment newInstance() {
        UserInfoFragment fragment = new UserInfoFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user_info, container, false);
        ButterKnife.bind(this, view);

        return view;
    }
}
