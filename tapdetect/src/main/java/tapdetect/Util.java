/*
* @Author: zhouben
* @Date:   2017-05-10 09:15:23
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-30 15:11:45
*/
package tapdetect;

import java.lang.Math;
import java.util.List;
import java.util.ArrayList;
// import java.util.stream.Stream;
// import java.util.stream.Collectors;

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
             = new height / old height
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
        // return contours.stream()
        //         .filter(cnt -> Imgproc.contourArea(cnt) > area)
        //         .collect(Collectors.toList());

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


    public static void drawContours(Mat im, List<MatOfPoint> contours, Scalar color) {
        for (int ind = 0; ind < contours.size(); ++ind) {
            Imgproc.drawContours(im, contours, ind, color, 2);
        }
    }

    public static void drawContourByPoints(Mat im, List<List<Point>> contours, Scalar color) {
        for (List<Point> contour: contours) {
            int len = contour.size();
            for (int i=0; i < len; ++i) {
                int next_i = (i + 1) % len;
                Imgproc.line(im, contour.get(i), contour.get(next_i), color, 2);
            }
        }
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

    public static void drawPoints(Mat im, List<Point> points, Scalar color) {
        for (Point point : points) {
            Imgproc.circle(im, point, 0, color, 3);  // radius=0, thickness=3
        }
    }

    public static void drawPoint(Mat im, Point point, Scalar color) {
        Imgproc.circle(im, point, 0, color, 3);  // radius=0, thickness=3
    }

    public static double intersectCos(Point start, Point end1, Point end2) {
        Point v1 = new Point(end1.x - start.x, end1.y - start.y);
        Point v2 = new Point(end2.x - start.x, end2.y - start.y);

        double dot = Math.abs(v1.x * v2.x + v1.y * v2.y);
        double mod = Math.sqrt((v1.x * v1.x + v1.y * v1.y) * (v2.x * v2.x + v2.y * v2.y));
        return dot / mod;
    }


    public static double pointDist(Point p1, Point p2) {
        return Math.sqrt((p1.x - p2.x) * (p1.x - p2.x) + (p1.y - p2.y) * (p1.y - p2.y));
    }

    public static Point incenter(Point p1, Point p2, Point p3) {
        // incenter of ∆ABC = (|AB| * C + |BC| * A + |AC| * B) / (|AB| + |BC| + |AC|)
        double d12 = pointDist(p1, p2);
        double d23 = pointDist(p2, p3);
        double d13 = pointDist(p1, p3);

        double circum = d12 + d23 + d13;
        return new Point(
                (p1.x * d23 + p2.x * d13 + p3.x * d12) / circum,
                (p1.y * d23 + p2.y * d13 + p3.y * d12) / circum
        );
    }

    public static Point averPoint(List<Point> points) {
        double x = 0.0, y = 0.0;
        for (Point pt: points) {
            x += pt.x;
            y += pt.y;
        }
        x /= points.size();
        y /= points.size();
        return new Point(x, y);
    }
}