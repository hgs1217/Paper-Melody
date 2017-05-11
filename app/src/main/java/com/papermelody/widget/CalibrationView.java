package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.papermelody.core.calibration.Calibration;

/**
 * Created by HgS_1217_ on 2017/5/9.
 */

public class CalibrationView extends View {

    private int photoHeight = 1280;
    private int photoWidth = 960;
    private int leftLowX = 0;
    private int leftLowY = 0;
    private int leftUpX = 0;
    private int leftUpY = 0;
    private int rightLowX = 0;
    private int rightLowY = 0;
    private int rightUpX = 0;
    private int rightUpY = 0;

    public CalibrationView(Context c) {
        super(c);
    }

    public CalibrationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CalibrationView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (leftLowX != 0) {
            double heightScalar = (double)canvas.getHeight() / photoHeight;
            double widthScalar = (double)canvas.getWidth() / photoWidth;
            Log.d("CANVAS", canvas.getHeight()+" "+canvas.getWidth());
            Log.d("CANVASSCA", heightScalar+" "+widthScalar);

            int point1X = (int) (leftUpX * widthScalar), point1Y = (int) (leftUpY * heightScalar),
                    point2X = (int) (leftLowX * widthScalar), point2Y = (int) (leftLowY * heightScalar),
                    point3X = (int) (rightUpX * widthScalar), point3Y = (int) (rightUpY * heightScalar),
                    point4X = (int) (rightLowX * widthScalar), point4Y = (int) (rightLowY * heightScalar);

            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStrokeWidth(6);
            canvas.drawLine(point1X, point1Y, point2X, point2Y, p);
            canvas.drawLine(point1X, point1Y, point3X, point3Y, p);
            canvas.drawLine(point2X, point2Y, point4X, point4Y, p);
            canvas.drawLine(point3X, point3Y, point4X, point4Y, p);
            /*try {

            } catch (Exception e) {
                e.printStackTrace();
            }*/
        }
    }

    public void updateCalibrationCoordinates(Calibration.CalibrationResult calibrationResult, int height, int width) {
        leftLowX = calibrationResult.getLeftLowX();
        leftLowY = calibrationResult.getLeftLowY();
        leftUpX = calibrationResult.getLeftUpX();
        leftUpY = calibrationResult.getLeftUpY();
        rightLowX = calibrationResult.getRightLowX();
        rightLowY = calibrationResult.getRightLowY();
        rightUpX = calibrationResult.getRightUpX();
        rightUpY = calibrationResult.getRightUpY();
        photoHeight = height;
        photoWidth = width;
        invalidate();
    }

}
