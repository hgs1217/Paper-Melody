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
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.core.Range;
import org.opencv.core.CvType;
import org.opencv.imgproc.Imgproc;


public class HandDetector {

    public Mat getHand(Mat im, Mat fgmask) {
        /**
         * @param im: image in YCrCb color space
         * @param fgmask: foreground mask given by `org.opencv.video.BackgroundSubtractor`,
         *              denoting whether or not a pixel is moving
         * will adjust color range according to `fgmask`
         * this function will not change `im` or `fgmask`
         * @return: a binary image will white pixels are in range
         */
//        if (ColorRange.getUpdatedCnt() < 50) {
//            List<Point> movingPixels = new ArrayList<>();
//            for (int r = 0; r < fgmask.height(); r += 4) {
//                pixelLoop:
//                for (int c = 0; c < fgmask.width(); c += 4) {
//                    if (fgmask.get(r, c)[0] == 0) {
//                        continue;
//                    }
//
//                    // found a moving pixel
//                    for (int ch = 0; ch < 3; ++ch) { // channels
//                        if (Math.abs(im.get(r, c)[ch] - ColorRange.getCenter()[ch])
//                                >= Config.FINGER_COLOR_TOLERANCE[ch]) {
//                            continue pixelLoop;
//                        }
//                    }
//                    movingPixels.add(new Point(c, r));
//                    if (movingPixels.size() > 1000) {
//                        break;
//                    }
//                }
//            }
//            if (movingPixels.size() > 10) {
//                ColorRange.updateRange(im, movingPixels);
//            }
//        }
        return this.colorRange(im.clone(), ColorRange.getRange()); // will color range change im?
    }


    private Mat colorRange(Mat im, Scalar[] colorRange) {
        /**
         *  Get coarse area of hand according to colorRange
         *  @param: colorRange:
         *      [
         *        [lower_bound_channel_0, lower_bound_channel_1, lower_bound_channel_2, ...],
         *        [upper_bound_channel_0, upper_bound_channel_1, upper_bound_channel_2, ...]
         *      ]
         *  will not change `im` or `colorRange`
         *
         *  @return: a binary image will white pixels are in range
         */

        // 1. Mask by color
        Mat mask = new Mat();
        Core.inRange(im, colorRange[0], colorRange[1], mask);
        // ImgLogger.debug("01_color_range.jpg", mask);

        // 2. Ignore face
        // Ignore first 1/3 in height to ignore face
        // We assume fingers only appear at between 1/3 and bottom
        mask.submat(new Range(0, (int) mask.size().height / 3), new Range(0, (int) mask.size().width)).setTo(Util.SCALAR_BLACK);

        // 3. remove noise
        // Morphology Open
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, Mat.ones(3, 3, CvType.CV_8U));
        Imgproc.dilate(mask, mask, Mat.ones(3, 3, CvType.CV_8U));
        // ImgLogger.debug("02_morpho_open.jpg", mask);

        return mask;
    }
}