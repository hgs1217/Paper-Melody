package com.papermelody.util;

import android.graphics.Bitmap;

import com.papermelody.core.calibration.Calibration;

import org.opencv.android.Utils;
import org.opencv.core.Mat;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CalibrationAPI {
    /**
     * 提供标定过程的接口
     */

    public static int[] getCalibrationCoordinate(Bitmap bitmap) {
        Mat rgbMat = new Mat();
        Utils.bitmapToMat(bitmap, rgbMat);

        int[ ] a  ;
        a= Calibration.main(rgbMat);
        return a;
    }
}
