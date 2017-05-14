package com.papermelody.util;

import android.util.Log;

import com.papermelody.core.calibration.Calibration;

import org.opencv.core.Mat;
import org.opencv.core.Point;

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
        a = Calibration.main(bgrMat,targetHeightStart,targetHeightEnd);

        return a;
    }

    public static Calibration.TransformResult getKeyTransform(Calibration.CalibrationResult result){
        return Calibration.transform(result);

    }


    public static List<Integer> getPlaySoundKey(Mat bgrMat, Calibration.TransformResult result) {
        List<Integer> keys = new ArrayList<>();
        int count[]=new int [37];
        for (int i=0;i<count.length;i++){count[i]=0;}
        List<Point> tap=TapDetectorAPI.getTap(bgrMat);
        if (tap.isEmpty()){return keys;}
        else {
            for (int i=0;i<tap.size();i++){
                count[Calibration.Key(result,tap.get(i).x,tap.get(i).y)]++;

            }
            for (int i=0;i<36;i++){if (count[i]!=0)keys.add(i);}
            return keys;


        }

    }
}

