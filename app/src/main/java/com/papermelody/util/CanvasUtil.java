package com.papermelody.util;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

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

    private static float getHeightScalar() {
        return (float) surfaceViewHeight / photoHeight;
    }

    private static float getWidthScalar() {
        return (float) surfaceViewWidth / photoWidth;
    }

    private static double getHeightOffset() {
        return (surfaceViewHeight - screenHeight) / 2.0;
    }

    public static void drawPoint(Canvas canvas, Point pt, int color) {
        pointPaint.setColor(color);

        canvas.drawCircle(transformX(pt.x), transformY(pt.y), 10, pointPaint);
    }

    public static void drawPoints(Canvas canvas, List<Point> points, int color) {
        pointPaint.setColor(color);

        for (Point pt : points) {
            canvas.drawCircle(transformX(pt.x), transformY(pt.y), 10, pointPaint);
            // subtracted by photoWidth because the point is reversed in X from front camera
        }
    }

    public static void drawContour(Canvas canvas, List<Point> contour, int color) {
        pointPaint.setColor(color);

        int len = contour.size();
        for (int i = 0; i < len; ++i) {
            int next_i = (i + 1) % len;
            Point p = contour.get(i), next = contour.get(next_i);

            canvas.drawLine(
                    transformX(p.x), transformY(p.y), transformX(next.x), transformY(next.y),
                    pointPaint);
        }
    }

    public static void drawContours(Canvas canvas, List<List<Point>> contours, int color) {
        pointPaint.setColor(color);

        for (List<Point> contour : contours) {
            int len = contour.size();
            for (int i = 0; i < len; ++i) {
                int next_i = (i + 1) % len;
                Point p = contour.get(i), next = contour.get(next_i);

                canvas.drawLine(
                        transformX(p.x), transformY(p.y), transformX(next.x), transformY(next.y),
                        pointPaint);
            }
        }
    }

    public static float transformX(double x) {
        return (photoWidth - (float) x) * getWidthScalar();
    }

    public static float transformY(double y) {
        return (float) (y * getHeightScalar() - getHeightOffset());
    }

    public static void writeText(Canvas canvas, String[] text) {
        int top = 50;
        for (String t : text) {
            canvas.drawText(t, 50, top, textPaint);
            top += 50;
        }
    }
}