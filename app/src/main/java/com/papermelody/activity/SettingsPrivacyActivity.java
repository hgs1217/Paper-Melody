package com.papermelody.activity;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.papermelody.R;
import com.papermelody.fragment.SettingsPrivacyPreferenceFragment;

import butterknife.BindView;


public class SettingsPrivacyActivity extends BaseActivity {
    /**
     * 用例：修改设置（流程二）
     * 账号与隐私设置页面
     */
    @BindView(R.id.toolbar_settings_play)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initToolbar();
        initPreferenceMenu();
    }

    private void initPreferenceMenu() {
        getFragmentManager().beginTransaction().replace(
                R.id.frameLayout_settings_privacy, new SettingsPrivacyPreferenceFragment(
                )).commit();
    }


    private void initToolbar() {
        toolbar.setTitle(R.string.settings_privacy);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((View v) -> {
            finish();
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_privacy;
    }
}
