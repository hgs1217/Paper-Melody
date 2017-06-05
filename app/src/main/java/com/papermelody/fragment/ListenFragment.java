package com.papermelody.fragment;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;

import com.papermelody.R;
import com.papermelody.util.ToastUtil;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.os.Environment.getExternalStorageDirectory;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ListenFragment extends BaseFragment {
    /**
     * 试听页面的试听部分，即播放器部分
     */
    @BindView(R.id.seekBar)
    SeekBar seekBar;
//    @BindView(R.id.btn_pause)
//    Button btnPause;

    private static final String FILENAME = "";
    private static MediaPlayer mediaPlayer;
    private TimerTask timerTask;
    private Timer timer;
    private boolean playState;
    private static String filename;

    public static ListenFragment newInstance(String fn) {
        ListenFragment fragment = new ListenFragment();
        Bundle bundle = new Bundle();
        bundle.putString(FILENAME, fn);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mediaPlayer = new MediaPlayer();
        filename = getArguments().getString(FILENAME);
        playState = false;
        try {
            mediaPlayer.setDataSource(getExternalStorageDirectory() + "/" +
                    Environment.DIRECTORY_DOWNLOADS + "/" + filename);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.i("nib", getExternalStorageDirectory() + "/" +
                    Environment.DIRECTORY_DOWNLOADS + "/" + filename);
            ToastUtil.showShort(R.string.unable_to_play);
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
                // FIXME: duration为0时，会崩溃闪退
                seekBar.setProgress(100 * mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration());
            }
        };
        timer = new Timer();
        timer.schedule(timerTask, 0, 1000);
//        btnPause.setOnClickListener((View v) -> {
//            if (playState) {
//                pausePlay();
//            } else {
//                starPlay();
//            }
//        });
    }

    @Override
    public void onPause() {
        super.onPause();
        timer.cancel();
        mediaPlayer.release();
    }

    public void starPlay() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
//            btnPause.setBackground(getResources().getDrawable(R.drawable.ic_pause_circle_outline_white_48dp));
            playState = true;
        }
    }

    public void pausePlay() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
//            btnPause.setBackground(getResources().getDrawable(R.drawable.ic_play_circle_outline_white_48dp));
            playState = false;
        }
    }

    public boolean getPlayState() {
        return playState;
    }
}
