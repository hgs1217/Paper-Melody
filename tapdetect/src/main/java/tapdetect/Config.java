/*
* @Author: zhouben
* @Date:   2017-05-10 14:37:27
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-30 21:12:18
*/

package tapdetect;

import org.opencv.core.Scalar;

public class Config {
    public static Scalar[] FINGER_COLOR_RANGE_SM = {new Scalar(0, 150, 100), new Scalar(255, 160, 130)};
    public static Scalar[] FINGER_COLOR_RANGE_LG = {new Scalar(0, 145, 100), new Scalar(255, 165, 140)};

    public static int IM_HEIGHT = 500;
    public static int TAP_THRESHOLD_ROW = 320;
    public static int HAND_AREA_MIN = 1200;
    public static int FINGER_TIP_STEP = 10;
    public static int FINGER_TIP_WIDTH = 30;
}
