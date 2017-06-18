package com.papermelody.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.papermelody.R;
import com.papermelody.model.OnlineMusic;
import com.papermelody.model.response.OnlineMusicInfo;
import com.papermelody.model.response.OnlineMusicListResponse;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.widget.OnlineMusicRecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by HgS_1217_ on 2017/6/13.
 */

public class FavoriteActivity extends BaseActivity {
    /**
     * 收藏作品列表，根据用户的点赞情况从服务器上获取
     */

    @BindView(R.id.favorite_recycler_view)
    RecyclerView viewFavorite;
    @BindView(R.id.toolbar_favor_products)
    Toolbar toolbarFavorProducts;
    @BindView(R.id.layout_favor_refresh)
    SwipeRefreshLayout favorRefresh;

    private Context context;
    private OnlineMusicRecyclerViewAdapter adapter;

    private OnlineMusicRecyclerViewAdapter.OnItemClickListener favoriteOnItemClickListener = new
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        context = this;
        setSupportActionBar(toolbarFavorProducts);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbarFavorProducts.setNavigationOnClickListener((View v) -> {
            finish();
        });
        initSwipeRefreshView();
        initGetMusicList();
    }

    private void initGetMusicList() {
        SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
        addSubscription(api.getFavoriteMusicList(App.getUser().getUserID())
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
                        },
                        NetworkFailureHandler.basicErrorHandler
                ));
    }

    private void initSwipeRefreshView() {
        favorRefresh.setColorSchemeResources(R.color.colorAccent);
        favorRefresh.setProgressBackgroundColorSchemeResource(R.color.white);
        favorRefresh.setSize(SwipeRefreshLayout.DEFAULT);
        favorRefresh.setProgressViewEndTarget(true, 100);
        favorRefresh.setOnRefreshListener(() -> {
            initGetMusicList();
            favorRefresh.setRefreshing(false);
        });
    }

    private void initRecyclerView(List<OnlineMusic> musics) {
        adapter = new OnlineMusicRecyclerViewAdapter(context, musics);
        adapter.setOnItemClickListener(favoriteOnItemClickListener);
        viewFavorite.setAdapter(adapter);
        viewFavorite.setLayoutManager(new GridLayoutManager(context, 1));
        viewFavorite.setItemAnimator(new DefaultItemAnimator());
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_favorite;
    }
}
