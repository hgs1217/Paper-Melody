package com.papermelody.model;

import android.content.Context;

import com.papermelody.model.response.UserResponse;
import com.papermelody.util.UrlUtil;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class User {
    /**
     * 用户类
     */

    private Integer userID;
    private String username;
    private String avatarUrl;
    private String avatarName;
    private String password;
    private String nickname;
    private String userInfo;

    public User() { }

    public User(UserResponse.UserInfo info, Context context) {

        username = info.getName();
        userID = info.getUserID();
        nickname = info.getNickname();
        password = info.getPassword();
        avatarName = info.getAvatarName();
        if (info.getAvatarName().length() > 0) {
            avatarUrl = UrlUtil.getAvatarUrl(context, info.getAvatarName());
        } else {
            avatarUrl = null;
        }
    }

    public Integer getUserID() {
        return userID;
    }

    public void setUserID(Integer userID) {
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getAvatarName() {
        return avatarName;
    }

    public void setAvatarName(String avatarName) {
        this.avatarName = avatarName;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }
}
