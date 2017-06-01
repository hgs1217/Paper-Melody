package com.papermelody.util;


import tapdetect.facade.Tap;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import java.util.List;

/**
 * Created by gigaflw on 2017/5/11.
 */

public class TapDetectorAPI {
    /**
     * Bridge the tapdetect and the app
     * TODO: this file can be substituted by tapdetect.facade.Tap
     */
    public static List<Point> getTaps(Mat bgrMat) {
       return Tap.getTaps(bgrMat);
    }

    public static List<List<Point>> getAllForDebug(Mat im) {
        return Tap.getAllForDebug(im);
    }
}
