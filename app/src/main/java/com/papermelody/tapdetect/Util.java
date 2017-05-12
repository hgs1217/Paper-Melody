/*
* @Author: zhouben
* @Date:   2017-05-10 09:15:23
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-10 22:26:55
*/
package com.papermelody.tapdetect;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
//import java.util.stream.Collectors;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Size;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;

public class Util {
    public static final Scalar SCALAR_BLACK = new Scalar(0, 0, 0);
    public static final Scalar SCALAR_WHITE = new Scalar(255, 255, 255);

    public static double resize(Mat im, int height) {
        /*
           @return: a double indicating the shrink ratio
         */

        Size size = im.size();
        int width = (int) (height * size.width / size.height);

        Imgproc.resize(im, im, new Size(width, height));
        return (double) height / size.height;
    }

    public static double resize(Mat im) {
        return Util.resize(im, Config.IM_HEIGHT);
    }

    public static List<MatOfPoint> largeContours(Mat im, int area) {
        List<MatOfPoint> contours = new ArrayList<>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(im, contours, hierarchy, 1, Imgproc.RETR_LIST);

        // FIXME: stream requires Android sdk >= 24
        List<MatOfPoint> ret = new ArrayList<>();
        for (MatOfPoint cnt: contours) {
            if (Imgproc.contourArea(cnt) > area) { ret.add(cnt); }
        }
        return ret;
    }

    public static List<Point> contoursToPoints(List<MatOfPoint> contours) {
        List<Point> ret = new ArrayList<>();
        for (MatOfPoint cnt: contours) {
            ret.addAll(cnt.toList());
        }
        return ret;
    }

    public static Mat drawContours(Mat im, List<MatOfPoint> contours, Scalar color) {
        Mat im_cpy = im.clone();

        for (int ind = 0; ind < contours.size(); ++ind) {
            Imgproc.drawContours(im_cpy, contours, ind, color, 2);
        }

        return im_cpy;
    }

    public static Mat fillContours(Size size, List<MatOfPoint> contours, Point[] seeds) {
        Mat im = Mat.zeros(size, CvType.CV_8U);
        Mat mask = Mat.zeros(new Size(size.width + 2, size.height + 2), CvType.CV_8U);

        for (int ind = 0; ind < contours.size(); ++ind) {
            Imgproc.drawContours(mask, contours, ind, Util.SCALAR_WHITE);
        }

        for (Point p : seeds) {
            Imgproc.floodFill(im, mask, p, Util.SCALAR_WHITE);
        }

        return im;
    }

    public static Mat drawPoints(Mat im, List<Point> points, Scalar color) {
        Mat im_cpy = im.clone();

        for (Point point : points) {
            Imgproc.circle(im_cpy, point, 1, color, 2);  // radius=2, thickness=3
        }

        return im_cpy;
    }

    public static double intersectCos(Point start, Point end1, Point end2) {
        Point v1 = new Point(end1.x - start.x, end1.y - start.y);
        Point v2 = new Point(end2.x - start.x, end2.y - start.y);

        double dot = Math.abs(v1.x * v2.x + v1.y * v2.y);
        double mod = Math.sqrt(v1.x * v1.x + v1.y * v1.y) * Math.sqrt(v2.x * v2.x + v2.y * v2.y);
        return dot / mod;
    }
}