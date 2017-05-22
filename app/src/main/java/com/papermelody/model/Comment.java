package com.papermelody.model;

import com.papermelody.model.response.CommentInfo;

import java.util.Date;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class Comment {
    /**
     * 评论类
     */

    private String author;
    private String musicID;
    private String content;
    private Date createTime;

    public Comment(CommentInfo info) {
        author = info.getAuthor();
        content = info.getComment();
        createTime = info.getCreateTime();
        musicID = info.getMusicID();
    }

    public String getAuthor() {
        return author;
    }

    public String getMusicID() {
        return musicID;
    }

    public String getContent() {
        return content;
    }

    public Date getCreateTime() {
        return createTime;
    }
}
