package com.papermelody.activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.papermelody.R;

import butterknife.BindView;

/**
 * Created by HgS_1217_ on 2017/3/18.
 */

public class PlayActivity extends BaseActivity {
    /**
     * 用例：演奏乐器
     * 弹奏乐器时的界面，可能存在虚拟乐器或曲谱等内容
     */
    @BindView(R.id.text_mode)
    TextView textViewMode;
    @BindView(R.id.text_instrument)
    TextView textViewInstrument;
    @BindView(R.id.text_mode_name)
    TextView textViewModeName;
    @BindView(R.id.text_instrument_name)
    TextView textViewInstrumentName;
    @BindView(R.id.btn_play_over)
    Button btnPlayOver;
    @BindView(R.id.key_effect_c3)
    LinearLayout keyC3;
    @BindView(R.id.key_effect_d3)
    LinearLayout keyD3;
    @BindView(R.id.key_effect_e3)
    LinearLayout keyE3;
    @BindView(R.id.key_effect_f3)
    LinearLayout keyF3;
    @BindView(R.id.key_effect_g3)
    LinearLayout keyG3;
    @BindView(R.id.key_effect_a3)
    LinearLayout keyA3;
    @BindView(R.id.key_effect_b3)
    LinearLayout keyB3;
    @BindView(R.id.key_effect_c4)
    LinearLayout keyC4;
    @BindView(R.id.key_effect_d4)
    LinearLayout keyD4;
    @BindView(R.id.key_effect_e4)
    LinearLayout keyE4;
    @BindView(R.id.key_effect_f4)
    LinearLayout keyF4;
    @BindView(R.id.key_effect_g4)
    LinearLayout keyG4;
    @BindView(R.id.key_effect_a4)
    LinearLayout keyA4;
    @BindView(R.id.key_effect_b4)
    LinearLayout keyB4;
    @BindView(R.id.key_effect_c5)
    LinearLayout keyC5;
    @BindView(R.id.key_effect_d5)
    LinearLayout keyD5;
    @BindView(R.id.key_effect_e5)
    LinearLayout keyE5;
    @BindView(R.id.key_effect_f5)
    LinearLayout keyF5;
    @BindView(R.id.key_effect_g5)
    LinearLayout keyG5;
    @BindView(R.id.key_effect_a5)
    LinearLayout keyA5;
    @BindView(R.id.key_effect_b5)
    LinearLayout keyB5;
    @BindView(R.id.key_effect_c3m)
    LinearLayout keyC3M;
    @BindView(R.id.key_effect_d3m)
    LinearLayout keyD3M;
    @BindView(R.id.key_effect_f3m)
    LinearLayout keyF3M;
    @BindView(R.id.key_effect_g3m)
    LinearLayout keyG3M;
    @BindView(R.id.key_effect_a3m)
    LinearLayout keyA3M;
    @BindView(R.id.key_effect_c4m)
    LinearLayout keyC4M;
    @BindView(R.id.key_effect_d4m)
    LinearLayout keyD4M;
    @BindView(R.id.key_effect_f4m)
    LinearLayout keyF4M;
    @BindView(R.id.key_effect_g4m)
    LinearLayout keyG4M;
    @BindView(R.id.key_effect_a4m)
    LinearLayout keyA4M;
    @BindView(R.id.key_effect_c5m)
    LinearLayout keyC5M;
    @BindView(R.id.key_effect_d5m)
    LinearLayout keyD5M;
    @BindView(R.id.key_effect_f5m)
    LinearLayout keyF5M;
    @BindView(R.id.key_effect_g5m)
    LinearLayout keyG5M;
    @BindView(R.id.key_effect_a5m)
    LinearLayout keyA5M;

