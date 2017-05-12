/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-11 11:00:34
*/

package com.papermelody.tapdetect;

import java.util.List;
import java.util.ArrayList;

import org.opencv.core.Point;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

public class TapDetector {
    public List<Point> getTapping(Mat im) {
        FingerDetector fd = new FingerDetector();
        List<Point> fingers = fd.getFingers(im);

        return getTapping(im, fingers);
    }

    public List<Point> getTapping(Mat im, List<Point> fingers) {
        List<Point> tapping = new ArrayList<>();

        for (Point point : fingers) {
            if (point.y > Config.TAP_THRESHOLD_ROW) {
                tapping.add(point);
            }
        }

        Mat tapping_im = Util.drawPoints(im, tapping, new Scalar(0, 255, 0));
        ImgLogger.info("20_tapping.jpg", tapping_im);
        return tapping;
    }
}