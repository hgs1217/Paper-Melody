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

    @BindView(R.id.edit_server_ip)  EditText editServerIP;
    @BindView(R.id.btn_server_ip)   Button btnServerIP;
    @BindView(R.id.btn_reset)       Button btnReset;
    @BindView(R.id.btn_play_listen) Button btnPlayListen;
    @BindView(R.id.seekbar1)        SeekBar sb1;
    @BindView(R.id.seekbar2)        SeekBar sb2;
    @BindView(R.id.seekbar3)        SeekBar sb3;
    @BindView(R.id.seekbar4)        SeekBar sb4;
    @BindView(R.id.seekbar5)        SeekBar sb5;
    @BindView(R.id.seekbar6)        SeekBar sb6;
    @BindView(R.id.seekbar7)        SeekBar sb7;
    @BindView(R.id.seekbar8)        SeekBar sb8;
    @BindView(R.id.seekbar9)        SeekBar sb9;
    @BindView(R.id.seekbar10)        SeekBar sb10;

    @BindView(R.id.seekbar_caption1) TextView text1;
    @BindView(R.id.seekbar_caption2) TextView text2;
    @BindView(R.id.seekbar_caption3) TextView text3;
    @BindView(R.id.seekbar_caption4) TextView text4;
    @BindView(R.id.seekbar_caption5) TextView text5;
    @BindView(R.id.seekbar_caption6) TextView text6;
    @BindView(R.id.seekbar_caption7) TextView text7;
    @BindView(R.id.seekbar_caption8) TextView text8;
    @BindView(R.id.seekbar_caption9) TextView text9;
    @BindView(R.id.seekbar_caption10) TextView text10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SeekBar[] seekbars = { sb1, sb2, sb3, sb4, sb5, sb6, sb7, sb8, sb9, sb10 };
        TextView[] texts = { text1, text2, text3, text4, text5, text6, text7, text8, text9, text10 };

        // 初始化用于修改ip地址的编辑框和按钮
        editServerIP.setText(App.getServerIP());
        btnServerIP.setOnClickListener((view) -> {
            String serverIP = editServerIP.getText().toString();
            App.setServerIP(serverIP);
            RetrofitClient.updateBaseUrl(serverIP);
            ToastUtil.showShort("当前ip地址被修改为："+serverIP);
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

    private void closeInputKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editServerIP.getWindowToken(), 0);
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_settings_dev;
    }
}
