package com.papermelody.activity;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.widget.TextView;

import com.papermelody.R;

import butterknife.BindInt;
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

    private int mode, instrument, category, opern;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        Intent intent = getIntent();
        mode = intent.getIntExtra("mode", 0);
        instrument = intent.getIntExtra("instrument", 0);
        category = intent.getIntExtra("category", 0);
//        TextView textViewOpern = (TextView)findViewById(R.id.text_opern);
//        textViewOpern.setText("regrewger");
        if (mode == 0) {
            Log.i("s","mode==0");
            textViewMode.setText("模式：自由演奏");
        } else {
            Log.i("s","mode==1");
            opern = intent.getIntExtra("opern", 0);
            textViewMode.setText("模式：跟谱演奏");
            textViewOpern.setText(getResources().getStringArray(R.array.spinner_opern)[opern]);
        }
        if (instrument == 0 && category == 0) {
            Log.i("s","instrument == 0 && category == 0");
            textViewInstrument.setText("乐器：15键钢琴");
        } else if (instrument == 0 && category == 1) {
            Log.i("s","instrument == 0 && category == 1");
            textViewInstrument.setText("乐器：21键钢琴");
        } else {
            Log.i("s","instrument == 1");
            textViewInstrument.setText("乐器：7孔笛");
        }
        // TODO;
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_play;
    }
}
