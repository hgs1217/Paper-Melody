package com.papermelody.activity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.papermelody.R;

import org.opencv.android.Utils;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class MainActivity extends AppCompatActivity {

    static{ System.loadLibrary("opencv_java3"); }

    int i=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        final Bitmap bitmap = ((BitmapDrawable) getResources().getDrawable(R.drawable.pyj)).getBitmap();
        imageView.setImageBitmap(bitmap);


        final Button button2 = (Button)findViewById(R.id.button);
        button2.setText("转换");
        button2.setOnClickListener(new Button.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
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
            }

        });
    }
}
