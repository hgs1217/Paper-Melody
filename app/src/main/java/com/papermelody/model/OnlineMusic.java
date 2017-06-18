package com.papermelody.model;

import android.content.Context;

import com.papermelody.model.response.OnlineMusicInfo;
import com.papermelody.util.UrlUtil;

import java.io.Serializable;
import java.util.ArrayList;

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
    private Integer musicAuthorID;
    private String musicAuthorAvatarUrl;
    private String musicInfo;
    private String musicCreateDate;

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
        musicAuthorID = info.getAuthorID();
        if (info.getAuthorAvatar().length() > 0) {
            musicAuthorAvatarUrl = UrlUtil.getAvatarUrl(context, info.getAuthorAvatar());
        } else {
            musicAuthorAvatarUrl = "";
        }

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
            musicPhotoUrl = "";
        }
    }

    @Override
    public String getFilename() {
        return filename;
    }

    @Override
    public String getPath() {
        return path;
    }

    @Override
    public String getMusicName() {
        return musicName;
    }

    public String getMusicAuthor() {
        return musicAuthor;
    }

    public String getMusicInfo() {
        return musicInfo;
    }

    public String getMusicCreateDate() {
        return musicCreateDate;
    }

    public String getUploadName() {
        return uploadName;
    }

    public String getUploadUser() {
        return uploadUser;
    }

    public String getUploadInfo() {
        return uploadInfo;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public String getMusicPhotoUrl() {
        return musicPhotoUrl;
    }

    public Integer getMusicID() {
        return musicID;
    }

    public Integer getViewNum() {
        return viewNum;
    }

    public Integer getUpvoteNum() {
        return upvoteNum;
    }

    public static String getSerialOnlinemusic() {
        return SERIAL_ONLINEMUSIC;
    }

    public Integer getMusicAuthorID() {
        return musicAuthorID;
    }

    public String getMusicAuthorAvatarUrl() {
        return musicAuthorAvatarUrl;
    }
}
