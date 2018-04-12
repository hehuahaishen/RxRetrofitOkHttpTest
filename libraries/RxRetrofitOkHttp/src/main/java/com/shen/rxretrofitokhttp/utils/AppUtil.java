package com.shen.rxretrofitokhttp.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 方法工具类
 *
 */

public class AppUtil {
    /**
     * 描述：判断网络是否有效.
     *
     * @return true, if is network available
     */
    public static boolean isNetworkAvailable(Context context) {
        try {
            ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivity != null) {
                NetworkInfo info = connectivity.getActiveNetworkInfo();
                if (info != null && info.isConnected()) {
                    if (info.getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return false;
    }


    /**
     * 读取BasUrl
     *
     * @param url
     * @return
     */
    public static String getBasUrl(String url) {
        String head = "";
        int index = url.indexOf("://");

        /* 存在 "://" */
        if (index != -1) {
            head = url.substring(0, index + 3);     // 如 -- http://
            url = url.substring(index + 3);         // 如 -- www.shen.com
        }

        /* 不存在 "://" */
        index = url.indexOf("/");                   // 可能是  www.shen.com/shen1/shen2
        if (index != -1) {
            url = url.substring(0, index + 1);      // 如 www.shen.com
        }
        return head + url;
    }

}
