/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-06-15 10:58:43
*/

package tapdetect.facade;

import java.util.List;
import java.util.ArrayList;
// import java.util.stream.Collectors;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import tapdetect.ColorRange;
import tapdetect.Config;
import tapdetect.FingerDetector;
import tapdetect.ForegroundDetector;
import tapdetect.HandDetector;
import tapdetect.ImgLogger;
import tapdetect.TapDetector;
import tapdetect.Util;

public class Tap {
    static {
        // with opencv java, use Core.NATIVE_LIBRARY_NAME,
        // with opencv android, use "opencv_java3"
        System.loadLibrary("opencv_java3");  // this line only need to be carried out once
        ImgLogger.silent();
    }

    private static final HandDetector hd = new HandDetector();
    private static final FingerDetector fd = new FingerDetector();
    private static final TapDetector td = new TapDetector();
    private static final ForegroundDetector fgd = new ForegroundDetector();

    private static long lastProcess = 0;
    private static long processInterval;
    private static List<Point> resultCache = new ArrayList<>();

    public static long getProcessInterval() {
        return processInterval;
    }

    public static boolean readyForNextFrame() {
        return System.currentTimeMillis() - lastProcess > Config.PROCESS_INTERVAL_MS;
    }

    public static void reset() {
        ColorRange.reset();
    }


    /**
     * Facade for outside to use the tap detector
     *
     * @param im: a Image in the type of org.opencv.core.Mat
     *            required to be in <strong>YCrCb</strong> color mode!
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


    public static List<Point> getTaps(Mat im) {
        // TODO: add throttle

        // resize to the standard size
        double recover_ratio = 1.0 / Util.resize(im);
        Mat fgmask = fgd.getForeground(im);

        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);
        Mat hand = hd.getHand(im, fgmask);

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

    public static List<Point> getAll(Mat im,
                                     List<List<Point>> contoursOutput,
                                     List<TapDetector.TapDetectPoint> tapDetectPointsOutput
    ) {
        /**
         * @param: im: A image in color space BGR
         * @param: contoursOutput
         *      if is not null, apexes of the contour of hand will be saved
         * @param: tapDetectPointsOutput
         *      if is not null, all results of detected points will be saved
         * @retrun:
         *      A list of points detected as being tapping
         *      (nothing but `TapDetectPoint` with status `FALLING` in `tapDetectPointsOutput`)
         *  This function will modify `im` into YCrCb as well as a smaller size
         */

        if (!checkTime()) { return resultCache; }


        double recover_ratio = 1.0 / Util.resize(im);
        Mat fgmask = fgd.getForeground(im);

        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);
        Mat hand = hd.getHand(im, fgmask);

        List<MatOfPoint> contour = new ArrayList<>();
        List<Point> fingers = fd.getFingers(im, hand, contour);
        List<TapDetector.TapDetectPoint> taps = td.getTappingAll(im, fingers);

        if (contoursOutput != null) {
            contoursOutput.clear();
            for (MatOfPoint cnt : contour) {
                List<Point> cntPt = cnt.toList();
                for (Point pt: cntPt) {
                    pt.x *= recover_ratio;
                    pt.y *= recover_ratio;
                }
                contoursOutput.add(cntPt);
            }
        }

        for (TapDetector.TapDetectPoint pt : taps) {
            pt.getPoint().x *= recover_ratio;
            pt.getPoint().y *= recover_ratio;
        }

        if (contoursOutput != null) {
            tapDetectPointsOutput.clear();
            tapDetectPointsOutput.addAll(taps);
        }

        List<Point> ret = new ArrayList<>();
        resultCache.clear();

        for (TapDetector.TapDetectPoint pt : taps) {
            if (pt.isFalling()) {
                ret.add(pt.getPoint());
                resultCache.add(pt.getPoint());
            }
        }

        return ret;
    }

    private static boolean checkTime() {
        long t = System.currentTimeMillis();
        if (t - lastProcess < Config.PROCESS_INTERVAL_MS) {
            // too higher the camera fps
            return false;
        } else {
            processInterval = t - lastProcess;
            lastProcess = t;
            return true;
        }
    }
}
