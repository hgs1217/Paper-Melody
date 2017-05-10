package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by HgS_1217_ on 2017/5/9.
 */

public class CalibrationView extends View {

    private int[] coordinates = new int[8];
    private int photoHeight = 1280;
    private int photoWidth = 960;

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

        if (coordinates.length == 8 && coordinates[0] != 0) {
            double heightScalar = (double)canvas.getHeight() / photoHeight;
            double widthScalar = (double)canvas.getWidth() / photoWidth;
            Log.d("CANVAS", canvas.getHeight()+" "+canvas.getWidth());
            Log.d("CANVASSCA", heightScalar+" "+widthScalar);
            /*int point1X = coordinates[0], point1Y = coordinates[1], point2X = coordinates[2],
                    point2Y = coordinates[3], point3X = coordinates[4], point3Y = coordinates[5],
                    point4X = coordinates[6], point4Y = coordinates[7];*/

            int point1Y = (int) (coordinates[0] * heightScalar), point1X = (int) (coordinates[1] * widthScalar),
                    point2Y = (int) (coordinates[2] * heightScalar), point2X = (int) (coordinates[3] * widthScalar),
                    point3Y = (int) (coordinates[4] * heightScalar), point3X = (int) (coordinates[5] * widthScalar),
                    point4Y = (int) (coordinates[6] * heightScalar), point4X = (int) (coordinates[7] * widthScalar);

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

    public void updateCalibrationCoordinates(int[] coordinates, int height, int width) {
        System.arraycopy(coordinates, 0, this.coordinates, 0, 8);
        photoHeight = height;
        photoWidth = width;
        invalidate();
    }

}
