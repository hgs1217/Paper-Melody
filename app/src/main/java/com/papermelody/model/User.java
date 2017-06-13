package com.papermelody.model;

import java.util.ArrayList;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class User {
    /**
     * 用户类
     */

    private Integer userID;
    private String username;
    private String photoURL;
    private String userInfo;
    private ArrayList<Music> userMusics;

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

    public String getPhotoURL() {
        return photoURL;
    }

    public void setPhotoURL(String photoURL) {
        this.photoURL = photoURL;
    }

    public String getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(String userInfo) {
        this.userInfo = userInfo;
    }

    public ArrayList<Music> getUserMusics() {
        return userMusics;
    }

    public void setUserMusics(ArrayList<Music> userMusics) {
        this.userMusics = userMusics;
    }
}
