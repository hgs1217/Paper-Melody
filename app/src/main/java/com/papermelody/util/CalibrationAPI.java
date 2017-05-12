package com.papermelody.util;

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

    /*public static Calibration.CalibrationResult getCalibrationCoordinate(Image image) {
        Mat bgrMat = ImageUtil.imageToBgr(image);
        Log.d("TESTCAL", bgrMat.rows() + " " + bgrMat.cols());

        Calibration.CalibrationResult a;
        a = Calibration.main(bgrMat);

        return a;
    }*/

    public static Calibration.CalibrationResult getCalibrationCoordinate(Mat bgrMat) {
        Log.d("TESTCAL", bgrMat.rows() + " " + bgrMat.cols());

        Calibration.CalibrationResult a;
        a = Calibration.main(bgrMat);

        return a;
    }
}

