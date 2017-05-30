package com.papermelody.activity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import com.papermelody.R;

import butterknife.BindView;

/**
 * Created by gigaflower on 2017/5/30.
 */

public class SettingsDevActivity extends BaseActivity {
    /**
     * Settings for tap detection algorithm     by gigaflw
     */


    @BindView(R.id.seekbar_im_height)           SeekBar sb_im_height;
//    @BindView(R.id.seekbar_hand_area)           SeekBar sb_hand_area;
//    @BindView(R.id.seekbar_finger_tip_step)     SeekBar sb_finger_tip_step;
//    @BindView(R.id.seekbar_finger_tip_width)    SeekBar sb_finger_tip_width;
//    @BindView(R.id.seekbar_tap_threshold_row)   SeekBar sb_finger_tap_threshold_row;

    @BindView(R.id.text_im_height)           TextView text_im_height;
//    @BindView(R.id.textview_hand_area)           TextView text_hand_area;
//    @BindView(R.id.textview_finger_tip_step)     TextView text_finger_tip_step;
//    @BindView(R.id.textview_finger_tip_width)    TextView text_finger_tip_width;
//    @BindView(R.id.textview_tap_threshold_row)   TextView text_finger_tap_threshold_row;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sb_im_height.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                text_im_height.setText(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
    }


    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_dev;
    }
}
