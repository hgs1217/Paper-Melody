package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HgS_1217_ on 2017/5/30.
 */

public class UploadMusicResponse extends HttpResponse {
    /**
     * 上传音乐的响应
     */

    @SerializedName("fileName")
    private String fileName;

    public String getFileName() {
        return fileName;
    }
}
