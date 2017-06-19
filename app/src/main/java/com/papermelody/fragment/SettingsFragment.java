package com.papermelody.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.papermelody.R;
import com.papermelody.activity.AboutActivity;
import com.papermelody.activity.SettingsDevActivity;
import com.papermelody.activity.SettingsPlayActivity;
import com.papermelody.activity.SettingsPrivacyActivity;
import com.papermelody.activity.TutorialActivity;
import com.papermelody.util.ToastUtil;

import butterknife.BindView;
import butterknife.ButterKnife;


public class SettingsFragment extends BaseFragment {
    /**
     * 用例：修改设置
     * 设置主页面
     */
    @BindView(R.id.cardView_developer)
    CardView btn_developer;
    @BindView(R.id.cardView_settings_play)
    CardView btn_setting_play;
    @BindView(R.id.cardView_about)
    CardView btn_about;
    @BindView(R.id.cardView_privacy)
    CardView btn_privacy;
    @BindView(R.id.cardView_viewTurorial)
    CardView btn_viewTutorial;
    @BindView(R.id.dev_menu_insider)
    LinearLayout dev_menu;

    private static SettingsFragment fragment;

    public static SettingsFragment newInstance() {
        if (fragment != null) {
            return fragment;
        }
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
        initCardView();
        return view;
    }

    public void initCardView() {
        btn_setting_play.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), SettingsPlayActivity.class);
            startActivity(intent);
        });


        btn_developer.setOnClickListener((View v) -> {
            final EditText et = new EditText(getContext());

            new AlertDialog.Builder(getContext()).setTitle("输入开发者权限密码：")
                    .setIcon(R.drawable.ic_settings_black_24dp)
                    .setView(et)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String input = et.getText().toString();
                            if (input.equals("admin")) {

                                Intent intent = new Intent(getActivity(), SettingsDevActivity.class);
                                startActivity(intent);
                            } else {
                                ToastUtil.showShort("密码错误");
                            }
                        }
                    })
                    .setNegativeButton("取消", null)
                    .show();
        });

        btn_privacy.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), SettingsPrivacyActivity.class);
            startActivity(intent);
        });

        btn_about.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), AboutActivity.class);
            startActivity(intent);
        });

        btn_viewTutorial.setOnClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), TutorialActivity.class);
            intent.putExtra(TutorialActivity.FROM_SPLASH, false);
            startActivity(intent);
        });
    }
}
