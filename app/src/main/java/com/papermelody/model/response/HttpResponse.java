package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HgS_1217_ on 2017/5/2.
 */

public class HttpResponse {
    /**
     * 用于处理服务器响应，查看操作状态
     */

    @SerializedName("error")
    private int error;
    @SerializedName("msg")
    private String msg;

    public int getError() {
        return error;
    }

    public String getMsg() {
        return msg;
    }
}