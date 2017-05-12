package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by HgS_1217_ on 2017/5/9.
 */

public class CameraDebugView extends View {

    private List<Point> fingerTips = new ArrayList<>();
    private Paint p = new Paint();
    private int photoHeight = 1920;
    private int photoWidth = 1080;

    public CameraDebugView(Context c) {
        super(c);
    }

    public CameraDebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraDebugView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Log.w("fingers", "" + fingerTips);

        float heightScalar = (float) canvas.getHeight() / photoHeight;
        float widthScalar =  (float) canvas.getWidth() / photoWidth;

        if (!fingerTips.isEmpty()) {
            p.setColor(Color.RED);
            p.setStrokeWidth(10);

            for (Point pt: fingerTips) {
                canvas.drawCircle((float)pt.x * widthScalar , (float)pt.y * heightScalar, 10, p);
            }
        }
    }

    public void updateFingerTips(List<Point> fingerTips, int height, int width) {
        this.fingerTips = new ArrayList<>(fingerTips);
        photoHeight = height;
        photoWidth = width;
        invalidate();
    }

}
