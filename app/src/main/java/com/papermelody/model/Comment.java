package com.papermelody.model;

import com.papermelody.model.response.CommentInfo;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class Comment {
    /**
     * 评论类
     */

    private String author;
    private Integer authorID;
    private Integer musicID;
    private String content;
    private String createTime;

    public Comment(CommentInfo info) {
        author = info.getAuthor();
        authorID = info.getAuthorID();
        content = info.getComment();
        createTime = info.getCreateTime();
        musicID = info.getMusicID();
    }

    public String getAuthor() {
        return author;
    }

    public Integer getAuthorID() {
        return authorID;
    }

    public Integer getMusicID() {
        return musicID;
    }

    public String getContent() {
        return content;
    }

    public String getCreateTime() {
        return createTime;
    }
}
