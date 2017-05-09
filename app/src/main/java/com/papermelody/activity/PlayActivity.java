package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
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

    private int mode, instrument, category, opern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        instrument = intent.getIntExtra("instrument", 0);
        category = intent.getIntExtra("category", 0);
        //opern = intent.getIntExtra("opern", 0);

        initView();
    }

    private void initView() {
        if (mode == 0) {
            textViewModeName.setText(R.string.mode_free);
            //textViewOpern.setText("");
        } else {
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
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
