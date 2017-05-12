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
    private static Paint p = new Paint();
    private static int photoHeight = 1920;
    private static int photoWidth = 1080;

    public static void updateSize(int height, int width) {
        photoHeight = height;
        photoWidth = width;
    }

    public static void drawPoints(Canvas canvas, List<Point> points, int color) {
        p.setColor(color);
        p.setStrokeWidth(10);

        float heightScalar = (float) canvas.getHeight() / photoHeight;
        float widthScalar =  (float) canvas.getWidth() / photoWidth;

        for (Point pt: points) {
            canvas.drawCircle((photoWidth - (float)pt.x) * widthScalar , (float)pt.y * heightScalar, 10, p);
            // subtracted by photoWidth because the point is reversed in X from front camera
        }
    }
}