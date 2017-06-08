package com.papermelody.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.fragment.LogInFragment;
import com.papermelody.fragment.ModeFragment;
import com.papermelody.fragment.ModeFreeSettingsFragment;
import com.papermelody.fragment.ModeOpernSettingsFragment;
import com.papermelody.fragment.MusicHallFragment;
import com.papermelody.fragment.SettingsFragment;
import com.papermelody.fragment.UserFragment;
import com.papermelody.fragment.UserInfoFragment;
import com.papermelody.widget.NoScrollViewPager;
import com.roughike.bottombar.BottomBar;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    /**
     * 主菜单，将由四个Fragment组成，实现方式为底部toolbar
     */

    @BindView(R.id.container)
    NoScrollViewPager container;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;

    public static final int MODE_FREE = 4;
    public static final int MODE_OPERN = 5;
    public static final int LOG_IN = 6;
    public static final int USER_INFO = 7;
    public static final int REGISTER = 8;

    private static final int REQUEST_PERMISSION = 1;

    /**
     * 请求类permissions：防止所有需要手动申请才能获取的权限
     */
    private String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
    private AlertDialog dialog;

    private FragmentManager fragmentManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        int currentTab = intent.getIntExtra("currentTab", 0);
        fragmentManager = getSupportFragmentManager();

        requestPermissions();

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
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab0));
                break;
            case 1:
                toolbarTitle.setText(R.string.tab_music_hall);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab1));
                break;
            case 2:
                toolbarTitle.setText(R.string.tab_user);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab2));
                break;
            case 3:
                toolbarTitle.setText(R.string.tab_settings);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab3));
                break;
            case MODE_FREE:
                toolbarTitle.setText(R.string.mode_free);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab0));
                break;
            case MODE_OPERN:
                toolbarTitle.setText(R.string.mode_opern);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab0));
                break;
            case LOG_IN:
                toolbarTitle.setText(R.string.log_in);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab0));
                break;
            case USER_INFO:
                toolbarTitle.setText(R.string.user_info);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab0));
                break;
            case REGISTER:
                toolbarTitle.setText(R.string.register);
                toolbar.setBackgroundColor(getResources().getColor(R.color.tab0));
                break;
            default:
                break;
        }
        toolbar.getMenu().clear();
    }

    private void initTabView() {
//        View indicatorMode = getLayoutInflater().inflate(R.layout.item_tab, null);
//        View indicatorSettings = getLayoutInflater().inflate(R.layout.item_tab, null);
//        View indicatorHall = getLayoutInflater().inflate(R.layout.item_tab, null);
//        View indicatorUser = getLayoutInflater().inflate(R.layout.item_tab, null);
//
//        TextView textViewMode = (TextView) indicatorMode.findViewById(R.id.text_item_tab);
//        TextView textViewSettings = (TextView) indicatorSettings.findViewById(R.id.text_item_tab);
//        TextView textViewHall = (TextView) indicatorHall.findViewById(R.id.text_item_tab);
//        TextView textViewUser = (TextView) indicatorUser.findViewById(R.id.text_item_tab);
//
//        ImageView imageViewMode = (ImageView) indicatorMode.findViewById(R.id.image_item_tab);
//        ImageView imageViewSettings = (ImageView) indicatorSettings.findViewById(R.id.image_item_tab);
//        ImageView imageViewHall = (ImageView) indicatorHall.findViewById(R.id.image_item_tab);
//        ImageView imageViewUser = (ImageView) indicatorUser.findViewById(R.id.image_item_tab);
//
//        imageViewMode.setImageDrawable(getDrawable(R.drawable.ic_audiotrack_black_24dp));
//        imageViewSettings.setImageDrawable(getDrawable(R.drawable.ic_settings_black_24dp));
//        imageViewHall.setImageDrawable(getDrawable(R.drawable.ic_cloud_circle_black_24dp));
//        imageViewUser.setImageDrawable(getDrawable(R.drawable.ic_account_circle_black_24dp));
//
//        textViewMode.setText(R.string.tab_mode);
//        textViewSettings.setText(R.string.tab_settings);
//        textViewHall.setText(R.string.tab_music_hall);
//        textViewUser.setText(R.string.tab_user);
//
//        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorMode));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorHall));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorUser));
//        tabLayout.addTab(tabLayout.newTab().setCustomView(indicatorSettings));

//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_mode).setIcon(getDrawable(R.drawable.ic_audiotrack_black_24dp)));
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_music_hall).setIcon(getDrawable(R.drawable.ic_cloud_circle_black_24dp)));
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_user).setIcon(getDrawable(R.drawable.ic_account_circle_black_24dp)));
//        tabLayout.addTab(tabLayout.newTab().setText(R.string.tab_settings).setIcon(getDrawable(R.drawable.ic_settings_black_24dp)));

