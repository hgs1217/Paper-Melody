package com.papermelody.util;

import android.content.Context;

/**
 * Created by HgS_1217_ on 2017/5/31.
 */

public class UrlUtil {
    /**
     * 处理url的工具类
     */

    public static String getImageUrl (Context context, String imgname) {
        return App.getServerIP() + "download/img/" + imgname;
    }
}
