package com.papermelody.model.response;

import com.google.gson.annotations.SerializedName;

/**
 * Created by HgS_1217_ on 2017/5/1.
 */

public class UserResponse extends HttpResponse {
    /**
     * 用于处理用户信息的服务器响应，用于登录与注册
     */

    @SerializedName("result")
    private UserInfo result;

    public UserInfo getResult() {
        return result;
    }

    public class UserInfo {

        @SerializedName("userID")
        private Integer userID;
        @SerializedName("name")
        private String name;
        @SerializedName("password")
        private String password;
        @SerializedName("nickname")
        private String nickname;
        @SerializedName("avatarName")
        private String avatarName;

        public UserInfo (String name, String pw, Integer id, String nickname, String avatarName) {
            userID = id;
            this.name = name;
            password = pw;
            this.nickname = nickname;
            this.avatarName = avatarName;
        }

        public Integer getUserID() {
            return userID;
        }

        public String getName() {
            return name;
        }

        public String getPassword() {
            return password;
        }

        public String getNickname() {
            return nickname;
        }

        public String getAvatarName() {
            return avatarName;
        }
    }
}
