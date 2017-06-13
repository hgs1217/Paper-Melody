package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;
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
import com.papermelody.widget.MusicHallRecyclerViewAdapter;

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

    public static final String SERIAL_ONLINEMUSIC = "SERIAL_ONLINEMUSIC";

    public static final int ORDER_DEFAULT = 0;
    public static final int ORDER_HOT = 1;
    public static final int ORDER_WELCOME = 2;

    private Context context;
    private MusicHallRecyclerViewAdapter adapter;
    private int orderMode = 0;
    private ArrayAdapter<CharSequence> arrayAdapterOrder;

    private MusicHallRecyclerViewAdapter.OnItemClickListener hallOnItemClickListener = new
            MusicHallRecyclerViewAdapter.OnItemClickListener() {
                @Override
                public void OnItemClick(OnlineMusic music) {
                    Intent intent = new Intent(context, OnlineListenActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable(SERIAL_ONLINEMUSIC, music);
                    intent.putExtras(bundle);
                    startActivity(intent);
                }
            };

    public static MusicHallFragment newInstance() {
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
                        NetworkFailureHandler.loginErrorHandler
                ));
    }

    private void initRecyclerView(List<OnlineMusic> musics) {
        adapter = new MusicHallRecyclerViewAdapter(context, musics);
        adapter.setOnItemClickListener(hallOnItemClickListener);
        recyclerViewHall.setAdapter(adapter);
        recyclerViewHall.setLayoutManager(new GridLayoutManager(context, 1));
        recyclerViewHall.setItemAnimator(new DefaultItemAnimator());
//        recyclerViewHeader.attachTo(recyclerViewHall);
    }

    private void initBannerView(List<OnlineMusic> musics) {
        // TODO: 此处后期需修改为MusicBanner与某些音乐绑定数据
        List<MusicBanner> banners = new ArrayList<>();
        int sz = musics.size();
        banners.add(new MusicBanner(musics.get(sz - 1).getMusicName(), musics.get(sz - 1).getMusicPhotoUrl()));
        banners.add(new MusicBanner(musics.get(sz - 2).getMusicName(), musics.get(sz - 2).getMusicPhotoUrl()));
        banners.add(new MusicBanner(musics.get(sz - 3).getMusicName(), musics.get(sz - 3).getMusicPhotoUrl()));

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

    private List<OnlineMusic> testCreateMusics() {
        ArrayList<OnlineMusic> musics = new ArrayList<>();
        OnlineMusic testMusic1 = new OnlineMusic(), testMusic2 = new OnlineMusic(),
                testMusic3 = new OnlineMusic();
        testMusic1.setMusicName("国歌");
        testMusic1.setMusicAuthor("作者：zb");
        testMusic2.setMusicName("共青团团歌");
        testMusic2.setMusicAuthor("作者：pyj");
        testMusic3.setMusicName("少先队队歌");
        testMusic3.setMusicAuthor("作者：tth");
        musics.add(testMusic1);
        musics.add(testMusic2);
        musics.add(testMusic3);
        return musics;
    }
}
