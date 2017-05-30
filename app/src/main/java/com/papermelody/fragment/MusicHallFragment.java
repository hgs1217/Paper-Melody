package com.papermelody.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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

    public static final String SERIAL_ONLINEMUSIC = "SERIAL_ONLINEMUSIC";

    private Context context;
    private MusicHallRecyclerViewAdapter adapter;
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

        return view;
    }

    private void initGetMusicList() {
        SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.getOnlineMusicList()
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
    }

    private void initBannerView(List<OnlineMusic> musics) {
        // TODO: 此处后期需修改为MusicBanner与某些音乐绑定数据
        List<MusicBanner> banners = new ArrayList<>();
        int sz = musics.size();
        banners.add(new MusicBanner(musics.get(sz-1).getMusicName(), musics.get(sz-1).getMusicPhotoUrl()));
        banners.add(new MusicBanner(musics.get(sz-2).getMusicName(), musics.get(sz-2).getMusicPhotoUrl()));
        banners.add(new MusicBanner(musics.get(sz-3).getMusicName(), musics.get(sz-3).getMusicPhotoUrl()));

        cyclePoster.setIndicatorsSelected(R.drawable.shape_cycle_indicator_selected,
                R.drawable.shape_cycle_indicator_unselected);
        cyclePoster.setDelay(2000);
        cyclePoster.setData(banners, null);
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
