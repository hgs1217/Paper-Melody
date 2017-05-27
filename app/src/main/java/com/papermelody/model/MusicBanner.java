package com.papermelody.model;

/**
 * Created by HgS_1217_ on 2017/5/27.
 */

public class MusicBanner {

    private String title;
    private String imgUrl;

    public MusicBanner(String title, String imgUrl) {
        this.title = title;
        this.imgUrl = imgUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
