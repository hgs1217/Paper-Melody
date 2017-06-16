package com.papermelody.widget;

/**
 * Created by tangtonghui on 17/6/16.
 */

import android.graphics.Paint;

public class Bean {

    int alpha; //
    int X; //
    int Y; //
    float radius; //
    Paint paint; //

    public int getAlpha() {
        return alpha;
    }

    public int getX() {
        return X;
    }

    public int getY() {
        return Y;
    }

    public float getRadius() {
        return radius;
    }

    public Paint getPaint() {
        return paint;
    }

    public void setAlpha(int alpha) {
        this.alpha = alpha;
    }

    public void setX(int x) {
        X = x;
    }

    public void setY(int y) {
        Y = y;
    }

    public void setRadius(float radius) {
        this.radius = radius;
    }

    public void setPaint(Paint paint) {
        this.paint = paint;
    }
}
