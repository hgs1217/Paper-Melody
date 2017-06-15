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
    private Date createTime;
    @SerializedName("message")
    private String message;

    public String getAuthor() {
        return author;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public String getMessage() {
        return message;
    }
}
