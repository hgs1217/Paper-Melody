package com.papermelody.util;

import android.util.Log;

import com.papermelody.tapdetect.FingerDetector;
import com.papermelody.tapdetect.HandDetector;
import com.papermelody.tapdetect.TapDetector;
import com.papermelody.tapdetect.Util;
import com.papermelody.tapdetect.facade.Tap;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigaflw on 2017/5/11.
 */

public class TapDetectorAPI {
    /**
     * Bridge the com.papermelody.tapdetect and the app
     */
    private static final HandDetector hd = new HandDetector();
    private static final FingerDetector fd = new FingerDetector();
    private static final TapDetector td = new TapDetector();


    public static List<Point> getTap(Mat bgrMat) {
       // Mat yuv = ImageUtil.imageToMat(image);
        //Mat im = ImageUtil.yuvToBgr(image, yuv);

        double shrink_ratio = Util.resize(bgrMat);
        Imgproc.cvtColor(bgrMat, bgrMat, Imgproc.COLOR_BGR2YCrCb);

        List<Point> taps = Tap.getTaps(bgrMat);

        for (Point pt: taps) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }

        return taps;
    }

    public static List<List<Point>> getAllForDebug(Mat im) {
//        Mat yuv = ImageUtil.imageToMat(image);
//        Mat im = ImageUtil.yuvToBgr(image, yuv);

        double shrink_ratio = Util.resize(im);

        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);

        Log.w("size", "" + im.size());
        Log.w("channels", "" + im.channels());

        Mat hand = hd.getHand(im);

        List<MatOfPoint> hand_contour = Util.largeContours(hand, 1200);
        List<Point> hand_contour_pt = Util.contoursToPoints(hand_contour);

        List<Point> fingers = fd.getFingers(im, hand);

        List<Point> taps = td.getTapping(im, fingers);

        Log.w("fingers", "" + fingers);
        Log.w("taps", "" + taps);

        for (Point pt: hand_contour_pt) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }
        for (Point pt: fingers) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }
        //  for (Point pt: taps) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }
        // no need because Point in taps and Point in finger have same reference

        // Log.w("fingers", "" + fingers);
        // Log.w("taps", "" + taps);

        List<List<Point>> ret = new ArrayList<>();
        ret.add(hand_contour_pt);
        ret.add(fingers);
        ret.add(taps);

        return ret;
    }
}
