package com.papermelody.util;

import android.content.Context;

import com.papermelody.R;

/**
 * Created by HgS_1217_ on 2017/5/31.
 */

public class UrlUtil {
    /**
     * 处理url的工具类
     */

    public static String getImageUrl (Context context, String imgname) {
        return context.getString(R.string.server_ip) + "getimage/" + imgname;
    }
}
