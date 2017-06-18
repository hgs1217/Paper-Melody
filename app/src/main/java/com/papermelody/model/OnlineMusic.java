package com.papermelody.model;

import android.content.Context;

import com.papermelody.model.response.OnlineMusicInfo;
import com.papermelody.util.UrlUtil;

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

    public static final String SERIAL_ONLINEMUSIC = "SERIAL_ONLINEMUSIC";

    /**
     * musicLink: 服务器端存放的音乐文件名，通过downloadMusic接口下载，与本地音乐文件同名
     */
    private String filename;

    private String path;
    private Integer musicID;
    private String musicName;
    private String musicAuthor;
    private String musicInfo;
    private Date musicCreateDate;

    /**
     * musicPhotoUrl: 服务器端存放的图片URL
     */
    private String musicPhotoUrl;
    private String uploadName;
    private String uploadUser;
    private String uploadInfo;
    private ArrayList<Comment> comments;
    private Integer viewNum;
    private Integer upvoteNum;

    public OnlineMusic() { }

    public OnlineMusic(OnlineMusicInfo info, Context context) {

        /**
         * getName 获取到的是作品名
         */
        musicName = info.getName();
        musicInfo = info.getMusicInfo();
        musicAuthor = info.getAuthor();
        musicCreateDate = info.getDate();

        /**
         * getMusicName 获取到的是文件名
         */
        filename = info.getMusicName();

        musicID = info.getMusicID();
        viewNum = info.getViewNum();
        upvoteNum = info.getUpvoteNum();
        if (info.getImgName().length() > 0) {
            musicPhotoUrl = UrlUtil.getImageUrl(context, info.getImgName());
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

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public void setMusicAuthor(String musicAuthor) {
        this.musicAuthor = musicAuthor;
    }

    public String getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(String musicInfo) {
        this.musicInfo = musicInfo;
    }

    public Date getMusicCreateDate() {
        return musicCreateDate;
    }

    public void setMusicCreateDate(Date musicCreateDate) {
        this.musicCreateDate = musicCreateDate;
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

    public String getMusicPhotoUrl() {
        return musicPhotoUrl;
    }

    public void setMusicPhotoUrl(String musicPhotoUrl) {
        this.musicPhotoUrl = musicPhotoUrl;
    }

    public Integer getMusicID() {
        return musicID;
    }

    public void setMusicID(Integer musicID) {
        this.musicID = musicID;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public void setViewNum(Integer viewNum) {
        this.viewNum = viewNum;
    }

    public Integer getUpvoteNum() {
        return upvoteNum;
    }

    public void setUpvoteNum(Integer upvoteNum) {
        this.upvoteNum = upvoteNum;
    }
}
