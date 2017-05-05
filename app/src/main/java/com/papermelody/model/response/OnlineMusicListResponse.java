package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by HgS_1217_ on 2017/5/4.
 */

public class OnlineMusicListResponse extends HttpResponse {
    /**
     * 用于处理音乐圈获取作品集的响应
     */

    @SerializedName("result")
    private OnlineMusicListInfo result;

    public OnlineMusicListInfo getResult() {
        return result;
    }

    public class OnlineMusicListInfo {

        @SerializedName("count")
        private Integer count;
        @SerializedName("musics")
        private List<OnlineMusicInfo> musics;

        public OnlineMusicListInfo (Integer count, List<OnlineMusicInfo> musics) {
            this.count = count;
            this.musics = musics;
        }

        public Integer getCount() {
            return count;
        }

        public List<OnlineMusicInfo> getMusics() {
            return musics;
        }
    }
}
