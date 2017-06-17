/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-06-15 10:58:43
*/

package tapdetect;

import android.util.Log;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

public class Sampler {
    // apexes defining a polygon in hand shape
    // on on a canvas shaped `sampleWindowWidth `, `sampleWindowHeight`
    // however the image size we will process will be a different one
    // in which case, we will put this canvas in the center of the image.
    static int sampleWindowWidth = 245;
    static int sampleWindowHeight = 147;
    // We need guarantee the center of the canvas lies inside the contour
    // otherwise, opencv flood-fill algorithm will fail to convert the apexes to a bi-image
    private static int sampleWindowApex[][] = {  // {col, row}
            {41, 82}, {38, 85}, {39, 96}, {68, 96}, {68, 109},
            {69, 110}, {72, 111}, {86, 111}, {89, 110}, {106, 81},
            {106, 109}, {113, 115}, {128, 115}, {132, 111}, {136, 83},
            {145, 109}, {148, 112}, {165, 112}, {168, 109}, {169, 92},
            {184, 104}, {185, 105}, {197, 105}, {198, 103}, {198, 96},
            {188, 82}, {179, 73}, {165, 62}, {152, 38}, {149, 35},
            {108, 35}, {103, 38}, {69, 72}
    };

    // mask used to saved the mask, this Mat will have same size will the image will process
    // in tap detection
    private static Mat sampleMask;
    private static boolean isStable = false;

    // contain same points with sampleWindowApex except for
    // they have offsets added so that it shares a same coordinate with
    // tap-detection algorithm
    private static List<Point> sampleWindowContour = new ArrayList<>();
    private static List<Point> samplePixels = new ArrayList<>();
    static int rowOffset = 0;
    static int colOffset = 0;

    static {
        for (int[] samplePt : sampleWindowApex) {
            sampleWindowContour.add(new Point(samplePt[0], samplePt[1]));
        }
    }

    public static boolean isInited() {
        return sampleMask != null;
    }

    public static void initSampleMask(int height, int width) {
        /**
         * Init sample mask according to `height`, `width`
         */

        // convert apexes to a binary image
        Mat sampleSrc = Mat.zeros(new Size(sampleWindowWidth, sampleWindowHeight), CvType.CV_8UC1);
        Util.fillContour(sampleSrc, sampleWindowContour, new Point(sampleWindowWidth / 2, sampleWindowHeight / 2));

        sampleMask = Mat.zeros(new Size(width, height), CvType.CV_8UC1);

        if (height < sampleWindowHeight || width < sampleWindowWidth) {
            throw new AssertionError(
                    "Too small the image(" + width + " cols x" + height +
                            "rows) is to contain a sampling window sized"
                            + sampleWindowWidth + "x" + sampleWindowHeight
            );
        }

        rowOffset = (height - sampleSrc.height()) / 2;
        colOffset = (width - sampleSrc.width()) / 2;

        sampleSrc.copyTo(sampleMask.submat(
                rowOffset, sampleSrc.height() + rowOffset,
                colOffset, sampleSrc.width() + colOffset
        ));


        for (int r = 0; r < height; r += 4) {
            for (int c = 0; c < width; c += 4) {
                if (sampleMask.get(r, c)[0] > 0) {
                    samplePixels.add(new Point(c, r));
                }
            }
        }

        // add points in sampleWindowContour for further use
        // now sampleWindowContour shares a same coordinate with
        // tap-detection algorithm
        for (Point p : sampleWindowContour) {
            p.x += colOffset;
            p.y += rowOffset;
        }
    }

    public static void sample(Mat im) {
        /**
         * Can't be used if `isInited() == false`
         * According to the `sampleMask`, retrieve the pixels in mat and update the color range
         */
        if (!isInited()) {
            return;
        }
        List<Point> pixelsToUpdate = new ArrayList<>();

        pixelLoop:
        for (Point p : samplePixels) {
            for (int ch = 0; ch < 3; ++ch) { // channels
                if (Math.abs(im.get((int) p.y, (int) p.x)[ch] - Config.FINGER_COLOR[ch])
                        >= Config.FINGER_COLOR_TOLERANCE[ch]) {
                    continue pixelLoop;
                }
            }
            pixelsToUpdate.add(p);
        }

        // Log.w("near ratio", "" + Math.round(pixelsToUpdate.size() / (double) samplePixels.size() * 100));
        if (pixelsToUpdate.size() < samplePixels.size() * Config.SAMPLE_PASS_THRESHOLD) {
            ColorRange.reset();
        } else {
            ColorRange.updateRange(im, pixelsToUpdate);
            if (ColorRange.isStable()) {
                Log.w("sampler", "stable!");
            }
        }
    }

    public static boolean sampleCompleted() {
        return ColorRange.isStable();
    }

    public static List<Point> getSampleWindowContour() {
        /**
         * For debug use in case you want to draw the contour
         */
        return sampleWindowContour;
    }
}
