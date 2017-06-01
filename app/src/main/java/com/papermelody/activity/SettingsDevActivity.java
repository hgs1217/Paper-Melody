package com.papermelody.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.TextView;

import com.papermelody.R;

import java.lang.reflect.Field;

import butterknife.BindView;
import tapdetect.Config;

/**
 * Created by gigaflower on 2017/5/30.
 */

public class SettingsDevActivity extends BaseActivity {
    /**
     * Settings for tap detection algorithm     by gigaflw
     */


    @BindView(R.id.seekbar1)        SeekBar sb1;
    @BindView(R.id.seekbar2)        SeekBar sb2;
    @BindView(R.id.seekbar3)        SeekBar sb3;
    @BindView(R.id.seekbar4)        SeekBar sb4;
    @BindView(R.id.seekbar5)        SeekBar sb5;
    @BindView(R.id.seekbar6)        SeekBar sb6;
    @BindView(R.id.seekbar7)        SeekBar sb7;

    @BindView(R.id.seekbar_caption1) TextView text1;
    @BindView(R.id.seekbar_caption2) TextView text2;
    @BindView(R.id.seekbar_caption3) TextView text3;
    @BindView(R.id.seekbar_caption4) TextView text4;
    @BindView(R.id.seekbar_caption5) TextView text5;
    @BindView(R.id.seekbar_caption6) TextView text6;
    @BindView(R.id.seekbar_caption7) TextView text7;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeekBar[] seekbars = { sb1, sb2, sb3, sb4, sb5, sb6, sb7 };
        TextView[] texts = { text1, text2, text3, text4, text5, text6, text7 };


        // Use reflect to dynamically set the value of seek bars
        // according to values in `tapdetect.Config`
        Field[] parameters = Config.class.getDeclaredFields();

        int ind = 0;
        for (Field field: parameters) {
            if (field.getType() != int.class) { continue; }
            final int index = ind;

            final String paraName = field.getName().replace("_", " ").toLowerCase();

            try {
                final int paraValue = field.getInt(Config.class);
                texts[ind].setText(paraName + ": " + paraValue);
                seekbars[ind].setMax(paraValue * 2);
                seekbars[ind].setProgress(paraValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                // if this error happens, blame it on me     by gigaflw
            }

            seekbars[index].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        field.set(Config.class, progress);
                        texts[index].setText(paraName + ": " + field.getInt(Config.class));
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        // if this error happens, blame it on me     by gigaflw
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {}

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {}
            });
            ind += 1;
        }
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_dev;
    }
}
