package com.papermelody.util;

import android.app.Application;
import android.content.Context;

import com.papermelody.model.User;

/**
 * Created by HgS_1217_ on 2017/5/1.
 */

public class App extends Application {
    /**
     * 用于记录各类数据的应用类，例如用户，设置等应用全局变量
     */

    public static final double STANDARD_SIZE_RATE = 1.33333; // 4: 3

    private User user;
    private static Context context;

    public static Context getInstance(){
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        user = null;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
