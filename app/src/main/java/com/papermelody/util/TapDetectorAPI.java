package com.papermelody.util;

import android.media.Image;

import com.papermelody.activity.PlayActivity;

import org.opencv.core.Mat;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class TapDetectorAPI {
    /**
     * 提供手指识别过程的接口
     */

    public static int getKey(Image image) {
        // TODO:
        Mat rgbaMat = ImageUtil.imageToRgba(image);
        //Log.d("TESTMAT", rgbaMat.rows()+" "+rgbaMat.cols());
        int key = PlayActivity.KEY_A3;
        return key;
    }
}
