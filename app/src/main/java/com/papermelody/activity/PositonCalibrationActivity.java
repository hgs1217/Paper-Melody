package com.papermelody.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.SeekBar;
import android.widget.TextView;

import com.papermelody.R;

import butterknife.BindView;

import static com.papermelody.R.id.seekbar3;
import static com.papermelody.R.id.seekbar4;
import static com.papermelody.R.id.seekbar5;
import static com.papermelody.R.id.seekbar6;
import static com.papermelody.R.id.textView12;
import static com.papermelody.R.id.textView13;
import static com.papermelody.R.id.textView7;
import static com.papermelody.R.id.textView8;

/**
 * Created by 潘宇杰 on 2017-6-18 0018.
 */

public class PositonCalibrationActivity extends BaseActivity {
    @BindView(seekbar3)
    SeekBar seek_bar3;
    @BindView(seekbar4)
    SeekBar  seek_bar4;
    @BindView(seekbar5)
    SeekBar  seek_bar5;
    @BindView(seekbar6)
    SeekBar seek_bar6;
    @BindView(textView7)
    TextView tLeftup;
    @BindView(textView8)
    TextView tLeftdown;
    @BindView(textView12)
    TextView tRightup;
    @BindView(textView13)
    TextView tRightdown;
    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);

    SharedPreferences.Editor editor = pref.edit();

    private  int value_temp;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        seek_bar3.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        seek_bar4.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        seek_bar5.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());
        seek_bar6.setOnSeekBarChangeListener(new OnSeekBarChangeListenerImp());

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
            value_temp=progress;
            switch (seekBar.getId()){
                case seekbar3:editor.putInt("leftup", value_temp-50);tLeftup.setText(String.valueOf(value_temp-50));break;
                case seekbar4:editor.putInt("leftdown", value_temp-50);tLeftdown.setText(String.valueOf(value_temp-50));break;
                case seekbar5:editor.putInt("rightup", value_temp-50);tRightup.setText(String.valueOf(value_temp-50));break;
                case seekbar6:editor.putInt("rightdown",value_temp-50);tRightdown.setText(String.valueOf(value_temp-50));break;

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



}


