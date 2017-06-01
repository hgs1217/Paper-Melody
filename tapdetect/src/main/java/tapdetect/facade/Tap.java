/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-30 16:12:37
*/

package tapdetect.facade;

import java.util.List;
import java.util.ArrayList;
// import java.util.stream.Collectors;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import tapdetect.FingerDetector;
import tapdetect.HandDetector;
import tapdetect.TapDetector;
import tapdetect.Util;

public class Tap {
    //    static {
//        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);  // this line only need to be carried out once
//    }
    static {
        System.loadLibrary("opencv_java3");
    }

    /**
     * Facade for outside to use the tap detector
     *
     * @param im: a Image in the type of org.opencv.core.Mat
     * required to be in <strong>YCrCb</strong> color mode!
     * @return : a <code>List</code> of <code>Points</code>
     * which takes the left top point as (0, 0) point
     * <p>
     * <br> Usage:
     * <code>
     * <br>  Mat im = Imgcodecs.imread("samples/foo.jpg");
     * <br>  List<Point> taps = Tap.getTaps(im);
     * <br>  Point pt = taps.get(0);
     * <br>  System.out.println("Hurrah, someone taps at (" + pt.x + ", " + pt.y + ")");
     * </code>
     */

    private static final HandDetector hd = new HandDetector();
    private static final FingerDetector fd = new FingerDetector();
    private static final TapDetector td = new TapDetector();

    public static List<Point> getTaps(Mat im) {
        // resize to the standard size
        double recover_ratio = 1.0 / Util.resize(im);

        Mat hand = hd.getHand(im);
        List<Point> fingers = fd.getFingers(im, hand);
        List<Point> taps = td.getTapping(im, fingers);

        for (Point pt : taps) {
            pt.x *= recover_ratio;
            pt.y *= recover_ratio;
        }

        return taps;
        // return taps.stream().map(pt -> new Point((int) (pt.x / recover_ratio), (int) (pt.y / recover_ratio)))
        //         .collect(Collectors.toList());
    }

    public static List<List<Point>> getAllForDebug(Mat im) {
        /*
            e.g.
                List<List<Point>> ret = getAllForDebug(im);
                ret[0]   // contour points of detected hand
                ret[1]   // detected fingers
                ret[2]   // detected points where tap happens
        */

        double recover_ratio = 1.0 / Util.resize(im);

        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);

        Mat hand = hd.getHand(im);

        List<MatOfPoint> hand_contour = Util.largeContours(hand, 1200);
        List<Point> hand_contour_pt = Util.contoursToPoints(hand_contour);

        List<Point> fingers = fd.getFingers(im, hand);

        List<Point> taps = td.getTapping(im, fingers);

        for (Point pt : hand_contour_pt) {
            pt.x *= recover_ratio;
            pt.y *= recover_ratio;
        }
        for (Point pt : fingers) {
            pt.x *= recover_ratio;
            pt.y *= recover_ratio;
        }
        // no need to shrink points in taps because Point in taps and Point in finger have same reference

        List<List<Point>> ret = new ArrayList<>();
        ret.add(hand_contour_pt);
        ret.add(fingers);
        ret.add(taps);

        return ret;
    }
}
