package com.papermelody.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import com.papermelody.R;

import butterknife.BindView;

import static com.papermelody.R.id.seekBar3;
import static com.papermelody.R.id.seekBar4;
import static com.papermelody.R.id.seekBar5;
import static com.papermelody.R.id.seekBar6;
import static com.papermelody.R.id.textView12;
import static com.papermelody.R.id.textView13;
import static com.papermelody.R.id.textView7;
import static com.papermelody.R.id.textView8;

/**
 * Created by 潘宇杰 on 2017-6-18 0018.
 */

public class PositonCalibrationActivity extends BaseActivity {
    @BindView(seekBar3)
    SeekBar seek_bar3;
    @BindView(seekBar4)
    SeekBar seek_bar4;
    @BindView(seekBar5)
    SeekBar seek_bar5;
    @BindView(seekBar6)
    SeekBar seek_bar6;
    @BindView(textView7)
    TextView tLeftup;
    @BindView(textView8)
    TextView tLeftdown;
    @BindView(textView12)
    TextView tRightup;
    @BindView(textView13)
    TextView tRightdown;
    @BindView(R.id.toolbar3)
    Toolbar toolbar;


    SharedPreferences.Editor editor;
    //Context context=getApplicationContext();
    SharedPreferences pref;

    private int value_temp;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initText();
        initToolbar();
        seek_bar3.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        seek_bar4.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        seek_bar5.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        seek_bar6.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());

    }


    private void initToolbar() {
        toolbar.setTitle("乐器位置标定设置");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener((View v) -> {
            finish();
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_position_calibration;
    }

    private class OnSeekBarChangeListenerImp implements
            SeekBar.OnSeekBarChangeListener {

        // 触发操作，拖动
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            editor = pref.edit();
            value_temp = progress;
            switch (seekBar.getId()) {
                case seekBar3: {
                    editor.putInt("leftup", value_temp - 50);
                    tLeftup.setText(String.valueOf(value_temp - 50));
                    break;
                }
                case seekBar4: {
                    editor.putInt("leftdown", value_temp - 50);
                    tLeftdown.setText(String.valueOf(value_temp - 50));
                    break;
                }
                case seekBar5: {
                    editor.putInt("rightup", value_temp - 50);
                    tRightup.setText(String.valueOf(value_temp - 50));
                    break;
                }
                case seekBar6: {
                    editor.putInt("rightdown", value_temp - 50);
                    tRightdown.setText(String.valueOf(value_temp - 50));
                    break;
                }

            }
            //text.layout((int) (progress * moveStep), 20, screenWidth, 80);
            //text.setText(getCheckTimeBySeconds(progress, startTimeStr));
            editor.commit();
        }

        // 表示进度条刚开始拖动，开始拖动时候触发的操作
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        // 停止拖动时候
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub
        }
    }

    public void initText() {
        pref = PreferenceManager.getDefaultSharedPreferences(this);

        tLeftup.setText(String.valueOf(pref.getInt("leftup", 0)));
        tLeftdown.setText(String.valueOf(pref.getInt("leftdown", 0)));
        tRightdown.setText(String.valueOf(pref.getInt("rightdown", 0)));
        tRightup.setText(String.valueOf(pref.getInt("rightup", 0)));
        seek_bar3.setProgress(pref.getInt("leftup", 0) + 50);
        seek_bar4.setProgress(pref.getInt("leftdown", 0) + 50);
        seek_bar5.setProgress(pref.getInt("rightup", 0) + 50);
        seek_bar6.setProgress(pref.getInt("rightdown", 0) + 50);

    }


}


