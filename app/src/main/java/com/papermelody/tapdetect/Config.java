/*
* @Author: zhouben
* @Date:   2017-05-10 14:37:27
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-10 22:03:37
*/

package com.papermelody.tapdetect;

import org.opencv.core.Scalar;

public class Config {
    public static Scalar[] FINGER_COLOR_RANGE_SM = {new Scalar(0, 150, 100), new Scalar(255, 160, 130)};
    public static Scalar[] FINGER_COLOR_RANGE_LG = {new Scalar(0, 145, 100), new Scalar(255, 165, 140)};

    public static int IM_HEIGHT = 250;
    public static int TAP_THRESHOLD_ROW = 160;
    public static int FINGER_TIP_STEP = 10;
    public static int FINGER_TIP_WIDTH = 15;
}
