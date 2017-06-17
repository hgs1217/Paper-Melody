package com.papermelody.util;

import android.util.Log;

import com.papermelody.core.calibration.Calibration;
import com.papermelody.core.calibration.CalibrationResult;
import com.papermelody.core.calibration.CalibrationResultsOfLatest5;
import com.papermelody.core.calibration.TransformResult;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import tapdetect.facade.Tap;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ImageProcessor {
    /**
     * 提供标定过程的接口
     */

    static { System.loadLibrary("opencv_java3"); }

    private static int cntOfCall = 0;
    private static CalibrationResultsOfLatest5 calibrationResultsOfLatest5;

    public static CalibrationResult getCalibrationCoordinate (Mat bgrMat, int targetHeightStart,
                                                              int targetHeightEnd) {
        /**
         * 传入照片（mat格式）获取标定得到的关键点结果
         */

        Log.d("TESTCAL", bgrMat.rows() + " " + bgrMat.cols());

        CalibrationResult a;
        a = Calibration.main(bgrMat, targetHeightStart, targetHeightEnd);
        Log.d("TESThist111", a.getLeftLowX() + " " + a.getLeftUpX());

        cntOfCall++;

        return a;
    }

    public static boolean getCalibrationStatus (CalibrationResult result) {
        /**
         * 判断标定是否成功
         */

        calibrationResultsOfLatest5 = Calibration.getNewCalibrationResultsOfLatest5(
                calibrationResultsOfLatest5, result);

        return (result.isFlag() && Calibration.whether_stable(calibrationResultsOfLatest5)) ||
                (ImageProcessor.getCntOfCall() > 200 && result.isFlag());
                //ImageProcessor.getCntOfCall() > 10;
    }

    public static TransformResult getKeyTransform (CalibrationResult result) {

        return Calibration.transform(result);
    }

    public static List<Integer> getPlaySoundKey (Mat bgrMat, TransformResult result) {
        return getPlaySoundKey(bgrMat, result, null);
    }
    public static List<Integer> getPlaySoundKey (Mat bgrMat, TransformResult result, List<Point> tap) {
        /**
         * 获取坐标判定得到的按键结果
         */

        List<Integer> keys = new ArrayList<>();
        int count[] = new int [37];   //
        for (int i = 0; i < count.length; i++) {
            count[i] = 0;
        }
        if (tap == null) {
            tap = Tap.getTaps(bgrMat);
        }
        if (tap.isEmpty()) {
            return keys;
        }
        else {
            for (int i = 0; i < tap.size(); i++) {
                count[Calibration.Key(result, tap.get(i).x, tap.get(i).y)]++;
            }
            for (int i = 0; i < 36; i++) {
                if (count[i] != 0) {
                    keys.add(i);
                }
            }
            return keys;
        }
    }

    public static void initProcessor() {
        cntOfCall = 0;
        calibrationResultsOfLatest5 = new CalibrationResultsOfLatest5();
    }

    public static int getCntOfCall() {
        return cntOfCall;
    }

    /**
     * 要预先给标定部分提供乐器种类，从而做出对应的按键策略
     * @param instrumentType
     */
    public static void setInstrumentType(int instrumentType) {
        Calibration.setInstrumentType(instrumentType);
    }
}

