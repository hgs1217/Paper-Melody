package com.papermelody.core.calibration;

import org.opencv.core.Mat;

import java.io.Serializable;

/**
 * Created by HgS_1217_ on 2017/5/28.
 */

public class TransformResult implements Serializable {

    Mat m;
    double blackWidth;

    TransformResult() {
        blackWidth = 0;
    }
}