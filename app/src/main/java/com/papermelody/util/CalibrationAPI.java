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

    public static Calibration.CalibrationResult getCalibrationCoordinate(Image image) {
        Mat rgbaMat = ImageUtil.imageToRgba(image);
        Log.d("TESTCAL", rgbaMat.rows() + " " + rgbaMat.cols());

       Calibration.CalibrationResult a;
        a = Calibration.main(rgbaMat);

        return a;
    }
}

