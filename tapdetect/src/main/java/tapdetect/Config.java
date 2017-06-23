/*
* @Author: zhouben
* @Date:   2017-05-10 14:37:27
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-30 21:12:18
*/

package tapdetect;

public class Config {
    public static int IM_HEIGHT = 250;
    public static int HAND_AREA_MIN = 300;
    public static int FINGER_TIP_WIDTH = 15;

    // max distance the finger tip could move between 2 frames
    public static int FINGER_TIP_MOVE_DIST_MAX = 25;
    // max distance the finger tip could move if to judge the point as lingering
    public static int FINGER_TIP_LINGER_DIST_MAX = 2;

    public static int IM_BLUR_SIZE = 10;

    // min interval between 2 frame to avoid to slow the moving speed
    public static int PROCESS_INTERVAL_MS = 50;
    public static double[][] FINGER_COLOR_RANGE = {{0, 135, 100}, {255, 142, 130}};

    // Sampling
    /////////////////////
    // A pixel is judged `maybe hand` if its color is between
    // FINGER_COLOR ± FINGER_COLOR_TOLERANCE
    // in YCrCb color space
    public static double[] FINGER_COLOR = {128, 150, 100};
    public static double[] FINGER_COLOR_TOLERANCE = {128, 17, 20};

    // One sample is successful if at least
    // `SAMPLE_PASS_THRESHOLD` of the total pixels
    // are judged `maybe hand`
    public static double SAMPLE_PASS_THRESHOLD = 0.8;

    // range[i] = aver(<Sampling pixels>) + std(<Sampling pixels>) * COLOR_RANGE_EXPAND[i[
    public static double[] COLOR_RANGE_EXPAND = {3, 1.6, 3};
    // sampling is not stable until the last `SAMPLE_STABLE_CNT` frames
    // have approximately same values
    public static int SAMPLE_STABLE_CNT = 5;
}
