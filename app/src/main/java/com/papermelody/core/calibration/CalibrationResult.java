package com.papermelody.core.calibration;

import java.io.Serializable;

/**
 * Created by HgS_1217_ on 2017/5/28.
 */

public class CalibrationResult implements Serializable {

    private boolean flag;
    private int leftLowX, leftLowY, leftUpX, leftUpY, rightLowX, rightLowY, rightUpX, rightUpY, leftUpRightX, leftUpRightY, rightUpLeftX, rightUpLeftY;

    public CalibrationResult() {
        flag = false;
        leftLowX = 0;
        leftLowY = 0;
        leftUpX = 0;
        leftUpY = 0;
        rightLowX = 0;
        rightLowY = 0;
        rightUpX = 0;
        rightUpY = 0;
        leftUpRightX = 0;
        leftUpRightY = 0;
        rightUpLeftX = 0;
        rightUpLeftY = 0;
    }

    public boolean isFlag() {
        return flag;
    }

    public int getLeftLowX() {
        return leftLowX;
    }

    public int getLeftLowY() {
        return leftLowY;
    }

    public int getLeftUpX() {
        return leftUpX;
    }

    public int getLeftUpY() {
        return leftUpY;
    }

    public int getRightLowX() {
        return rightLowX;
    }

    public int getRightLowY() {
        return rightLowY;
    }

    public int getRightUpX() {
        return rightUpX;
    }

    public int getRightUpY() {
        return rightUpY;
    }

    public int getLeftUpRightX() {
        return leftUpRightX;
    }

    public int getLeftUpRightY() {
        return leftUpRightY;
    }

    public int getRightUpLeftX() {
        return rightUpLeftX;
    }

    public int getRightUpLeftY() {
        return rightUpLeftY;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public void setLeftLowX(int leftLowX) {
        this.leftLowX = leftLowX;
    }

    public void setLeftLowY(int leftLowY) {
        this.leftLowY = leftLowY;
    }

    public void setLeftUpX(int leftUpX) {
        this.leftUpX = leftUpX;
    }

    public void setLeftUpY(int leftUpY) {
        this.leftUpY = leftUpY;
    }

    public void setRightLowX(int rightLowX) {
        this.rightLowX = rightLowX;
    }

    public void setRightLowY(int rightLowY) {
        this.rightLowY = rightLowY;
    }

    public void setRightUpX(int rightUpX) {
        this.rightUpX = rightUpX;
    }

    public void setRightUpY(int rightUpY) {
        this.rightUpY = rightUpY;
    }

    public void setLeftUpRightX(int leftUpRightX) {
        this.leftUpRightX = leftUpRightX;
    }

    public void setLeftUpRightY(int leftUpRightY) {
        this.leftUpRightY = leftUpRightY;
    }

    public void setRightUpLeftX(int rightUpLeftX) {
        this.rightUpLeftX = rightUpLeftX;
    }

    public void setRightUpLeftY(int rightUpLeftY) {
        this.rightUpLeftY = rightUpLeftY;
    }
}
