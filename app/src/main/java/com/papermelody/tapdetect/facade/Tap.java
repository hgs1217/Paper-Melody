/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-11 11:00:34
*/

package com.papermelody.tapdetect.facade;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.opencv.core.Mat;
import org.opencv.core.Point;

import com.papermelody.tapdetect.FingerDetector;
import com.papermelody.tapdetect.HandDetector;
import com.papermelody.tapdetect.TapDetector;
import com.papermelody.tapdetect.Util;

public class Tap {

    /**
     * Facade for outside to use the tap detector
     *
     * @param im: a Image in the type of org.opencv.core.Mat
     *            required to be in <strong>YCrCb</strong> color mode!
     * @return : a <code>List</code> of <code>Points</code>
     * which takes the left top point as (0, 0) point
     *
     * The coordinates may contain decimals, just round them
     *
     * <p>
     * <br> Usage:
     * <code>
     * <br>  Mat im = Imgcodecs.imread("samples/foo.jpg");
     * <br>  List<Point> taps = Tap.getTaps(im);
     * <br>  Point pt = taps.get(0);
     * <br>  System.out.println("Hurrah, someone taps at (" + pt.x + ", " + pt.y + ")");
     * </code>
     */
    public static List<Point> getTaps(Mat im) {
        HandDetector hd = new HandDetector();
        FingerDetector fd = new FingerDetector();
        TapDetector td = new TapDetector();

        // resize to the standard size
        double shrink_ratio = Util.resize(im);

        Mat hand = hd.getHand(im);
        List<Point> fingers = fd.getFingers(im, hand);
        List<Point> taps = td.getTapping(im, fingers);

        for (Point pt: taps) {
            pt.x /= shrink_ratio;
            pt.y /= shrink_ratio;
        }

        return taps;
    }
}
