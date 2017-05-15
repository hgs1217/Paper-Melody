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
import android.widget.ImageView;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.fragment.LogInFragment;
import com.papermelody.fragment.ModeFragment;
import com.papermelody.fragment.ModeFreeSettingsFragment;
import com.papermelody.fragment.ModeOpernSettingsFragment;
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

    public static final int MODE_FREE = 4;
    public static final int MODE_OPERN = 5;
    public static final int LOG_IN = 6;

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
                toolbarTitle.setText(R.string.tab_music_hall);
                break;
            case 2:
                toolbarTitle.setText(R.string.tab_user);
                break;
            case 3:
                toolbarTitle.setText(R.string.tab_settings);
                break;
            case MODE_FREE:
                toolbarTitle.setText(R.string.mode_free);
                break;
            case MODE_OPERN:
                toolbarTitle.setText(R.string.mode_opern);
                break;
            case LOG_IN:
                toolbarTitle.setText(R.string.log_in);
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

        ImageView imageViewMode = (ImageView) indicatorMode.findViewById(R.id.image_item_tab);
        ImageView imageViewSettings = (ImageView) indicatorSettings.findViewById(R.id.image_item_tab);
        ImageView imageViewHall = (ImageView) indicatorHall.findViewById(R.id.image_item_tab);
        ImageView imageViewUser = (ImageView) indicatorUser.findViewById(R.id.image_item_tab);

        imageViewMode.setImageDrawable(getDrawable(R.drawable.ic_audiotrack_black_24dp));
        imageViewSettings.setImageDrawable(getDrawable(R.drawable.ic_settings_black_24dp));
        imageViewHall.setImageDrawable(getDrawable(R.drawable.ic_cloud_circle_black_24dp));
        imageViewUser.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_24dp));

        textViewMode.setText(R.string.tab_mode);
        textViewSettings.setText(R.string.tab_settings);
        textViewHall.setText(R.string.tab_music_hall);
        textViewUser.setText(R.string.tab_user);


        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorMode));
        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorHall));
        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorUser));
        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorSettings));

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

    public void updateFragment(int position) {
        /* 用于切换至模式设置页面调用 */

        updateToolbar(position);
        container.setCurrentItem(position, false);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }

    public class TabPagerAdapter extends FragmentStatePagerAdapter {

        private final int pageCount = 7;

        public TabPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ModeFragment.newInstance();
                case 1:
                    return MusicHallFragment.newInstance();
                case 2:
                    return UserFragment.newInstance();
                case 3:
                    return SettingsFragment.newInstance();
                /* 以下case 不能通过tab栏切换达到，合并到这里便于实现一些需要对主页面的部分元素进行交互的操作 */
                case MODE_FREE:
                    return ModeFreeSettingsFragment.newInstance();
                case MODE_OPERN:
                    return ModeOpernSettingsFragment.newInstance();
                case LOG_IN:
                    return LogInFragment.newInstance();
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
