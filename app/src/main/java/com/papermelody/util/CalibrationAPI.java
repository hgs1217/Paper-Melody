package com.papermelody.util;

import android.media.Image;
import android.util.Log;

import com.papermelody.core.calibration.Calibration;

import org.opencv.core.Mat;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CalibrationAPI {
    /**
     * 提供标定过程的接口
     */

    static{ System.loadLibrary("opencv_java3"); }

    public static int[] getCalibrationCoordinate(Image image) {
        Mat rgbaMat = ImageUtil.imageToRgba(image);

        int[ ] a = new int []{0, 0, 0, 0, 0, 0, 0, 0};
        a = Calibration.main(rgbaMat);
        for (int i=0; i<a.length; ++i) {
            Log.d("TESTA"+i, a[i]+" ");
        }
        return a;
    }
}
