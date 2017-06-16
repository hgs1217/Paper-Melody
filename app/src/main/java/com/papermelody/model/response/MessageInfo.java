package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.Date;

/**
 * Created by HgS_1217_ on 2017/6/15.
 */

public class MessageInfo {
    /**
     * 接收服务器端的message json数据
     */

    @SerializedName("author")
    private String author;
    @SerializedName("createTime")
    private String createTime;
    @SerializedName("message")
    private String message;
    @SerializedName("isNew")
    private Boolean isNew;

    public String getAuthor() {
        return author;
    }

    public String getCreateTime() {
        return createTime;
    }

    public String getMessage() {
        return message;
    }

    public Boolean isNew() {
        return isNew;
    }
}
