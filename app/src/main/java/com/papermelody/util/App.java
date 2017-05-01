package com.papermelody.util;

import android.app.Application;

import com.papermelody.model.User;

/**
 * Created by HgS_1217_ on 2017/5/1.
 */

public class App extends Application {
    /**
     * 用于记录各类数据的应用类，例如用户，设置等应用全局变量
     */

    private User user;

    public App() {
        user = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
