/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-11 11:00:34
*/

package tapdetect;

import android.util.Log;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import org.opencv.core.Point;
import org.opencv.core.Mat;

public class TapDetector {
    enum FingerTipStatus {
        NOT_CARE, FALLING, LINGER, TAPPING, PRESSING,
    }

    public static class TapDetectPoint extends Point {
        FingerTipStatus status;

        TapDetectPoint(Point point, FingerTipStatus status) {
            super(point.x, point.y);
            this.status = status;
        }

        TapDetectPoint(TapDetectPoint other) {
            super(other.x, other.y);
            status = other.status;
        }

        int distanceFrom(Point pt) {
            return Math.abs((int) (x - pt.x)) / 2 + Math.abs((int) (y - pt.y));
        }

        public boolean isFalling() {
            return status == FingerTipStatus.FALLING;
        }

        public boolean isLingering() {
            return status == FingerTipStatus.LINGER;
        }

        public boolean isTapping() {
            return status == FingerTipStatus.TAPPING;
        }

        public boolean isPressing() {
            return status == FingerTipStatus.PRESSING;
        }
    }

    public static List<Point> getTapping(Mat im, List<Point> fingers) {
        /**
         * @param: im: A YCrCb image
         * @param: fingers: A list of points indicating the position of finger tips
         * @return:
         *      A list of points detected as being tapping
         */
        List<TapDetectPoint> all = getTappingAll(im, fingers);
        List<Point> result = new ArrayList<>();
        for (TapDetectPoint p : all) {
            if (p.isTapping()) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<Point> getPressing(Mat im, List<Point> fingers) {
        /**
         * @param: im: A YCrCb image
         * @param: fingers: A list of points indicating the position of finger tips
         * @return:
         *      A list of points detected as being pressing
         */
        List<TapDetectPoint> all = getTappingAll(im, fingers);
        List<Point> result = new ArrayList<>();
        for (TapDetectPoint p : all) {
            if (p.isPressing()) {
                result.add(p);
            }
        }
        return result;
    }

    public static List<TapDetectPoint> getTappingAll(Mat im, List<Point> fingers) {
        /**
         * @param: im: A YCrCb image
         * @param: fingers: A list of points indicating the position of finger tips
         * @return:
         *  A list of `TapDetectPoint` whose `status` indicating the status of each finger tip point
         */
        List<TapDetectPoint> nextFingers = new ArrayList<>();

        TapDetectPoint nearestPt;
        int nearest_dist;

        for (final Point p : fingers) {

            // find nearest point among all points from last frame
            // it is assumed that if the finger tip is detected in both frame
            // then the former must be the nearest to the latter
            if (lastFingerTips.isEmpty()) {
                nearestPt = null;
                nearest_dist = Config.FINGER_TIP_MOVE_DIST_MAX + 1;  // use the max value as invalid
            } else {
                nearestPt = Collections.min(lastFingerTips,
//                    (TapDetectPoint p1, TapDetectPoint p2) -> Integer.compare(p1.distanceFrom(p), p2.distanceFrom(p))
                        new Comparator<TapDetectPoint>() {
                            @Override
                            public int compare(TapDetectPoint p1, TapDetectPoint p2) {
                                return Integer.compare(p1.distanceFrom(p), p2.distanceFrom(p));
                            }
                        }
                );
                nearest_dist = nearestPt.distanceFrom(p);
            }

            if (nearestPt == null || nearest_dist > Config.FINGER_TIP_MOVE_DIST_MAX) {
                // has no relevant point at last frame
                nextFingers.add(new TapDetectPoint(p, FingerTipStatus.NOT_CARE));

            } else if (nearest_dist < Config.FINGER_TIP_LINGER_DIST_MAX) {
                // has a point at last frame with almost a same position
                lastFingerTips.remove(nearestPt); // can not be matched by other points
                if (nearestPt.isFalling()) {
                    // last frame this is falling, and this frame it lingers
                    // Tap detected !
                    noNeighborAdd(nextFingers, new TapDetectPoint(p, FingerTipStatus.TAPPING));
                    // nextFingers.add(new TapDetectPoint(p, FingerTipStatus.TAPPING));
                } else if (nearestPt.isPressing() || nearestPt.isTapping()) {
                    nextFingers.add(new TapDetectPoint(p, FingerTipStatus.PRESSING));
                } else {
                    nextFingers.add(new TapDetectPoint(p, FingerTipStatus.LINGER));
                }
            } else if (Math.abs(p.x - nearestPt.x) < p.y - nearestPt.y) {
                // has a point at last frame which is above this point and not too far
                nextFingers.add(new TapDetectPoint(p, FingerTipStatus.FALLING));
                lastFingerTips.remove(nearestPt); // can not be matched by other points
            } else {
                nextFingers.add(new TapDetectPoint(p, FingerTipStatus.NOT_CARE));
            }

        }

        // update lastFingerTips
        lastFingerTips.clear();
        for (TapDetectPoint p : nextFingers) {
            lastFingerTips.add(new TapDetectPoint(p));
        }
        return nextFingers;
    }

    private static void noNeighborAdd(List<TapDetectPoint> points, TapDetectPoint toAdd) {
        for (TapDetectPoint p : points) {
            if (p.isTapping() && p.distanceFrom(toAdd) < 7) {
                p.x = (p.x + toAdd.x) * 0.5;
                p.y = (p.y + toAdd.y) * 0.5;
                return;
            }
        }
        points.add(toAdd);
    }

    private static LinkedList<TapDetectPoint> lastFingerTips = new LinkedList<>();  // finger tips of last frame
}
