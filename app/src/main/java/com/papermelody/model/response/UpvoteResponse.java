package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HgS_1217_ on 2017/6/14.
 */

public class UpvoteResponse extends HttpResponse {
    /**
     * 用于处理点赞状态响应
     */

    @SerializedName("status")
    private boolean status;
    @SerializedName("upvoteNum")
    private int upvoteNum;
    @SerializedName("viewNum")
    private int viewNum;

    public boolean isStatus() {
        return status;
    }

    public int getUpvoteNum() {
        return upvoteNum;
    }

    public int getViewNum() {
        return viewNum;
    }
}