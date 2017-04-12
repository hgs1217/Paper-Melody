package com.papermelody.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.fragment.ModeFragment;
import com.papermelody.fragment.MusicHallFragment;
import com.papermelody.fragment.SettingsFragment;
import com.papermelody.fragment.UserFragment;
import com.papermelody.widget.NoScrollViewPager;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    /**
     * 主菜单，将由四个Fragment组成，实现方式为底部toolbar
     */

    @BindView(R.id.container)
    NoScrollViewPager container;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int currentTab = intent.getIntExtra("currentTab", 0);
        fragmentManager = getSupportFragmentManager();

        initTabView();
        updateToolbar(currentTab);

        TabPagerAdapter tabPagerAdapter = new TabPagerAdapter(fragmentManager);
        container.setAdapter(tabPagerAdapter);
        container.setCurrentItem(currentTab);
    }

    private void updateToolbar(int position) {
        toolbar.setLogo(null);
        toolbar.setTitle(null);
        switch (position) {
            case 0:
                toolbarTitle.setText(R.string.tab_mode);
                break;
            case 1:
                toolbarTitle.setText(R.string.tab_settings);
                break;
            case 2:
                toolbarTitle.setText(R.string.tab_music_hall);
                break;
            case 3:
                toolbarTitle.setText(R.string.tab_user);
                break;
            default:
                break;
        }
        toolbar.getMenu().clear();
    }

    private void initTabView() {
        View indicatorMode = getLayoutInflater().inflate(R.layout.item_tab, null);
        View indicatorSettings = getLayoutInflater().inflate(R.layout.item_tab, null);
        View indicatorHall = getLayoutInflater().inflate(R.layout.item_tab, null);
        View indicatorUser = getLayoutInflater().inflate(R.layout.item_tab, null);

        TextView textViewMode = (TextView) indicatorMode.findViewById(R.id.text_item_tab);
        TextView textViewSettings = (TextView) indicatorSettings.findViewById(R.id.text_item_tab);
        TextView textViewHall = (TextView) indicatorHall.findViewById(R.id.text_item_tab);
        TextView textViewUser = (TextView) indicatorUser.findViewById(R.id.text_item_tab);

        textViewMode.setText(R.string.tab_mode);
        textViewSettings.setText(R.string.tab_settings);
        textViewHall.setText(R.string.tab_music_hall);
        textViewUser.setText(R.string.tab_user);

        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorMode));
        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorSettings));
        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorHall));
        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorUser));

        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int currentPage = tab.getPosition();
                updateToolbar(currentPage);
                container.setCurrentItem(currentPage, false);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }

            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {

        private final int pageCount = 4;

        public TabPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ModeFragment.newInstance();
                case 1:
                    return SettingsFragment.newInstance();
                case 2:
                    return MusicHallFragment.newInstance();
                case 3:
                    return UserFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return pageCount;
        }
    }
}
