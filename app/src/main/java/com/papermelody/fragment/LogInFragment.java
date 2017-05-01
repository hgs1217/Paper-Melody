package com.papermelody.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.papermelody.R;
import com.papermelody.activity.MainActivity;
import com.papermelody.model.User;
import com.papermelody.util.App;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class LogInFragment extends BaseFragment {
    /**
     * 用例：注册、登录
     * 注册与登录页面，（暂弃用：此页面包含2个Fragment）
     */

    @BindView(R.id.edit_username)
    EditText editUsername;
    @BindView(R.id.edit_password)
    EditText editPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_log_in)
    Button btnLogIn;

    public static LogInFragment newInstance() {
        LogInFragment fragment = new LogInFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_in, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        btnRegister.setOnClickListener((View v) -> {
            // TODO:
        });
        btnLogIn.setOnClickListener((View v) -> {
            // TODO:

            // TEST:
            User user = new User();
            user.setUsername("ssb");
            ((App) getActivity().getApplication()).setUser(user);
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateFragment(2);
        });
    }
}
