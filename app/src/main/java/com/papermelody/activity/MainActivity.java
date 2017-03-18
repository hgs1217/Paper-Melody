package com.papermelody.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.papermelody.R;
import com.papermelody.R2;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import butterknife.BindView;

public class MainActivity extends BaseActivity {
    /**
     * 主页面
     * 当前页面供导入opencv包时测试使用，之后的版本需要将整个页面重做
     * 这个页面现在的一些写法，例如ButterKnife和lambda写法的使用，可作将来代码的模板风格参考
     */

    @BindView(R2.id.imageView)
    ImageView imageView;
    @BindView(R2.id.button)
    Button button2;

    private int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.pyj)).getBitmap();
        imageView.setImageBitmap(bitmap);

        button2.setText("转换");
        button2.setOnClickListener((View v) -> {
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
                ImageView imageView = (ImageView)findViewById(R.id.imageView);
                if(i%2==1)
                    imageView.setImageBitmap(grayBmp);
                else
                    imageView.setImageBitmap(bitmap);
            });
    }

    @Override
    protected int getContentViewId() {
        return R.layout.activity_main;
    }
}
