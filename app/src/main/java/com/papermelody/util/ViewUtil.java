package com.papermelody.util;

import android.app.Activity;
import android.content.Context;

/**
 * Created by HgS_1217_ on 2017/4/10.
 */

public class ViewUtil {
    /**
     * 用于界面的工具类
     */

    public static int getWindowRotation(Activity activity) {
        return activity.getWindowManager().getDefaultDisplay().getRotation();
    }

    public static int getScreenWidth(Context context) {
        return context.getResources().getDisplayMetrics().widthPixels;
    }

    public static int getScreenHeight(Context context) {
        return context.getResources().getDisplayMetrics().heightPixels;
    }

    /**
     * dp -> px 单位转化
     **/
    public static int pxToDp(Context context, int px) {
        double density = context.getResources().getDisplayMetrics().density;
        double dpFloat = px / density;
        return (int) (dpFloat + 0.5);
    }

    public static int dpToPx(Context context, int dp) {
        double density = context.getResources().getDisplayMetrics().density;
        double dpFloat = dp * density;
        return (int) (dpFloat + 0.5);
    }
}
