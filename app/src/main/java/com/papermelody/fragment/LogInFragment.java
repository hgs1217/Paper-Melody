package com.papermelody.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;

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

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class LogInFragment extends BaseFragment {
    /**
     * 用例：注册、登录
     * 注册与登录页面，（暂弃用：此页面包含2个Fragment）
     */

    @BindView(R.id.ttl_username)
    TextInputLayout userTextInputLayoutUser;
    @BindView(R.id.ttl_password)
    TextInputLayout pwTextInputLayoutUser;
    @BindView(R.id.et_username)
    EditText editUsername;
    @BindView(R.id.et_password)
    EditText editPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.btn_go)
    Button btnLogIn;
    @BindView(R.id.main_layout)
    LinearLayout mainlayout;

    private Context context;

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

        //设置可以计数

        ButterKnife.bind(this, view);

        context = getActivity();
        userTextInputLayoutUser.setCounterEnabled(true);
        //计数的最大值
        userTextInputLayoutUser.setCounterMaxLength(20);
        pwTextInputLayoutUser.setCounterEnabled(true);

        //计数的最大值
        pwTextInputLayoutUser.setCounterMaxLength(20);

        initView();
        return view;
    }

    private void initView() {

        btnLogIn.setOnClickListener((View v) -> {
            String name = editUsername.getText().toString();
            userTextInputLayoutUser.setErrorEnabled(false);
            MainActivity activity = (MainActivity) getActivity();

            if (TextUtils.isEmpty(name)) userTextInputLayoutUser.setError("用户名不能为空");
            if ( activity.isContainChinese(name)) {
                userTextInputLayoutUser.setError("不能包含中文");
            }
            if (TextUtils.isEmpty(name) || name.length() < 2) {
                userTextInputLayoutUser.setError("用户名过短");
            } else {
                String pw = editPassword.getText().toString();
                pwTextInputLayoutUser.setErrorEnabled(false);
                if (TextUtils.isEmpty(pw) || pw.length() < 6) {

                    pwTextInputLayoutUser.setError("密码错误不能少于6个字符");

                } else {
                    SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
                    addSubscription(api.login(name, pw)
                            .flatMap(NetworkFailureHandler.httpFailureFilter)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .map(response -> ((UserResponse) response).getResult())
                            .subscribe(
                                    userInfo -> {
                                        ToastUtil.showShort(R.string.login_success);
                                        updateUser(userInfo);
                                    },
                                    NetworkFailureHandler.loginErrorHandler
                            ));
                }
            }
        });
        btnRegister.setOnClickListener((View v) -> {
                    MainActivity mainActivity = (MainActivity) getActivity();
                    mainActivity.updateFragment(MainActivity.REGISTER);
                }
        );
        mainlayout.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1)
            {
                InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });
    }

    private void updateUser(UserResponse.UserInfo userInfo) {
        User user = new User(userInfo, context);
        App.setUser(user);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateFragment(MainActivity.MAIN_USER);
    }
}
