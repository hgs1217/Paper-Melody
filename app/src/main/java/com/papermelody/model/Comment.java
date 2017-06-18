package com.papermelody.model;

import android.content.Context;

import com.papermelody.model.response.CommentInfo;
import com.papermelody.util.UrlUtil;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class Comment {
    /**
     * 评论类
     */

    private String author;
    private Integer authorID;
    private String authorAvatarUrl;
    private Integer musicID;
    private String content;
    private String createTime;

    public Comment(Context context, CommentInfo info) {
        author = info.getAuthor();
        authorID = info.getAuthorID();
        if (info.getAuthorAvatar().length() > 0) {
            authorAvatarUrl = UrlUtil.getAvatarUrl(context, info.getAuthorAvatar());
        } else {
            authorAvatarUrl = "";
        }
        content = info.getComment();
        createTime = info.getCreateTime();
        musicID = info.getMusicID();
    }

    public Comment(Integer autID, Integer musID, String aut, String cre, String con, String autAva) {
        author = aut;
        authorID = autID;
        authorAvatarUrl = autAva;
        content = con;
        createTime = cre;
        musicID = musID;
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

    public String getAuthorAvatarUrl() {
        return authorAvatarUrl;
    }

    public String getContent() {
        return content;
    }

    public String getCreateTime() {
        return createTime;
    }
}
