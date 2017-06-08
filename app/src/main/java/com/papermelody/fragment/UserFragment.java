package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.activity.HistoryActivity;
import com.papermelody.activity.MainActivity;
import com.papermelody.model.User;
import com.papermelody.util.App;
import com.papermelody.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class UserFragment extends BaseFragment {
    /**
     * 用例：查看用户信息
     * 用户页面，包括个人信息，已上传作品，收藏作品等等
     */

    @BindView(R.id.text_username)
    TextView textUsername;
    @BindView(R.id.btn_login)
    Button btnLogIn;
    @BindView(R.id.btn_user_info)
    Button btnUserInfo;
    @BindView(R.id.btn_user_history)
    Button btnUserHistory;
    @BindView(R.id.btn_user_upload)
    Button btnUserUpload;
    @BindView(R.id.btn_user_favorite)
    Button btnUserFavorite;

    private User user;
    private Context context;

    public static UserFragment newInstance() {
        UserFragment fragment = new UserFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_user, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();
        initView();
        return view;
    }

    private void initView() {
        updateUser();

        btnLogIn.setOnClickListener((View v) -> {
            if (user == null) {
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.updateFragment(MainActivity.LOG_IN);
            } else {
                App.setUser(null);
                user = null;
                ToastUtil.showShort(R.string.user_log_out);
                updateUser();
            }
        });

        btnUserInfo.setOnClickListener((View v) -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateFragment(MainActivity.USER_INFO);
        });
        btnUserHistory.setOnClickListener((View v) -> {
            Intent intent = new Intent(context, HistoryActivity.class);
            startActivity(intent);
        });
        btnUserUpload.setOnClickListener((View v) -> {
            // TODO:
        });
        btnUserFavorite.setOnClickListener((View v) -> {
            // TODO:
        });
    }

    public void updateUser() {
        user = App.getUser();
        if (user == null) {
            Log.d("TEST2", "TEST2");
            textUsername.setText(R.string.un_log_in);
            btnLogIn.setText(R.string.user_log_in);
        } else {
            Log.d("TEST3", user.getUsername());
            textUsername.setText(user.getUsername());
            btnLogIn.setText(R.string.user_log_out);
        }
    }
}
