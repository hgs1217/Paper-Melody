package com.papermelody.fragment;

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

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RegisterFragment extends BaseFragment {

    @BindView(R.id.ttl_username)
    TextInputLayout userTextInputLayoutUser;
    @BindView(R.id.ttl_password)
    TextInputLayout pwTextInputLayoutUser;
    //@BindView(R.id.ttl_mail)
    TextInputLayout emTextInputLayoutUser;
    @BindView(R.id.et_username)
    EditText editUsername;
   // @BindView(R.id.et_email)
    EditText editemail;
    @BindView(R.id.et_password)
    EditText editPassword;
    @BindView(R.id.btn_register)
    Button btnRegister;
    @BindView(R.id.main_layout)
    LinearLayout mainlayout;

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
            userTextInputLayoutUser.setErrorEnabled(false);
            MainActivity activity = (MainActivity) getActivity();
            if (TextUtils.isEmpty(name)) userTextInputLayoutUser.setError("用户名不能为空");
            if (activity.isContainChinese(name)) {  // FIXME: 此处需和register统一


                userTextInputLayoutUser.setError("不能包含中文");

            }
            if (TextUtils.isEmpty(name) || name.length() < 2) {  // FIXME: 此处需和register统一

                userTextInputLayoutUser.setError("用户名过短");

            }
            else {


                pwTextInputLayoutUser.setErrorEnabled(false);  // FIXME: 存在某些情况下点击用户名editText不会弹出输入框的bug
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
                }
        );


        mainlayout.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View arg0, MotionEvent arg1)
            {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
                return imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
            }
        });

    }

    private void updateUser(UserResponse.UserInfo userInfo) {
        User user = new User(userInfo, getActivity());
        App.setUser(user);
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.updateFragment(MainActivity.MAIN_USER);
    }

}
