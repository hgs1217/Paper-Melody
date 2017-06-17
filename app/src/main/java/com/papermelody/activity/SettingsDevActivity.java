package com.papermelody.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;

import com.papermelody.R;
import com.papermelody.util.App;
import com.papermelody.util.NetworkFailureHandler;
import com.papermelody.util.RetrofitClient;
import com.papermelody.util.SocialSystemAPI;
import com.papermelody.util.ToastUtil;

import java.lang.reflect.Field;

import butterknife.BindView;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import tapdetect.Config;

/**
 * Created by gigaflower on 2017/5/30.
 */

public class SettingsDevActivity extends BaseActivity {
    /**
     * Settings for tap detection algorithm     by gigaflw
     */

    @BindView(R.id.edit_server_ip)
    EditText editServerIP;
    @BindView(R.id.btn_server_ip)
    Button btnServerIP;
    @BindView(R.id.btn_reset)
    Button btnReset;
    @BindView(R.id.btn_play_listen)
    Button btnPlayListen;
    @BindView(R.id.btn_to_upload)
    Button btnUpload;

    @BindView(R.id.seekbarY)
    SeekBar seekBarY;
    @BindView(R.id.seekbarCr)
    SeekBar seekBarCr;
    @BindView(R.id.seekbarCb)
    SeekBar seekBarCb;

    @BindView(R.id.seekbar_captionY)
    TextView seekBarCaptionY;
    @BindView(R.id.seekbar_captionCr)
    TextView seekBarCaptionCr;
    @BindView(R.id.seekbar_captionCb)
    TextView seekBarCaptionCb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 初始化用于修改ip地址的编辑框和按钮
        editServerIP.setText(App.getServerIP());
        btnServerIP.setOnClickListener((view) -> {
            String serverIP = editServerIP.getText().toString();
            App.setServerIP(serverIP);
            RetrofitClient.updateBaseUrl(serverIP);
            ToastUtil.showShort("当前ip地址被修改为：" + serverIP);
            editServerIP.clearFocus();
            closeInputKeyboard();
        });

        btnReset.setOnClickListener((view) -> {
            SocialSystemAPI api = RetrofitClient.getSocialSystemAPI();
            addSubscription(api.reset()
                    .flatMap(NetworkFailureHandler.httpFailureFilter)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(response -> response)
                    .subscribe(
                            response -> {
                                if (response.getError() == 0) {
                                    ToastUtil.showShort("服务器已重置");
                                }
                            },
                            NetworkFailureHandler.basicErrorHandler
                    ));
        });

        btnPlayListen.setOnClickListener((view) -> {
            Intent intent = new Intent(this, PlayListenActivity.class);
            startActivity(intent);
        });

        btnUpload.setOnClickListener((view) -> {
            Intent intent = new Intent(this, UploadActivity.class);
            intent.putExtra(PlayActivity.FILENAME, "Kissbye.mid");
            startActivity(intent);
        });


        TextView[] colorRangeCaption = { seekBarCaptionY, seekBarCaptionCr, seekBarCaptionCb};
        SeekBar[] colorRangeSeekBar = { seekBarY, seekBarCr, seekBarCb};
        String[] caption = {"Y", "Cr", "Cb"};

        for (int i=0; i<3; ++i) {
            final int index = i;
            double paraValue = Config.FINGER_COLOR[i];
            colorRangeCaption[i].setText(caption[i] + ": " + paraValue);
            colorRangeSeekBar[i].setMax(255);
            colorRangeSeekBar[i].setProgress((int) paraValue);

            colorRangeSeekBar[i].setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    Config.FINGER_COLOR[index] = (double) progress;
                    colorRangeCaption[index].setText(caption[index] + ": " + Config.FINGER_COLOR[index]);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
        }

        // Use reflect to dynamically set the value of seek bars
        // according to values in `tapdetect.Config`
        Field[] parameters = Config.class.getDeclaredFields();
        int ind = 0;
        for (Field field : parameters) {
            if (field.getType() != int.class && field.getType() != double.class) {
                continue;
            }
            int textViewId = getResources().getIdentifier("seekbar_caption" + (ind + 1), "id", getPackageName());
            int seekBarId = getResources().getIdentifier("seekbar" + (ind + 1), "id", getPackageName());
            final TextView textView = (TextView) findViewById(textViewId);
            final SeekBar seekBar = (SeekBar) findViewById(seekBarId);

            final String paraName = field.getName().replace("_", " ").toLowerCase();

            try {
                int paraValue;
                if (field.getType() == double.class) {
                    paraValue = (int) (field.getDouble(Config.class) * 100);
                    textView.setText(paraName + ": " + (double) paraValue / 100.0);
                } else {
                    paraValue = field.getInt(Config.class);
                    textView.setText(paraName + ": " + paraValue);
                }
                seekBar.setMax(Math.max(10, paraValue * 2));
                seekBar.setProgress(paraValue);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                // if this error happens, blame it on me     by gigaflw
            }

            seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    try {
                        if (field.getType() == double.class) {
                            field.set(Config.class, (double) progress / 100.0);
                            textView.setText(paraName + ": " + field.getDouble(Config.class));
                        } else {
                            field.set(Config.class, progress);
                            textView.setText(paraName + ": " + field.getInt(Config.class));
                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                        // if this error happens, blame it on me     by gigaflw
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                }
            });
            ind += 1;
        }
    }

    private void closeInputKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editServerIP.getWindowToken(), 0);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_dev;
    }
}
