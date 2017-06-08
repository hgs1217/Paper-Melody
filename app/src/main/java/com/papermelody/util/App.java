package com.papermelody.util;

import android.app.Application;
import android.content.Context;

import com.papermelody.R;
import com.papermelody.model.User;

/**
 * Created by HgS_1217_ on 2017/5/1.
 */

public class App extends Application {
    /**
     * 用于记录各类数据的应用类，例如用户，设置等应用全局变量
     */

    private static User user;
    private static String serverIP;
    private static Context context;

    public static Context getInstance() { return context; }

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        user = null;
        serverIP = getString(R.string.server_ip);
    }

    public static User getUser() {
        return user;
    }

    public static void setUser(User user) {
        App.user = user;
    }

    public static String getServerIP() {
        return serverIP;
    }

    public static void setServerIP(String serverIP) {
        App.serverIP = serverIP;
    }
}
