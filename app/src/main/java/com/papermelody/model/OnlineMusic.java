package com.papermelody.model;

import android.content.Context;

import com.papermelody.R;
import com.papermelody.model.response.OnlineMusicInfo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class OnlineMusic extends Music implements Serializable {
    /**
     * 上传音乐类
     */

    private String filename;
    private String path;
    private String musicName;
    private String musicAuthor;
    private String musicInfo;
    private Date musicCreateDate;
    private String musicLink;
    private String musicPhotoUrl;
    private String uploadName;
    private String uploadUser;
    private String uploadInfo;
    private ArrayList<Comment> comments;
    private int upvoteNum;

    public OnlineMusic() { }

    public OnlineMusic(OnlineMusicInfo info, Context context) {
        musicName = info.getName();
        musicAuthor = info.getAuthor();
        musicCreateDate = info.getDate();
        musicLink = info.getLink();
        if (info.getImgLink().length() > 0) {
            musicPhotoUrl = context.getString(R.string.server_ip) + info.getImgLink();
        } else {
            musicPhotoUrl = null;
        }
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public void setFilename(String filename) {
        this.filename = filename;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public void setPath(String path) {
        this.path = path;
    }

    @Override
    public String getMusicName() {
        return musicName;
    }

    @Override
    public void setMusicName(String musicName) {
        this.musicName = musicName;
    }

    @Override
    public String getMusicAuthor() {
        return musicAuthor;
    }

    @Override
    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    @Override
    public String getMusicInfo() {
        return musicInfo;
    }

    @Override
    public void setMusicInfo(String musicInfo) {
        this.musicInfo = musicInfo;
    }

    public Date getMusicCreateDate() {
        return musicCreateDate;
    }

    public void setMusicCreateDate(Date musicCreateDate) {
        this.musicCreateDate = musicCreateDate;
    }

    public String getMusicLink() {
        return musicLink;
    }

    public void setMusicLink(String musicLink) {
        this.musicLink = musicLink;
    }

    public String getUploadName() {
        return uploadName;
    }

    public void setUploadName(String uploadName) {
        this.uploadName = uploadName;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public void setUploadUser(String uploadUser) {
        this.uploadUser = uploadUser;
    }

    public String getUploadInfo() {
        return uploadInfo;
    }

    public void setUploadInfo(String uploadInfo) {
        this.uploadInfo = uploadInfo;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public int getUpvoteNum() {
        return upvoteNum;
    }

    public void setUpvoteNum(int upvoteNum) {
        this.upvoteNum = upvoteNum;
    }

    public String getMusicPhotoUrl() {
        return musicPhotoUrl;
    }

    public void setMusicPhotoUrl(String musicPhotoUrl) {
        this.musicPhotoUrl = musicPhotoUrl;
    }
}
