/*
* @Author: zhouben
* @Date:   2017-05-10 09:15:07
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-30 21:06:58
*/
package tapdetect;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;


public class HandDetector {

    public Mat getHand(Mat im, Mat fgmask) {
        /**
         * @param im: image in YCrCb color space
         * @param fgmask: foreground mask given by `org.opencv.video.BackgroundSubtractor`,
         *              denoting whether or not a pixel is moving
         * this function will not change `im` or `fgmask`
         */
//        if (ColorRange.getUpdatedCnt() < 200) {
            List<Point> movingPixels = new ArrayList<>();
            for (int r = 0; r < fgmask.height(); r += 4) {
                pixelLoop: for (int c = 0; c < fgmask.width(); c += 4) {
                    if (fgmask.get(r, c)[0] == 0) {
                        continue;
                    }

                    // found a moving pixel
                    for (int ch = 0; ch < 3; ++ch) { // channels
                        if (Math.abs(im.get(r, c)[ch] - ColorRange.getCenter()[ch])
                                >= Config.FINGER_COLOR_TOLERANCE[ch]) {
                            continue pixelLoop;
                        }
                    }
                    movingPixels.add(new Point(c, r));
                    if (movingPixels.size() > 1000) {
                        break;
                    }
                }
            }
            if (movingPixels.size() > 10 && movingPixels.size() < 1000) {
                ColorRange.updateRange(im, movingPixels);
            }
//        }
        return this.colorRange(im.clone(), ColorRange.getRange()); // will color range change im?
    }

    @Deprecated
    public Mat getHandOld(Mat im) {
        assert im.size().height == Config.IM_HEIGHT;
        Mat handSm = this.colorRange(im.clone(), Config.FINGER_COLOR_RANGE_SM);
        Mat handLg = this.colorRange(im.clone(), Config.FINGER_COLOR_RANGE_LG);

        ImgLogger.debug("04_small_range.jpg", handSm);
        ImgLogger.debug("05_large_range.jpg", handLg);

        List<MatOfPoint> contoursSm = Util.largeContours(handSm, Config.HAND_AREA_MIN);

        Point[] seeds = new Point[contoursSm.size()];

        for (int i = 0; i < contoursSm.size(); ++i) {
            Moments m = Imgproc.moments(contoursSm.get(i));
            int col = (int) (m.get_m10() / m.get_m00());
            int row = (int) (m.get_m01() / m.get_m00());
            seeds[i] = new Point(col, row);
        }
        // [(col1, row1), ...] center of each contour

        List<MatOfPoint> contours_lg = Util.largeContours(handLg, Config.HAND_AREA_MIN);

        Mat hand = Util.fillContours(handLg.size(), contours_lg, seeds);

        ImgLogger.info("06_hand.jpg", hand);
        return hand;
    }

    private Mat colorRange(Mat im, Scalar[] colorRange) {
        // 1. Mask by color
        Mat mask = new Mat(im.size(), CvType.CV_8U);
        Core.inRange(im, colorRange[0], colorRange[1], mask);
        ImgLogger.debug("01_color_range.jpg", mask);

        // 2. Ignore face
        // Ignore first 1/3 in height to ignore face
        // We assume fingers only appear at between 1/3 and bottom
        mask.submat(new Range(0, (int) mask.size().height / 3), new Range(0, (int) mask.size().width)).setTo(Util.SCALAR_BLACK);

        // 3. remove noise
        // Morphology Open
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, Mat.ones(3, 3, CvType.CV_8U));
        Imgproc.dilate(mask, mask, Mat.ones(3, 3, CvType.CV_8U));
        ImgLogger.debug("02_morpho_open.jpg", mask);

        return mask;
    }
}