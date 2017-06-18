package com.papermelody.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.papermelody.R;
import com.papermelody.activity.CameraDebugActivity;
import com.papermelody.activity.MainActivity;
import com.papermelody.model.ImgBanner;
import com.papermelody.widget.MainCycleViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ModeFragment extends BaseFragment {
    /**
     * 用例：演奏乐器（流程一）
     * 主菜单开始演奏入口
     */

    @BindView(R.id.mode_cycle_view_pager)
    MainCycleViewPager cyclePoster;
    @BindView(R.id.btn_mode_free)
    Button btnModeFree;
    @BindView(R.id.btn_mode_opern)
    Button btnModeOpern;
//    @BindView(R.id.btn_mode_camera_debug)
//    Button btnModeCameraDebug;

    public static ModeFragment newInstance() {
        ModeFragment fragment = new ModeFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mode, container, false);
        ButterKnife.bind(this, view);
        initView();
        initBannerView();
        return view;
    }

    public void initView() {
        btnModeFree.setOnClickListener((View v) -> {
            // TODO: 当设置完成后需修改


            MainActivity mainActivity = (MainActivity) getActivity();

            mainActivity.updateFragment(MainActivity.MODE_FREE);
//            Intent intent = new Intent(getActivity(), CalibrationActivity.class);
//            startActivity(intent);
        });
        btnModeOpern.setOnClickListener((View v) -> {
            // TODO: 当设置完成后需修改
            MainActivity mainActivity = (MainActivity) getActivity();
            mainActivity.updateFragment(MainActivity.MODE_OPERN);
//            Intent intent = new Intent(getActivity(), UploadActivity.class);
//            startActivity(intent);
        });
        // 改为长按modefree进入debug模式
        btnModeFree.setOnLongClickListener((View v) -> {
            Intent intent = new Intent(getActivity(), CameraDebugActivity.class);
            startActivity(intent);
            return true;
        });
    }

    private void initBannerView() {
        List<ImgBanner> banners = new ArrayList<>();
        banners.add(new ImgBanner(R.drawable.testimg1));
        banners.add(new ImgBanner(R.drawable.testimg2));
        banners.add(new ImgBanner(R.drawable.testimg3));

        cyclePoster.setIndicatorsSelected(R.drawable.shape_cycle_indicator_selected,
                R.drawable.shape_cycle_indicator_unselected);
        cyclePoster.setDelay(2000);
        cyclePoster.setData(banners, null);
    }
}
