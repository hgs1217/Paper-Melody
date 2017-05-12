package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.papermelody.util.CanvasUtil;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by gigaflw on 2017/5/12.
 * Draw on the canvas as you wish
 */

public class CameraDebugView extends View {

    private List<Point> handContours = new ArrayList<>();
    private List<Point> fingerTips = new ArrayList<>();
    private List<Point> tapping = new ArrayList<>();

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

        if (!handContours.isEmpty()) {
            CanvasUtil.drawPoints(canvas, handContours, Color.BLUE);
        }
        if (!fingerTips.isEmpty()) {
            CanvasUtil.drawPoints(canvas, fingerTips, Color.RED);
        }
        if (!tapping.isEmpty()) {
            CanvasUtil.drawPoints(canvas, tapping, Color.GREEN);
        }

    }

    public void updatePoints(List<Point> handContours, List<Point> fingerTips,List<Point> tapping, int height, int width) {
        this.handContours = new ArrayList<>(handContours);
        this.fingerTips = new ArrayList<>(fingerTips);
        this.tapping = new ArrayList<>(tapping);
        CanvasUtil.updateSize(height, width);
        invalidate();
    }

}
