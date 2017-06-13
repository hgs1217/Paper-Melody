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
    @BindView(R.id.toolbar_icon)
    ImageView toolbarIcon;

    public static final int MAIN_HOME = 0;
    public static final int MAIN_HALL = 1;
    public static final int MAIN_USER = 2;
    public static final int MAIN_SETTINGS = 3;
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
    private int currentPage = 0;

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
        // TODO: back图标太丑，不和设计风格
        switch (position) {
            case MAIN_HOME:
                toolbarTitle.setText(R.string.tab_mode);
                break;
            case MAIN_HALL:
                toolbarTitle.setText(R.string.tab_music_hall);
                break;
            case MAIN_USER:
                toolbarTitle.setText(R.string.tab_user);
                break;
            case MAIN_SETTINGS:
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
            case USER_INFO:
                toolbarTitle.setText(R.string.user_info);
                break;
            case REGISTER:
                toolbarTitle.setText(R.string.register);
                break;
            default:
                break;
        }

        switch (position) {
            case MODE_FREE:
            case MODE_OPERN:
            case USER_INFO:
            case LOG_IN:
            case REGISTER:
                toolbarIcon.setImageDrawable(getDrawable(R.drawable.back));
                toolbarIcon.setOnClickListener((view) -> {
                    onBackPressed();
                });
                break;
            default:;
                toolbarIcon.setImageDrawable(null);
                toolbarIcon.setOnClickListener((view) -> { });
                break;
        }
    }

    private void initTabView() {
        bottomBar.setOnTabSelectListener((@IdRes int tabId) -> {
            int currentPage;
            switch (tabId) {
                case R.id.tab_1:
                    currentPage = MAIN_HOME;
                    break;
                case R.id.tab_2:
                    currentPage = MAIN_HALL;
                    break;
                case R.id.tab_3:
                    currentPage = MAIN_USER;
                    break;
                case R.id.tab_4:
                    currentPage = MAIN_SETTINGS;
                    break;
                default:
                    currentPage = MAIN_HOME;
                    break;
            }
            updateToolbar(currentPage);
            this.currentPage = currentPage;
            container.setCurrentItem(currentPage, false);
        });
    }

    public void updateFragment(int position) {
        /* 用于切换至模式设置页面调用 */

        updateToolbar(position);
        currentPage = position;
        container.setCurrentItem(position, false);
    }

    @Override
    public void onBackPressed() {
        switch (currentPage) {
            case MODE_FREE:
            case MODE_OPERN:
                updateFragment(MAIN_HOME);
                break;
            case USER_INFO:
            case LOG_IN:
                updateFragment(MAIN_USER);
                break;
            case REGISTER:
                updateFragment(LOG_IN);
                break;
            default:
                super.onBackPressed();
        }
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
                case MAIN_HOME:
                    return ModeFragment.newInstance();
                case MAIN_HALL:
                    return MusicHallFragment.newInstance();
                case MAIN_USER:
                    return UserFragment.newInstance();
                case MAIN_SETTINGS:
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
            for (int i = 0; i < permissions.length; ++i) {
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
     *
     * @param requestCode  请求码
     * @param permissions  请求的所有权限
     * @param grantResults 权限的结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_PERMISSION) {
            if (Build.VERSION.SDK_INT >= 23) {
                for (int i = 0; i < grantResults.length; ++i) {
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
                for (int i = 0; i < permissions.length; ++i) {
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
