package com.papermelody.util;

import java.util.List;
import android.media.Image;
import android.util.Log;

import com.papermelody.tapdetect.FingerDetector;
import com.papermelody.tapdetect.HandDetector;
import com.papermelody.tapdetect.TapDetector;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
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
        // TODO:
        Mat yuv = ImageUtil.imageToMat(image);
        Mat im = new Mat(image.getHeight(), image.getWidth(), CvType.CV_8UC4);
        Imgproc.cvtColor(yuv, im, Imgproc.COLOR_YUV2BGR_I420);
        Imgproc.cvtColor(im, im, Imgproc.COLOR_BGR2YCrCb);

        Log.w("size", "" + im.size());
        Log.w("channels", "" + im.channels());

        Mat hand = hd.getHand(im);

//        List<MatOfPoint> contours = Util.largeContours(hand, 0);
//        Log.w("hand_cnt", "" + contours.size());
//        for (MatOfPoint mat: contours) {
//            Log.w("hand", "" + mat.toList());
//        }
//
//        List<Point> fingers = new ArrayList<>();
//        for (MatOfPoint cnt: contours) {
//            fingers.addAll(cnt.toList());
//        }
        List<Point> fingers = fd.getFingers(im, hand);
        Log.w("fingers", "" + fingers);

        List<Point> taps = td.getTapping(im, fingers);
        Log.w("taps", "" + taps);

        return fingers;
    }
}
