package com.papermelody.fragment;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.papermelody.R;
import com.papermelody.util.ToastUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ListenFragment extends BaseFragment {
    /**
     * 试听页面的试听部分，即播放器部分
     */
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    @BindView(R.id.btn_pause)
    Button btnPause;

    private MediaPlayer mediaPlayer;
    private AssetFileDescriptor afd;
    private TimerTask timerTask;
    private Timer timer;
    private boolean playState;

    public static ListenFragment newInstance() {
        ListenFragment fragment = new ListenFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        playState = true;
        try {
            afd = getResources().getAssets().openFd("Kissbye.mid");
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            ToastUtils.showShort(R.string.unable_to_play);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstance) {
        View view = inflater.inflate(R.layout.fragment_listen, container, false);
        ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress() * mediaPlayer.getDuration() / 100);
            }
        });
        timerTask = new TimerTask() {
            @Override
            public void run() {
                seekBar.setProgress(100 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration());
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
        btnPause.setOnClickListener((View v) -> {
            if (playState) {
                mediaPlayer.pause();
                btnPause.setText(R.string.conti);
                playState = false;
            } else {
                mediaPlayer.start();
                btnPause.setText(R.string.pause);
                playState = true;
            }
        });
    }

    @Override
    public void onPause(){
        super.onPause();
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        timer.cancel();
        mediaPlayer.release();
    }
}
