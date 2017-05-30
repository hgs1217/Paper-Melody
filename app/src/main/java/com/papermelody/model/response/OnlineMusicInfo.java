package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by HgS_1217_ on 2017/5/4.
 */

public class OnlineMusicInfo {
    /**
     * 接收服务器端的onlineMusic json数据
     */

    @SerializedName("name")
    private String name;
    @SerializedName("author")
    private String author;
    @SerializedName("date")
    private Date date;
    @SerializedName("link")
    private String link;
    @SerializedName("imglink")
    private String imgLink;

    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getLink() {
        return link;
    }

    public String getImgLink() {
        return imgLink;
    }
}
