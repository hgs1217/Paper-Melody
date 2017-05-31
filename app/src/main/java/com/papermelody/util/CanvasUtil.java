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
    private static int photoHeight = 1920;
    private static int photoWidth = 1080;
    private static Context context;
    private static int surHeight = 960;

    static {
        pointPaint.setStrokeWidth(10);
        textPaint.setColor(Color.CYAN);
        textPaint.setTextSize(50);
    }

    public static void updateSize(int height, int width, Context c, int surViewHeight) {
        photoHeight = height;
        photoWidth = width;
        context = c;
        surHeight = surViewHeight;
    }

    public static void drawPoints(Canvas canvas, List<Point> points, int color) {
        pointPaint.setColor(color);

        // FIXME: inefficient to update upon every image
        // these constants should be calced at the very beginning of `initCamera`
        float heightScalar = (float) canvas.getHeight() / photoHeight;
        float widthScalar =  (float) canvas.getWidth() / photoWidth;
        int screenHeight = ViewUtil.getScreenHeight(context);
        double offset = (surHeight - screenHeight) / 2.0;

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