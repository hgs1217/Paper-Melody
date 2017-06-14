package com.papermelody.fragment;

import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
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
import com.papermelody.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RegisterFragment extends BaseFragment {

    @BindView(R.id.ttl_username)
    TextInputLayout userTextInputLayoutUser;
    @BindView(R.id.ttl_password)
    TextInputLayout pwTextInputLayoutUser;
    @BindView(R.id.ttl_mail)
    TextInputLayout emTextInputLayoutUser;
    @BindView(R.id.et_username)
    EditText editUsername;
    @BindView(R.id.et_email)
    EditText editemail;
    @BindView(R.id.et_password)
    EditText editPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;

    public static RegisterFragment newInstance() {
        RegisterFragment fragment = new RegisterFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        //设置可以计数

        ButterKnife.bind(this, view);
        pwTextInputLayoutUser.setCounterEnabled(true);

        //计数的最大值
        pwTextInputLayoutUser.setCounterMaxLength(20);

        initView();
        return view;
    }

    private void initView() {
        btnRegister.setOnClickListener((View v) -> {
                    String name = editUsername.getText().toString();
                    String pw = editPassword.getText().toString();
                    String em = editemail.getText().toString();

                    pwTextInputLayoutUser.setErrorEnabled(false);
                    if (TextUtils.isEmpty(pw) || pw.length() < 6) {
                        pwTextInputLayoutUser.setError("密码错误不能少于6个字符");
                    } else {
                        SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
                        addSubscription(api.register(name, pw)
                                .flatMap(NetworkFailureHandler.httpFailureFilter)
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .map(response -> ((UserResponse) response).getResult())
                                .subscribe(
                                        userInfo -> {
                                            ToastUtil.showShort(R.string.register_success);
                                            updateUser(userInfo);
                                        },
                                        NetworkFailureHandler.loginErrorHandler
                                ));
                    }
                }
        );
    }

    private void updateUser(UserResponse.UserInfo userInfo) {
        User user = new User();
        user.setUserID(userInfo.getUserID());
        user.setUsername(userInfo.getName());
        App.setUser(user);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateFragment(MainActivity.MAIN_USER);
    }
}
