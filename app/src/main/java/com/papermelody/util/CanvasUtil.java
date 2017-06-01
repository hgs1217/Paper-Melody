package com.papermelody.util;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import org.opencv.core.Point;

import java.util.List;

/**
 * Created by gigaflower on 2017/5/12.
 */

public class CanvasUtil {
    private static Paint pointPaint = new Paint();
    private static Paint textPaint = new Paint();

    private static int photoHeight = -1;
    private static int photoWidth = -1;

    private static int surfaceViewHeight = -1;
    private static int surfaceViewWidth = -1;

    private static int screenHeight = -1;

//    private static Context context;

    static {
        pointPaint.setStrokeWidth(10);
        textPaint.setColor(Color.CYAN);
        textPaint.setTextSize(50);
    }

    public static void setSurfaceViewSize(int width, int height) {
        surfaceViewWidth = width;
        surfaceViewHeight = height;
    }

    public static void setPhotoSize(int width, int height) {
        photoWidth = width;
        photoHeight = height;
    }

    public static void setScreenHeight(int height) {
        screenHeight = height;
    }

    public static void drawPoints(Canvas canvas, List<Point> points, int color) {
        pointPaint.setColor(color);

        float heightScalar = (float) surfaceViewHeight / photoHeight;
        float widthScalar =  (float) surfaceViewWidth  / photoWidth;
        double offset = (surfaceViewHeight - screenHeight) / 2.0;

        for (Point pt: points) {
            canvas.drawCircle((photoWidth - (float)pt.x) * widthScalar, (float)(pt.y * heightScalar + offset), 10, pointPaint);
            // subtracted by photoWidth because the point is reversed in X from front camera
        }
    }

    public static void writeText(Canvas canvas, String[] text) {
        int top = 50;
        for (String t: text) {
            canvas.drawText(t, 50, top, textPaint);
            top += 50;
        }
    }
}