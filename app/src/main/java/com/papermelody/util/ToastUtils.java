package com.papermelody.util;

import android.widget.Toast;

/**
 * Created by HgS_1217_ on 2017/5/2.
 */

public class ToastUtils {
    /**
     * 处理Toast工具类
     */

    public static void showShort(int resId) {
        Toast.makeText(App.getInstance(), resId, Toast.LENGTH_SHORT).show();
    }


    public static void showShort(String message) {
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_SHORT).show();
    }


    public static void showLong(int resId) {
        Toast.makeText(App.getInstance(), resId, Toast.LENGTH_LONG).show();
    }


    public static void showLong(String message) {
        Toast.makeText(App.getInstance(), message, Toast.LENGTH_LONG).show();
    }
}
