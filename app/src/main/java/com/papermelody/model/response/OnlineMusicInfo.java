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
    @SerializedName("musicName")
    private String musicName;
    @SerializedName("imgName")
    private String imgName;
    @SerializedName("musicID")
    private Integer musicID;
    @SerializedName("viewNum")
    private Integer viewNum;
    @SerializedName("upvoteNum")
    private Integer upvoteNum;


    public String getName() {
        return name;
    }

    public String getAuthor() {
        return author;
    }

    public Date getDate() {
        return date;
    }

    public String getMusicName() {
        return musicName;
    }

    public String getImgName() {
        return imgName;
    }

    public Integer getMusicID() {
        return musicID;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public Integer getUpvoteNum() {
        return upvoteNum;
    }
}
