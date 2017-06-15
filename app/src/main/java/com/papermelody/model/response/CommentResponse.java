package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

import java.util.List;


/**
 * Created by 潘宇杰 on 2017-5-15 0015.
 */

public class CommentResponse extends HttpResponse {

    @SerializedName("result")
    private CommentListInfo result;

    public CommentListInfo getResult() {
        return result;
    }

    public class CommentListInfo {

        @SerializedName("count")
        private Integer count;
        @SerializedName("comments")
        private List<CommentInfo> comments;

        public CommentListInfo (Integer count, List<CommentInfo> comments) {
            this.count = count;
            this.comments = comments;
        }

        public Integer getCount() {
            return count;
        }

        public List<CommentInfo> getComments() {
            return comments;
        }
    }
}
