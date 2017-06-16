/*
* @Author: zhouben
* @Date:   2017-05-10 09:15:07
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-30 21:06:58
*/
package tapdetect;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;
import org.opencv.video.BackgroundSubtractor;
import org.opencv.video.Video;

@Deprecated
public class ForegroundDetector {

    private final BackgroundSubtractor bs = Video.createBackgroundSubtractorMOG2();
    private Mat fgmask = new Mat();

    public Mat getForeground(Mat im) {
        /**
         * @param im: image in YCrCb color space
         * @return foreground mask given by `org.opencv.video.BackgroundSubtractor`,
         *              denoting whether or not a pixel is moving
         * this function will not change `im`
         */
        bs.apply(im, fgmask);

        Imgproc.morphologyEx(fgmask, fgmask, Imgproc.MORPH_OPEN, Mat.ones(3, 3, CvType.CV_8U));
        Imgproc.threshold(fgmask, fgmask, Config.FINGER_FG_THRESHOLD, 255, Imgproc.THRESH_BINARY);

        return fgmask;
    }

}