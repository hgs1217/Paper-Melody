package com.papermelody.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.support.annotation.IdRes;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.papermelody.R;
import com.roughike.bottombar.BottomBar;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    /**
     * 主菜单，将由四个Fragment组成，实现方式为底部toolbar
     * 当前页面供导入opencv包时测试使用，之后的版本需要将整个页面重做
     * 这个页面现在的一些写法，例如ButterKnife和lambda写法的使用，可作将来代码的模板风格参考
     * 同时首页放入了音效播放的实现，可供参考
     */

    @BindView(R.id.image_img)
    ImageView imageImg;
    @BindView(R.id.button_switch)
    Button buttonSwitch;
    @BindView(R.id.button_c4)
    Button buttonC4;
    @BindView(R.id.button_d4)
    Button buttonD4;
    @BindView(R.id.button_e4)
    Button buttonE4;
    @BindView(R.id.button_f4)
    Button buttonF4;
    @BindView(R.id.button_g4)
    Button buttonG4;
    @BindView(R.id.button_a4)
    Button buttonA4;
    @BindView(R.id.button_b4)
    Button buttonB4;
    @BindView(R.id.bottom_bar)
    BottomBar bottomBar;

    private int[] voiceResId = new int[]{R.raw.c4, R.raw.d4, R.raw.e4, R.raw.f4, R.raw.g4, R.raw.a4, R.raw.b4};
    private String[] buttonString = new String[]{"C4", "D4", "E4", "F4", "G4", "A4", "B4"};

    private SoundPool mSoundPool;
    private int[] voiceId = new int[7];
    private int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Button[] buttonSound = new Button[]{buttonC4, buttonD4, buttonE4, buttonF4, buttonG4, buttonA4, buttonB4};

        final Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.pyj)).getBitmap();
        imageImg.setImageBitmap(bitmap);

        buttonSwitch.setText("转换");
        buttonSwitch.setOnClickListener((View v) -> {
            i++;
            Mat rgbMat = new Mat();
            Mat grayMat = new Mat();
            //获取lena彩色图像所对应的像素数据
            Utils.bitmapToMat(bitmap, rgbMat);
            //将彩色图像数据转换为灰度图像数据并存储到grayMat中
            Imgproc.cvtColor(rgbMat, grayMat, Imgproc.COLOR_RGB2GRAY);
            //创建一个灰度图像
            Bitmap grayBmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.RGB_565);
            //将矩阵grayMat转换为灰度图像
            Utils.matToBitmap(grayMat, grayBmp);
            if (i % 2 == 1)
                imageImg.setImageBitmap(grayBmp);
            else
                imageImg.setImageBitmap(bitmap);
        });

        SoundPool.Builder spb = new SoundPool.Builder();
        spb.setMaxStreams(10);
        AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
        attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);
        spb.setAudioAttributes(attrBuilder.build());
        mSoundPool = spb.build();

        for (int i = 0; i < voiceId.length; ++i) {
            voiceId[i] = mSoundPool.load(this, voiceResId[i], 1);
            buttonSound[i].setText(buttonString[i]);
            final int fi = i;
            buttonSound[i].setOnClickListener((View v) -> {
                mSoundPool.play(voiceId[fi], 1, 1, 0, 0, 1);
            });
        }

        bottomBar.setOnTabSelectListener((@IdRes int tabId) -> {
            if (tabId == R.id.tab_1) {
                // do something
                Toast.makeText(getApplicationContext(), "tab1 selected", Toast.LENGTH_SHORT).show();
            }
        });

        bottomBar.setOnTabReselectListener((@IdRes int tabId) -> {
            if(tabId == R.id.tab_1){
                // do something
                Toast.makeText(getApplicationContext(), "tab1 reselected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }
}