//        ColorStateList colors;
//        if (Build.VERSION.SDK_INT >= 23) {
//            colors = getResources().getColorStateList(R.color.tab_icon_tint, getTheme());
//        }
//        else {
//            colors = getResources().getColorStateList(R.color.tab_icon_tint);
//        }
//        for (int i = 0; i < tabLayout.getTabCount(); i++) {
//            TabLayout.Tab tab = tabLayout.getTabAt(i);
//            Drawable icon = tab.getIcon();
//            if (icon != null) {
//                icon = DrawableCompat.wrap(icon);
//                DrawableCompat.setTintList(icon, colors);
//            }
//        }
//        tabLayout.setSelectedTabIndicatorColor(Color.TRANSPARENT);

//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                int currentPage = tab.getPosition();
//                updateToolbar(currentPage);
//                container.setCurrentItem(currentPage, false);
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//            }
//        });
        bottomBar.setOnTabSelectListener((@IdRes int tabId) -> {
            int currentPage;
            switch (tabId) {
                case R.id.tab_1:
                    currentPage = 0;
                    break;
                case R.id.tab_2:
                    currentPage = 1;
                    break;
                case R.id.tab_3:
                    currentPage = 2;
                    break;
                case R.id.tab_4:
                    currentPage = 3;
                    break;
                default:
                    currentPage = 0;
                    break;
            }
            updateToolbar(currentPage);
            container.setCurrentItem(currentPage, false);
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

        private final int pageCount = 9;

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
                case USER_INFO:
                    return UserInfoFragment.newInstance();
                case REGISTER:
                    return com.papermelody.fragment.RegisterFragment.newInstance();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return pageCount;
        }
    }

    private void requestPermissions() {
        if (Build.VERSION.SDK_INT >= 23) {
            // 检查该权限是否已经获取
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            for (int i=0; i<permissions.length; ++i) {
                if (ContextCompat.checkSelfPermission(this, permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    // 如果没有授予该权限，就去提示用户请求
                    ActivityCompat.requestPermissions(this, permissions, REQUEST_PERMISSION);
                    break;
                }
            }
        }
    }

    /**
     * 用户权限申请的回调方法
     * @param requestCode   请求码
     * @param permissions   请求的所有权限
     * @param grantResults  权限的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (Build.VERSION.SDK_INT >= 23) {
                for (int i=0; i<grantResults.length; ++i) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                        boolean b = shouldShowRequestPermissionRationale(permissions[i]);
                        if (!b) {
                            // 用户还是想用我的 APP 的
                            // 提示用户去应用设置界面手动开启权限
                            showDialogTipUserGoToAppSetting();
                        } else
                            finish();
                    }
                }
            }
        }
    }

    /**
     * 提示用户去应用设置界面手动开启权限
     */
    private void showDialogTipUserGoToAppSetting() {

        dialog = new AlertDialog.Builder(this)
                .setTitle("权限不可用")
                .setMessage("请在-应用设置-权限-中，允许该应用使用照相机和存储等权限")
                .setPositiveButton("立即开启", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // 跳转到应用设置界面
                        goToAppSetting();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                }).setCancelable(false).show();
    }

    /**
     * 若权限被始终禁止，跳转到当前应用的设置界面
     */
    private void goToAppSetting() {
        Intent intent = new Intent();

        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);

        startActivityForResult(intent, REQUEST_PERMISSION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_PERMISSION) {
            if (android.os.Build.VERSION.SDK_INT >= 23) {
                for (int i=0; i<permissions.length; ++i) {
                    // 检查该权限是否已经获取
                    int per = ContextCompat.checkSelfPermission(this, permissions[i]);
                    // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
                    if (per != PackageManager.PERMISSION_GRANTED) {
                        // 提示用户应该去应用设置界面手动开启权限
                        showDialogTipUserGoToAppSetting();
                    } else {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                        }
                    }
                }
            }
        }
    }
}
