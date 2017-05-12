package com.papermelody.util;

import java.util.ArrayList;
import java.util.List;
import android.media.Image;
import android.util.Log;

import com.papermelody.tapdetect.FingerDetector;
import com.papermelody.tapdetect.HandDetector;
import com.papermelody.tapdetect.TapDetector;
import com.papermelody.tapdetect.Util;
import com.papermelody.tapdetect.facade.Tap;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.imgproc.Imgproc;

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


    public static List<Point> getTap(Image image) {
        Mat yuv = ImageUtil.imageToMat(image);
        Mat im = ImageUtil.yuvToBgr(image, yuv);

        double shrink_ratio = Util.resize(im);
        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);

        List<Point> taps = Tap.getTaps(im);

        for (Point pt: taps) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }

        return taps;
    }

    public static List<List<Point>> getAllForDebug(Image image) {
        Mat yuv = ImageUtil.imageToMat(image);
        Mat im = ImageUtil.yuvToBgr(image, yuv);

        double shrink_ratio = Util.resize(im);

        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);

        Log.w("size", "" + im.size());
        Log.w("channels", "" + im.channels());

        Mat hand = hd.getHand(im);

        List<MatOfPoint> hand_contour = Util.largeContours(hand, 1200);
        List<Point> hand_contour_pt = Util.contoursToPoints(hand_contour);

        List<Point> fingers = fd.getFingers(im, hand);
        Log.w("fingers", "" + fingers);

        List<Point> taps = td.getTapping(im, fingers);
        Log.w("taps", "" + taps);

        for (Point pt: hand_contour_pt) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }
        for (Point pt: fingers) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }
        for (Point pt: taps) { pt.x /= shrink_ratio; pt.y /= shrink_ratio; }

        List<List<Point>> ret = new ArrayList<>();
        ret.add(hand_contour_pt);
        ret.add(fingers);
        ret.add(taps);

        return ret;
    }
}
