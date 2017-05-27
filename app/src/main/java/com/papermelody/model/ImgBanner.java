package com.papermelody.model;

/**
 * Created by HgS_1217_ on 2017/5/27.
 */

public class ImgBanner {

    private String title;
    private int resId;

    public ImgBanner (int resId) {
        this("", resId);
    }

    public ImgBanner (String title, int resId) {
        this.title = title;
        this.resId = resId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getResId() {
        return resId;
    }

    public void setResId(int resId) {
        this.resId = resId;
    }
}
