/*
* @Author: zhouben
* @Date:   2017-05-10 09:16:34
* @Last Modified by:   zhouben
* @Last Modified time: 2017-05-11 11:01:03
*/
package com.papermelody.tapdetect;

import java.lang.RuntimeException;

import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.core.Mat;

/**
 * Logger to record the immediate/final result of image processing
 * <br> Usage:
 * <code>
 * <br>  ImgLogger.setLevel(ImgLogger.LOG_LEVEL_DEBUG);   // save all image with importance above 'debug' level
 * <br>  ImgLogger.info(filename, image);   // save the image with 'info' importance level
 * </code>
 */

public class ImgLogger {
    public static final int LOG_LEVEL_DEBUG = 0;
    public static final int LOG_LEVEL_INFO = 1;
    public static final int LOG_LEVEL_SILENT = 10;
    public static String baseDir = "";

    private static int logLevel = LOG_LEVEL_SILENT;

    public static void setLevel(String level) {
        switch (level) {
            case "debug":
                logLevel = LOG_LEVEL_DEBUG;
                break;
            case "info":
                logLevel = LOG_LEVEL_INFO;
                break;
            case "silent":
                logLevel = LOG_LEVEL_SILENT;
                break;
            default:
                throw new RuntimeException("Unknown log level '" + level + "'");
        }
    }

    public static void setLevel(int level) {
        logLevel = level;
    }

    public static void silent() {
        logLevel = LOG_LEVEL_SILENT;
    }

    private static void log(String filename, Mat im, int level) {
        filename = baseDir + '/' + filename;
        if (level >= logLevel) {
            Imgcodecs.imwrite(filename, im);
        }
    }

    public static void debug(String filename, Mat im) {
        log(filename, im, LOG_LEVEL_DEBUG);
    }

    public static void info(String filename, Mat im) {
        log(filename, im, LOG_LEVEL_INFO);
    }

    public static void show(String filename, Mat im) {
        log(filename, im, LOG_LEVEL_SILENT - 1);
    }

    public static void setBaseDir(String baseDir) {
        ImgLogger.baseDir = baseDir;
    }
}
