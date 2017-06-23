package com.papermelody.widget;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.papermelody.util.CanvasUtil;

import org.opencv.core.Point;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import tapdetect.ColorRange;
import tapdetect.Config;
import tapdetect.Sampler;
import tapdetect.TapDetector.TapDetectPoint;
import tapdetect.facade.Tap;

/**
 * Created by gigaflw on 2017/5/12.
 * Draw on the canvas as you wish
 */

// FIXME: edit this, no debug any more
public class CameraDebugView extends View {

    private List<List<Point>> handContours = new ArrayList<>();
    private List<TapDetectPoint> fingerTips = new ArrayList<>();
    private long processDelay, cameraInterval, processInterval;
    private boolean showTextInfo = true, showContourInfo = true, showPointInfo = true;

    public CameraDebugView(Context c) {
        super(c);
    }

    public CameraDebugView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CameraDebugView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public List<TapDetectPoint> getFingerTips() {
        return fingerTips;
    }

    public List<List<Point>> getHandContours() {
        return handContours;
    }

    public void hideTextInfo() {
        showTextInfo = false;
    }

    public void showTextInfo() {
        showTextInfo = true;
    }

    public void hideContourInfo() {
        showContourInfo = false;
    }

    public void showContourInfo() {
        showContourInfo = true;
    }

    public void hidePointInfo() {
        showPointInfo = false;
    }

    public void showPointInfo() {
        showPointInfo = true;
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!Tap.sampleCompleted() && Tap.getSampleWindowContour() != null) {
            CanvasUtil.drawContour(canvas, Tap.getSampleWindowContour(), Color.MAGENTA);
        }

        if (showContourInfo) {
            if (!handContours.isEmpty()) {
                CanvasUtil.drawContours(canvas, handContours, Color.RED);
            }
        }

        if (showPointInfo) {
            for (TapDetectPoint p : fingerTips) {
                int color;
                if (p.isFalling()) {
                    color = Color.rgb(0, 160, 0); // dark green
                } else if (p.isTapping()) {
                    color = Color.GREEN;
                } else if (p.isPressing()) {
                    color = Color.rgb(255, 170, 50);
                } else if (p.isLingering()) {
                    color = Color.BLUE;
                } else {
                    color = Color.GRAY;
                }

                CanvasUtil.drawPoint(canvas, p, color);
            }
        }

        if (showTextInfo) {
            String[] to_be_write = {
                    "Camera interval: " + cameraInterval + " ms",
                    "Process interval: " + processInterval + " ms",
                    "Time consumed: " + processDelay + " ms",
                    // "Hand contour: " + handContours.size() + " pts",
                    // "Finger tip: " + fingerTips.size() + " pts",
                    // "Falling: " + cnt[0] + " pts",
                    // "Tapping: " + cnt[1] + " pts",
                    // "Lingering: " + cnt[2] + " pts",
                    "ColorRange: " + ColorRange.getRange()[0],
                    "ColorRange: " + ColorRange.getRange()[1],
                    "Aver: " + Arrays.toString(Sampler.getAver()),
                    "ratio: " + Sampler.getRatio()
            };
            if (!Tap.sampleCompleted()) {
                CanvasUtil.writeText(canvas, to_be_write, 50);
            } else {
                CanvasUtil.writeText(canvas, to_be_write, 200);
            }
        }
    }

    public void updateInfo(long processDelay, long cameraInterval, long processInterval) {
        this.processDelay = processDelay;
        this.cameraInterval = cameraInterval;
        this.processInterval = processInterval;

        invalidate();
    }

}
