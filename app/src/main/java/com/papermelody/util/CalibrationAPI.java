package com.papermelody.util;

import org.opencv.core.Mat;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class CalibrationAPI {
    /**
     * 提供标定过程的接口
     */

    static{ System.loadLibrary("opencv_java3"); }

    public static int[] getCalibrationCoordinate(Mat rgbaMat) {
        int[ ] a = new int []{0, 0, 0, 0, 0, 0, 0, 0};
        //a = Calibration.main(rgbaMat);
        return a;
    }
}
