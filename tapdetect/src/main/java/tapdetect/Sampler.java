/*
* @Author: zhouben
* @Date:   2017-05-10 22:47:18
* @Last Modified by:   zhouben
* @Last Modified time: 2017-06-15 10:58:43
*/

package tapdetect;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Size;

import java.util.ArrayList;
import java.util.List;

public class Sampler {
    private static Mat sampleMask;
    private static int sampleContour[][] = {
            {32, 81}, {32, 95}, {57, 102}, {60, 111}, {94, 111},
            {96, 95}, {103, 103}, {105, 94}, {102, 111}, {110, 115},
            {104, 116}, {113, 113}, {120, 121}, {142, 111}, {139, 98},
            {152, 113}, {168, 105}, {174, 111}, {180, 100}, {207, 102},
            {207, 89}, {193, 88}, {187, 72}, {184, 81}, {178, 65},
            {168, 65}, {175, 57}, {162, 54}, {151, 32}, {99, 34},
            {89, 52}, {75, 49}, {77, 62}, {72, 55}, {67, 72}
    };
    private static int sampleSrcWidth = 245;
    private static int sampleSrcHeight = 147;

    public static boolean isInited() {
        return sampleMask != null;
    }

    public static void initSampleMask(int height, int width) {
        /**
         * Init sample mask according to `height`, `width`
         */
        Mat sampleSrc = Mat.zeros(new Size(sampleSrcWidth, sampleSrcHeight), CvType.CV_8UC1);
        List<Point> samplePoints = new ArrayList<>();
        for (int[] samplePt: sampleContour) {
            samplePoints.add(new Point(samplePt[0], samplePt[1]));
        }
        Util.fillContour(sampleSrc, samplePoints, new Point(sampleSrcWidth/2, sampleSrcHeight/2));

        sampleMask = Mat.zeros(new Size(width, height), CvType.CV_8UC1);

        int row_start = (height - sampleSrc.height()) / 2;
        int col_start = (width - sampleSrc.width()) / 2;

        sampleSrc.copyTo(sampleMask.submat(
                row_start, sampleSrc.height() + row_start,
                col_start, sampleSrc.width() + col_start
        ));
    }

    public static void sample(Mat mat) {
        /**
         * Can't be used if `isInited() == false`
         * According to the `sampleMask`, retrieve the pixels in mat and update the color range
         */
        if (!isInited()) { return; }

        List<Point> pixels = new ArrayList<>();

        for (int r = 0; r < mat.height(); r += 4) {
            for (int c = 0; c < mat.width(); c += 4) {
                if (sampleMask.get(r, c)[0] == 0) {
                    continue;
                }
                pixels.add(new Point(c, r));
            }
        }
        ColorRange.updateRange(mat, pixels);
    }

    public static boolean sampleCompleted() {
        return false;
    }
}
