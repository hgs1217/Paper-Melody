/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-11 11:00:34
*/

package tapdetect;

import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.ArrayList;

import org.opencv.core.Point;
import org.opencv.core.Mat;

public class TapDetector {
    enum FingerTipStatus {
        NOT_CARE, FALLING, LINGER, TAPPING
    }

    public static class TapDetectPoint {
        Point point = new Point();
        FingerTipStatus status;

        TapDetectPoint(Point point, FingerTipStatus status) {
            this.point.x = point.x;
            this.point.y = point.y;
            this.status = status;
        }

        int distanceFrom(Point pt) {
            return Math.abs((int) (point.x - pt.x)) / 2 + Math.abs((int) (point.y - pt.y));
        }

        public Point getPoint() { return point; }

        public boolean isFalling() { return status == FingerTipStatus.FALLING;  }
        public boolean isLingering()  { return status == FingerTipStatus.LINGER;  }
        public boolean isTapping() { return status == FingerTipStatus.TAPPING;  }

        public String toString() {
            return "" + point;
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
        for (TapDetectPoint p: all) {
            if (p.status == FingerTipStatus.TAPPING) {
                result.add(p.point);
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
        ArrayList<FingerTipStatus> fingerTipsStatus = new ArrayList<>();

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
                fingerTipsStatus.add(FingerTipStatus.NOT_CARE);

            } else if (nearest_dist < Config.FINGER_TIP_LINGER_DIST_MAX) {
                // has a point at last frame with almost a same position
                lastFingerTips.remove(nearestPt); // can not be matched by other points
                if (nearestPt.status == FingerTipStatus.FALLING && p.y > Config.TAP_THRESHOLD_ROW) {
                    // last frame this is falling, and this frame it lingers
                    // Tap detected !
                    fingerTipsStatus.add(FingerTipStatus.TAPPING);
                } else {
                    fingerTipsStatus.add(FingerTipStatus.LINGER);
                }
            } else if (Math.abs(p.x - nearestPt.point.x) < p.y - nearestPt.point.y) {
                // has a point at last frame which is above this point and not too far
                fingerTipsStatus.add(FingerTipStatus.FALLING);
                lastFingerTips.remove(nearestPt); // can not be matched by other points
            } else {
                fingerTipsStatus.add(FingerTipStatus.NOT_CARE);
            }

        }

        // update lastFingerTips
        List<TapDetectPoint> result = new ArrayList<>();
        lastFingerTips.clear();
        for (int i=0; i<fingers.size(); ++i) {
            lastFingerTips.add(new TapDetectPoint(fingers.get(i), fingerTipsStatus.get(i)));
            result.add(new TapDetectPoint(fingers.get(i), fingerTipsStatus.get(i)));
        }

        return result;
    }

    private static LinkedList<TapDetectPoint> lastFingerTips = new LinkedList<>();  // finger tips of last frame
}