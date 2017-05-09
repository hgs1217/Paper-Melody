package com.papermelody.activity;

import android.content.Intent;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
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

    private int mode, instrument, category, opern;
    private LinearLayout[] keys = new LinearLayout[7];
    private int[] voiceResId = new int[] {R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4};
    private int[] voiceId = new int[7];
    private SoundPool soundPool;

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

        keys = new LinearLayout[] {keyC4, keyD4, keyE4, keyF4, keyG4, keyA4, keyB4};

        for (int i = 0; i < keys.length; ++i) {
            voiceId[i] = soundPool.load(this, voiceResId[i], 1);
            final int fi = i;
            keys[i].setOnClickListener((View v) -> {
                soundPool.play(voiceId[fi], 1, 1, 0, 0, 1);
                Animation animation= AnimationUtils.loadAnimation(this, R.anim.alpha_key_show);
                keys[fi].setAlpha(1);
                keys[fi].startAnimation(animation);
                new Handler().postDelayed(new Runnable(){
                    public void run() {
                        keys[fi].setAlpha(0);
                    }
                }, 200);
            });
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
