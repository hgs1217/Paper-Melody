package com.papermelody.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;

import com.papermelody.util.CanvasUtil;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.List;

import tapdetect.ColorRange;

/**
 * Created by gigaflw on 2017/5/12.
 * Draw on the canvas as you wish
 */

public class CameraDebugView extends View {

    private List<Point> handContours = new ArrayList<>();
    private List<Point> fingerTips = new ArrayList<>();
    private List<Point> falling = new ArrayList<>();
    private List<Point> lingering = new ArrayList<>();
    private List<Point> tapping = new ArrayList<>();
    private long processDelay, cameraInterval, processInterval;

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
            CanvasUtil.drawPoints(canvas, handContours, Color.RED);
        }
//        Log.w("fingers when draw", "" + fingerTips);
        if (!fingerTips.isEmpty()) {
            CanvasUtil.drawPoints(canvas, fingerTips, Color.GRAY);
        }
//        Log.w("tapping when draw", "" + tapping);
        if (!falling.isEmpty()) {
            CanvasUtil.drawPoints(canvas, falling, Color.rgb(0, 160, 0));
        }

        if (!lingering.isEmpty()) {
            CanvasUtil.drawPoints(canvas, lingering, Color.BLUE);
        }

        if (!tapping.isEmpty()) {
            CanvasUtil.drawPoints(canvas, tapping, Color.GREEN);
        }

        String[] to_be_write = {
                "Camera interval: " + cameraInterval + " ms",
                "Process interval: " + processInterval + " ms",
                "Time consumed: " + processDelay + " ms",
                "Hand contour: " + handContours.size() + " pts",
                "Finger tip: " + fingerTips.size() + " pts",
                "Falling: " + falling.size() + " pts",
                "Lingering: " + lingering.size() + " pts",
                "Tapping: " + tapping.size() + " pts",
                "ColorRange: " + ColorRange.getRange()[0],
                "ColorRange: " + ColorRange.getRange()[1],
                "updated: " + ColorRange.getUpdatedCnt()
        };
        CanvasUtil.writeText(canvas, to_be_write);
    }

    public void updateInfo(
            List<Point> handContours,
            List<Point> fingerTips,
            List<Point> falling,
            List<Point> lingering,
            List<Point> tapping,
            long processDelay, long cameraInterval, long processInterval
    ) {
        this.handContours = new ArrayList<>(handContours);
        this.fingerTips = new ArrayList<>(fingerTips);
        this.falling = new ArrayList<>(falling);
        this.lingering = new ArrayList<>(lingering);
        this.tapping = new ArrayList<>(tapping);
        this.processDelay = processDelay;
        this.cameraInterval = cameraInterval;
        this.processInterval = processInterval;

        invalidate();
    }

}
