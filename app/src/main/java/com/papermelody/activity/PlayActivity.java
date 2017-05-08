package com.papermelody.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.util.ToastUtils;

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
    @BindView(R.id.text_opern)
    TextView textViewOpern;
    @BindView(R.id.text_instrument)
    TextView textViewInstrument;
    @BindView(R.id.btn_play_over)
    Button btnPlayOver;

    private int mode, instrument, category, opern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        instrument = intent.getIntExtra("instrument", 0);
        category = intent.getIntExtra("category", 0);

        if (mode == 0) {
            textViewMode.setText("模式：自由演奏");
            textViewOpern.setText("");
        } else {
            opern = intent.getIntExtra("opern", 0);
            textViewMode.setText("模式：跟谱演奏");
            textViewOpern.setText("曲谱：" + getResources().getStringArray(R.array.spinner_opern)[opern]);
        }
        if (instrument == 0 && category == 0) {
            textViewInstrument.setText("乐器：15键钢琴");
        } else if (instrument == 0 && category == 1) {
            textViewInstrument.setText("乐器：21键钢琴");
        } else {
            textViewInstrument.setText("乐器：7孔笛");
        }
        btnPlayOver.setOnClickListener((View v)->{
            Intent intent1 = new Intent(getApplicationContext(), PlayListenActivity.class);
            startActivity(intent1);
        });
        // TODO;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
