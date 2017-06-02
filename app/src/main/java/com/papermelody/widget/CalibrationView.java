package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.papermelody.core.calibration.CalibrationResult;
import com.papermelody.util.ViewUtil;

/**
 * Created by HgS_1217_ on 2017/5/9.
 */

public class CalibrationView extends View {

    private int photoHeight = 480;
    private int photoWidth = 640;
    private int leftLowX = 0;
    private int leftLowY = 0;
    private int leftUpX = 0;
    private int leftUpY = 0;
    private int rightLowX = 0;
    private int rightLowY = 0;
    private int rightUpX = 0;
    private int rightUpY = 0;
    private int height = 960;
    private int width = 1280;
    private boolean flag = false;
    private boolean success = false;
    private Context context = null;

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

        if (context != null && flag) {
            double heightScalar = (double) height / photoHeight;
            double widthScalar = (double) width / photoWidth;
            int screenHeight = ViewUtil.getScreenHeight(context);
            double offset = (height - screenHeight) / 2.0;
//            Log.d("CANVAS", height+" "+canvas.getWidth());
//
//            Log.d("CANVAS1", height+" "+photoHeight+" "+width+" "+photoWidth);
//            Log.d("CANVAS2", height+" "+screenHeight+" "+offset);

            if (!success) {
                // 标定过程中图片和显示的视频左右相反，应当对坐标进行左右对称取反的操作
                leftUpX = photoWidth - leftUpX;
                leftLowX = photoWidth - leftLowX;
                rightUpX = photoWidth - rightUpX;
                rightLowX = photoWidth - rightLowX;
            }

            int point1X =  (int) (leftUpX * widthScalar), point1Y = (int) (leftUpY * heightScalar - offset),
                    point2X = (int) (leftLowX * widthScalar), point2Y = (int) (leftLowY * heightScalar - offset),
                    point3X = (int) (rightUpX * widthScalar), point3Y = (int) (rightUpY * heightScalar - offset),
                    point4X = (int) (rightLowX * widthScalar), point4Y = (int) (rightLowY * heightScalar - offset);

            if (success) {
                Log.d("TESThistres2",point1X+"");
                Log.d("TESThistres2",point1Y+"");
                Log.d("TESThistres2",point2X+"");
                Log.d("TESThistres2",point2Y+"");
                Log.d("TESThistres2",point3X+"");
                Log.d("TESThistres2",point3Y+"");
                Log.d("TESThistres2",point4X+"");
                Log.d("TESThistres2",point4Y+"");
            }

            Paint p = new Paint();
            p.setColor(Color.RED);
            p.setStrokeWidth(6);
            canvas.drawLine(point1X, point1Y, point2X, point2Y, p);
            canvas.drawLine(point1X, point1Y, point3X, point3Y, p);
            canvas.drawLine(point2X, point2Y, point4X, point4Y, p);
            canvas.drawLine(point3X, point3Y, point4X, point4Y, p);
        }
    }

    public void setSize(int width, int height) {
        this.height = height;
        this.width = width;
    }

    public void setPhotoSize(int width, int height) {
        photoWidth = width;
        photoHeight = height;
    }

    public void updateCalibrationCoordinates(CalibrationResult calibrationResult, Context context, boolean success) {
        leftLowX = calibrationResult.getLeftLowX();
        leftLowY = calibrationResult.getLeftLowY();
        leftUpX = calibrationResult.getLeftUpX();
        leftUpY = calibrationResult.getLeftUpY();
        rightLowX = calibrationResult.getRightLowX();
        rightLowY = calibrationResult.getRightLowY();
        rightUpX = calibrationResult.getRightUpX();
        rightUpY = calibrationResult.getRightUpY();
        flag = calibrationResult.isFlag();

        this.success = success;
        this.context = context;
        invalidate();
    }

}
