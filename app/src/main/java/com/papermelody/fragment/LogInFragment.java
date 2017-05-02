package com.papermelody.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.papermelody.R;
import com.papermelody.activity.MainActivity;
import com.papermelody.model.User;
import com.papermelody.model.response.UserResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtils;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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

    private SocialSystemAPI api;

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
        api = RetrofitClient.getSocialSystemAPI();
        initView();
        return view;
    }

    private void initView() {
        btnRegister.setOnClickListener((View v) -> {
            String name = editUsername.getText().toString();
            String pw = editPassword.getText().toString();
            addSubscription(api.register(name, pw)
                    .flatMap(NetworkFailureHandler.httpFailureFilter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(response -> ((UserResponse) response).getResult())
                    .subscribe(
                            userInfo -> {
                                ToastUtils.showShort("register success");
                                updateUser(userInfo);
                            },
                            NetworkFailureHandler.basicErrorHandler
                    ));
        });
        btnLogIn.setOnClickListener((View v) -> {
            String name = editUsername.getText().toString();
            String pw = editPassword.getText().toString();
            addSubscription(api.login(name, pw)
                    .flatMap(NetworkFailureHandler.httpFailureFilter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(response -> ((UserResponse) response).getResult())
                    .subscribe(
                            userInfo -> {
                                ToastUtils.showShort("login success");
                                Log.d("TEST", String.valueOf(userInfo));
                                updateUser(userInfo);
                            },
                            NetworkFailureHandler.basicErrorHandler
                    ));
        });
    }

    private void updateUser(UserResponse.UserInfo userInfo) {
        User user = new User();
        user.setUsername(userInfo.getName());
        ((App) getActivity().getApplication()).setUser(user);
        MainActivity mainActivity = (MainActivity) getActivity();
        Log.d("TEST", "UPDATE");
        mainActivity.updateFragment(2);
    }
}
