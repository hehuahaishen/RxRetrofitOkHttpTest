package com.shen.rxretrofitokhttp.utils;

import android.content.Context;

import com.shen.rxretrofitokhttp.RxRetrofitApp;
import com.shen.rxretrofitokhttp.cookie.CookieResult;


/**
 * 数据缓存
 * 数据库工具类-geendao运用
 *
 */

public class CookieSPUtil {

    private static CookieSPUtil sp;
    private Context mContext;


    public CookieSPUtil() {
        mContext = RxRetrofitApp.getApplication();
    }


    /**
     * 获取单例
     * @return
     */
    public static CookieSPUtil getInstance() {
        if (sp == null) {
            synchronized (CookieSPUtil.class) {
                if (sp == null) {
                    sp = new CookieSPUtil();
                }
            }
        }
        return sp;
    }


    /**
     * 插入记录（数据）
     * @param info
     */
    public void saveCookie(CookieResult info){
        SPUtils.put(mContext, info.getUrl(), info);
    }

    /**
     * 更新记录（数据）
     * @param info
     */
    public void updateCookie(CookieResult info){
        SPUtils.put(mContext, info.getUrl(), info);
    }

    /**
     * 删除记录（数据）
     * @param info
     */
    public void deleteCookie(CookieResult info){
        SPUtils.remove(mContext, info.getUrl());
    }


    /**
     * 根据 "url字段" 查询获取记录（数据）
     * @param url
     * @return
     */
    public CookieResult queryCookieBy(String  url) {
        CookieResult into = new CookieResult();
        CookieResult intoTemp = (CookieResult) SPUtils.get(mContext, url, into);
        return intoTemp;
}

//    /**
//     * 获取所有的记录（数据）
//     *
//     * @return
//     */
//    public List<CookieResult> queryCookieAll() {
//        List<CookieResult> list = new ArrayList<>();
//        return list;
//    }
}