    private int mode, instrument, category, opern;
    private LinearLayout[] keys = new LinearLayout[36];
    private int[] voiceResId = new int[] {R.raw.c3, R.raw.d3, R.raw.e3, R.raw.f3, R.raw.g3, R.raw.a3, R.raw.b3,
            R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4, R.raw.c5, R.raw.d5, R.raw.e5,
            R.raw.f5, R.raw.g5, R.raw.a5, R.raw.b5, R.raw.c3m, R.raw.d3m, R.raw.f3m, R.raw.g3m, R.raw.a3m,
            R.raw.c4m, R.raw.d4m, R.raw.f4m, R.raw.g4m, R.raw.a4m, R.raw.c5m, R.raw.d5m, R.raw.f5m, R.raw.g5m,
            R.raw.a5m};
    private int[] voiceId = new int[36];
    private SoundPool soundPool;

    private final Handler viewStartHandler = new Handler(){
        public void handleMessage(Message msg) {
            int i = msg.what;
            keys[i].clearAnimation();
            Animation animation= AnimationUtils.loadAnimation(PlayActivity.this, R.anim.alpha_key_show);
            keys[i].setAlpha(1);
            keys[i].startAnimation(animation);
        };
    };

    private final Handler viewGoneHandler = new Handler(){
        public void handleMessage(Message msg) {
            int i = msg.what;
            keys[i].clearAnimation();
            Animation animation= AnimationUtils.loadAnimation(PlayActivity.this, R.anim.alpha_key_gone);
            keys[i].startAnimation(animation);
        };
    };

    private final Handler viewEndHandler = new Handler(){
        public void handleMessage(Message msg) {
            int i = msg.what;
            keys[i].setAlpha(0);
        };
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        instrument = intent.getIntExtra("instrument", 0);
        category = intent.getIntExtra("category", 0);
        //opern = intent.getIntExtra("opern", 0);

        initSoundPool();
        initView();
    }

    private void initSoundPool() {
        SoundPool.Builder spb = new SoundPool.Builder();
        spb.setMaxStreams(10);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        spb.setAudioAttributes(attrBuilder.build());
        soundPool = spb.build();
    }

    private void initView() {
        switch (mode) {
            case 0:
                textViewModeName.setText(R.string.mode_free);
                //textViewOpern.setText("");
            case 1:
                textViewModeName.setText(R.string.mode_opern);
                //textViewOpern.setText("曲谱：" + getResources().getStringArray(R.array.spinner_opern)[opern]);
        }

        if (instrument == 0 && category == 0) {
            textViewInstrumentName.setText(R.string.piano_with_21_keys);
        } else if (instrument == 0 && category == 1) {
            textViewInstrumentName.setText("乐器：15键钢琴");
        } else {
            textViewInstrumentName.setText("乐器：7孔笛");
        }

        btnPlayOver.setOnClickListener((View v)->{
            Intent intent = new Intent(this, PlayListenActivity.class);
            startActivity(intent);
            finish();
        });

        keys = new LinearLayout[] {keyC3, keyD3, keyE3, keyF3, keyG3, keyA3, keyB3, keyC4, keyD4,
                keyE4, keyF4, keyG4, keyA4, keyB4, keyC5, keyD5, keyE5, keyF5, keyG5, keyA5, keyB5,
                keyC3M, keyD3M, keyF3M, keyG3M, keyA3M, keyC4M, keyD4M, keyF4M, keyG4M, keyA4M,
                keyC5M, keyD5M, keyF5M, keyG5M, keyA5M};

        for (int i = 0; i < keys.length; ++i) {
            voiceId[i] = soundPool.load(this, voiceResId[i], 1);
            final int fi = i;
            keys[i].setOnClickListener((View v) -> {
                soundPool.play(voiceId[fi], 1, 1, 0, 0, 1);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            Message msg1 = new Message(), msg2 = new Message(), msg3 = new Message();
                            msg1.what = fi;
                            msg2.what = fi;
                            msg3.what = fi;
                            viewStartHandler.sendMessage(msg1);
                            Log.d("TEST","THREAD1");
                            Thread.sleep(100);
                            viewGoneHandler.sendMessage(msg2);
                            Log.d("TEST","THREAD2");
                            Thread.sleep(100);
                            viewEndHandler.sendMessage(msg3);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            });
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
