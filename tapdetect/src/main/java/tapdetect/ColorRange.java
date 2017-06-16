package tapdetect;

import android.util.Log;

import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class ColorRange {
    private static double[][] range =
            {Config.FINGER_COLOR_RANGE[0].clone(), Config.FINGER_COLOR_RANGE[1].clone()};
    private static Queue<double[]> history = new LinkedList<>();

    public static void reset() {
        range[0] = Config.FINGER_COLOR_RANGE[0].clone();
        range[1] = Config.FINGER_COLOR_RANGE[1].clone();
        history.clear();
    }

    public static boolean isStable() {
        if (history.size() < Config.SAMPLE_STABLE_CNT) {
            return false;
        }

        double[] max = {0, 0, 0}, min = {256, 256, 256};
        for (double[] val : history) {
            for (int i = 0; i < 3; ++i) {
                max[i] = Math.max(val[i], max[i]);
                min[i] = Math.min(val[i], min[i]);
            }
        }

        // for debug
        // double[] diff = new double[3];
        // for (int i = 0; i < 3; ++i) {
        //    diff[i] = max[i] - min[i];
        // }
        // Log.w("colorange diff", Arrays.toString(diff));

        double stableThreshold = 0.5;
        for (int i = 1; i < 3; ++i) {
            // do not check Y channel because it changes much more than Cr or Cb
            if (max[i] - min[i] > stableThreshold) {
                return false;
            }
        }
        return true;
    }

    public static Scalar[] getRange() {
        double[][] ret = new double[2][3];
        for (int i = 0; i < 2; ++i) {
            for (int j = 0; j < 3; ++j) {
                // ret[i][j] = Math.round(range[i][j]);
                ret[i][j] = Math.round(range[i][j] * 100.0) / 100.0;
            }
        }
        return new Scalar[]{new Scalar(ret[0]), new Scalar(ret[1])};
    }

    public static void updateRange(Mat im, List<Point> movingPixels) {
        if (movingPixels.isEmpty()) {
            return;
        }
        double[] aver = {0, 0, 0};
        double[] std = {0, 0, 0};

        // calc stat values
        int n = movingPixels.size();
        for (Point point : movingPixels) {
            double[] val = im.get((int) point.y, (int) point.x);
            for (int i = 0; i < 3; ++i) {
                aver[i] += val[i];
                std[i] += val[i] * val[i];
            }
        }
        for (int i = 0; i < 3; ++i) {
            aver[i] /= n;
            std[i] = Math.sqrt(std[i] / n - aver[i] * aver[i]);
        }

         std[0] *= Config.COLOR_RANGE_EXPAND[0];
         std[1] *= Config.COLOR_RANGE_EXPAND[1];
         std[2] *= Config.COLOR_RANGE_EXPAND[2];  // YCrCb, Y channel has a larger range

        // calc new color range
        double[][] newRange = new double[2][3];
        for (int i = 0; i < 3; ++i) {
            newRange[0][i] = Math.max(aver[i] - std[i], 0);
            newRange[1][i] = Math.min(aver[i] + std[i], 255.0);

            // range[0][i] += (newRange[0][i] - range[0][i]) / updatedCnt;
            // range[1][i] += (newRange[1][i] - range[1][i]) / updatedCnt;

            range[0][i] = newRange[0][i];
            range[1][i] = newRange[1][i];
        }

        if (history.size() == Config.SAMPLE_STABLE_CNT) {
            history.remove();
        }
        history.add(aver);
    }
}
