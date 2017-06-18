package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.papermelody.R;
import com.papermelody.activity.OnlineListenActivity;
import com.papermelody.model.MusicBanner;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.OnlineMusicInfo;
import com.papermelody.model.response.OnlineMusicListResponse;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.widget.MusicHallCycleViewPager;
import com.papermelody.widget.OnlineMusicRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class MusicHallFragment extends BaseFragment {
    /**
     * 用例：浏览音乐圈
     * 音乐圈页面，上传作品使用recyclerView排序
     */

    @BindView(R.id.hall_cycle_view_pager)
    MusicHallCycleViewPager cyclePoster;
    @BindView(R.id.recycler_view_hall)
    RecyclerView recyclerViewHall;
    @BindView(R.id.layout_hall_refresh)
    SwipeRefreshLayout layoutRefresh;
    @BindView(R.id.spinner_hall)
    Spinner spinnerHall;
//    @BindView(R.id.recycler_header)
//    RecyclerViewHeader recyclerViewHeader;

    private Context context;
    private OnlineMusicRecyclerViewAdapter adapter;
    private int orderMode = 0;
    private ArrayAdapter<CharSequence> arrayAdapterOrder;

    private OnlineMusicRecyclerViewAdapter.OnItemClickListener hallOnItemClickListener = new
            OnlineMusicRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(OnlineMusic music) {
                    Intent intent = new Intent(context, OnlineListenActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(OnlineMusic.SERIAL_ONLINEMUSIC, music);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            };

    private static MusicHallFragment fragment;

    public static MusicHallFragment newInstance() {
        if (fragment != null) {
            return fragment;
        }
        MusicHallFragment fragment = new MusicHallFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_music_hall, container, false);
        ButterKnife.bind(this, view);
        context = view.getContext();

        initGetMusicList();
        initSwipeRefreshView();
        initSpinner();

        return view;
    }

    private void initGetMusicList() {
        SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.getOnlineMusicList(orderMode)
                .flatMap(NetworkFailureHandler.httpFailureFilter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(response -> ((OnlineMusicListResponse) response).getResult().getMusics())
                .subscribe(
                        musicList -> {
                            List<OnlineMusic> musics = new ArrayList<>();
                            for (OnlineMusicInfo info : musicList) {
                                musics.add(new OnlineMusic(info, context));
                            }
                            initRecyclerView(musics);
                            initBannerView(musics);
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void initRecyclerView(List<OnlineMusic> musics) {
        adapter = new OnlineMusicRecyclerViewAdapter(context, musics);
        adapter.setOnItemClickListener(hallOnItemClickListener);
        recyclerViewHall.setAdapter(adapter);
        recyclerViewHall.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerViewHall.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewHeader.attachTo(recyclerViewHall);
    }

    private void initBannerView(List<OnlineMusic> musics) {

        List<MusicBanner> banners = new ArrayList<>();
        try {
            banners.add(new MusicBanner(musics.get(0).getMusicName(), musics.get(0).getMusicPhotoUrl()));
            banners.add(new MusicBanner(musics.get(1).getMusicName(), musics.get(1).getMusicPhotoUrl()));
            banners.add(new MusicBanner(musics.get(2).getMusicName(), musics.get(2).getMusicPhotoUrl()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        cyclePoster.setIndicatorsSelected(R.drawable.shape_cycle_indicator_selected,
                R.drawable.shape_cycle_indicator_unselected);
        cyclePoster.setDelay(3000);
        cyclePoster.setData(banners, null);
    }

    private void initSwipeRefreshView() {
        layoutRefresh.setColorSchemeResources(R.color.colorAccent);
        layoutRefresh.setProgressBackgroundColorSchemeResource(R.color.white);
        layoutRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        layoutRefresh.setProgressViewEndTarget(true, 100);
        layoutRefresh.setOnRefreshListener(() -> {
            initGetMusicList();
            layoutRefresh.setRefreshing(false);
        });
    }

    private void initSpinner() {
        arrayAdapterOrder = ArrayAdapter.createFromResource(getContext(), R.array.spinner_order_mode, android.R.layout.simple_spinner_item);
        arrayAdapterOrder.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerHall.setAdapter(arrayAdapterOrder);
        spinnerHall.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                orderMode = position;
                initGetMusicList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
}
