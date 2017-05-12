package com.papermelody.util;

import android.util.Log;

import com.papermelody.core.calibration.Calibration;

import org.opencv.core.Mat;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ImageProcessor {
    /**
     * 提供标定过程的接口
     */

    static{ System.loadLibrary("opencv_java3"); }

    public static Calibration.CalibrationResult getCalibrationCoordinate(Mat bgrMat, int targetHeightStart,
                                                                         int targetHeightEnd) {
        Log.d("TESTCAL", bgrMat.rows() + " " + bgrMat.cols());

        Calibration.CalibrationResult a;
        a = Calibration.main(bgrMat);

        return a;
    }

    public static List<Integer> getPlaySoundKey(Mat bgrMat, Calibration.CalibrationResult result) {
        List<Integer> keys = new ArrayList<>();
        return keys;
    }
}

