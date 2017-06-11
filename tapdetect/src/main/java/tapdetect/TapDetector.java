/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-11 11:00:34
*/

package tapdetect;

import java.util.Collections;
import java.util.Comparator;
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
            return Math.abs((int) (point.x - pt.x)) + Math.abs((int) (point.y - pt.y));
        }

        public Point getPoint() { return point; }

        public boolean isFalling() { return status == FingerTipStatus.FALLING;  }
        public boolean isLinger()  { return status == FingerTipStatus.LINGER;  }
        public boolean isTapping() { return status == FingerTipStatus.TAPPING;  }

        public String toString() {
            return "" + point;
        }
    }

    public List<Point> getTapping(Mat im, List<Point> fingers) {
        List<TapDetectPoint> all = getTappingAll(im, fingers);
        List<Point> result = new ArrayList<>();
        for (TapDetectPoint p: all) {
            if (p.status == FingerTipStatus.TAPPING) {
                result.add(p.point);
            }
        }
        return result;
    }

    public List<TapDetectPoint> getTappingAll(Mat im, List<Point> fingers) {
        ArrayList<FingerTipStatus> fingerTipsStatus = new ArrayList<>();

        TapDetectPoint nearest_pt;
        int nearest_dist;

        for (final Point p : fingers) {

            // find nearest point among all points from last frame
            // it is assumed that if the finger tip is detected in both frame
            // then the former must be the nearest to the latter
            if (this.lastFingerTips.isEmpty()) {
                nearest_pt = null;
                nearest_dist = Config.FINGER_TIP_MOVE_DIST_MAX + 1;  // use the max value as invalid
            } else {
                nearest_pt = Collections.min(this.lastFingerTips,
//                    (TapDetectPoint p1, TapDetectPoint p2) -> Integer.compare(p1.distanceFrom(p), p2.distanceFrom(p))
                        new Comparator<TapDetectPoint>() {
                            @Override
                            public int compare(TapDetectPoint p1, TapDetectPoint p2) {
                                return Integer.compare(p1.distanceFrom(p), p2.distanceFrom(p));
                            }
                        }
                );
                nearest_dist = nearest_pt.distanceFrom(p);
            }

            if (nearest_pt == null || nearest_dist > Config.FINGER_TIP_MOVE_DIST_MAX) {
                // has no relevant point at last frame
                fingerTipsStatus.add(FingerTipStatus.NOT_CARE);

            } else if (nearest_dist < Config.FINGER_TIP_LINGER_DIST_MAX) {
                // has a point at last frame with almost a same position
                if (nearest_pt.status == FingerTipStatus.FALLING && p.y > Config.TAP_THRESHOLD_ROW) {
                    // last frame this is falling, and this frame it lingers
                    // Tap detected !
                    fingerTipsStatus.add(FingerTipStatus.TAPPING);
                } else {
                    fingerTipsStatus.add(FingerTipStatus.LINGER);
                }
            } else if (Math.abs(p.x - nearest_pt.point.x) < p.y - nearest_pt.point.y) {
                // has a point at last frame which is above this point and not too far
                fingerTipsStatus.add(FingerTipStatus.FALLING);
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

        // Mat tapping_im = Util.drawPoints(im, tapping, new Scalar(0, 255, 0));
        return result;
    }

    private ArrayList<TapDetectPoint> lastFingerTips = new ArrayList<>();  // finger tips of last frame
}