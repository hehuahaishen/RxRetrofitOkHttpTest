package com.shen.rxretrofitokhttp.api;

import io.reactivex.Observable;
import retrofit2.Retrofit;

/**
 * 参数的设置
 */
public abstract class BaseApi {

    /** 基础url*/
    // private String mBaseUrl = "https://www.izaodao.com/Api/";
    // private String mBaseUrl = "localhost:8080/C3P0Test/";
    private String mBaseUrl = "http://192.168.23.1:8080/C3P0Test/";


    /** 链接 -- 超时时间 -- 默认6秒*/
    private int mConnectionTime = 6;
    /** 写操作 -- 超时时间 */
    private int mWriteTime = 10;
    /** 读操作 -- 超时时间 */
    private int mReadTime = 10;

    /** 有网 -- 情况下的本地缓存时间 -- 默认60秒 */
    private int mCookieNetWorkTime = 60;
    /** 无网络 -- 的情况下本地缓存时间 -- 默认30天 */
    private int mCookieNoNetWorkTime = 24 * 60 * 60 * 30;


    /** retry次数 -- retry:重试 */
    private int mRetryCount = 1;
    /** retry延迟 -- retry:重试 */
    private long mRetryDelay = 100;
    /** retry叠加延迟 -- retry:重试 */
    private long mRetryIncreaseDelay = 100;

    /** 是否 -- 能取消 -- 加载框 */
    private boolean mCancel = false;
    /** 是否显示加载框 */
    private boolean mShowProgress = true;
    /** 是否需要缓存处理 */
    private boolean mCache = false;


    /** 缓存url-可手动设置 */
    private String mCacheUrl;
    /** 方法 -- 如果需要缓存必须设置这个参数；不需要不用設置 */
    private String mMethod = "";
    /*----------------------------------------------------------------------------*/
    /**
     * 设置参数 -- 抽象方法 -- 子类实现
     *
     * @param retrofit
     * @return
     */
    public abstract Observable getObservable(Retrofit retrofit);


    /*----------------------------------------------------------------------------*/
    /** 基础url*/
    public String getBaseUrl() {
        return mBaseUrl;
    }
    /** 基础url*/
    public void setBaseUrl(String baseUrl) {
        this.mBaseUrl = baseUrl;
    }


    /** 链接 -- 超时时间 -- 默认6秒 */
    public int getConnectionTime() {
        return mConnectionTime;
    }
    /** 链接 -- 超时时间 -- 默认6秒 */
    public void setConnectionTime(int connectionTime) {
        this.mConnectionTime = connectionTime;
    }

    /** 写操作 -- 超时时间 */
    public int getWriteTime() {
        return mWriteTime;
    }
    /** 写操作 -- 超时时间 */
    public void setWriteTime(int writeTime) {
        mWriteTime = writeTime;
    }

    /** 读操作 -- 超时时间 */
    public int getReadTime() {
        return mReadTime;
    }
    /** 读操作 -- 超时时间 */
    public void setReadTime(int readTime) {
        mReadTime = readTime;
    }

    /** 有网 -- 情况下的本地缓存时间 -- 默认60秒 */
    public int getCookieNetWorkTime() {
        return mCookieNetWorkTime;
    }
    /** 有网 -- 情况下的本地缓存时间 -- 默认60秒 */
    public void setCookieNetWorkTime(int cookieNetWorkTime) {
        mCookieNetWorkTime = cookieNetWorkTime;
    }

    /** 无网络 -- 的情况下本地缓存时间 -- 默认30天 */
    public int getCookieNoNetWorkTime() {
        return mCookieNoNetWorkTime;
    }
    /** 无网络 -- 的情况下本地缓存时间 -- 默认30天 */
    public void setCookieNoNetWorkTime(int cookieNoNetWorkTime) {
        mCookieNoNetWorkTime = cookieNoNetWorkTime;
    }

    /** retry次数 -- retry:重试 */
    public int getRetryCount() {
        return mRetryCount;
    }
    /** retry次数 -- retry:重试 */
    public void setRetryCount(int retryCount) {
        mRetryCount = retryCount;
    }

    /** retry延迟 -- retry:重试 */
    public long getRetryDelay() {
        return mRetryDelay;
    }
    /** retry延迟 -- retry:重试 */
    public void setRetryDelay(long retryDelay) {
        mRetryDelay = retryDelay;
    }

    /** retry叠加延迟 -- retry:重试 */
    public long getRetryIncreaseDelay() {
        return mRetryIncreaseDelay;
    }
    /** retry叠加延迟 -- retry:重试 */
    public void setRetryIncreaseDelay(long retryIncreaseDelay) {
        mRetryIncreaseDelay = retryIncreaseDelay;
    }

    /** 是否 -- 能取消 -- 加载框 */
    public boolean isCancel() {
        return mCancel;
    }
    /** 是否 -- 能取消 -- 加载框 */
    public void setCancel(boolean cancel) {
        mCancel = cancel;
    }

    /** 是否显示加载框 */
    public boolean isShowProgress() {
        return mShowProgress;
    }
    /** 是否显示加载框 */
    public void setShowProgress(boolean showProgress) {
        mShowProgress = showProgress;
    }

    /** 是否需要缓存处理 */
    public boolean isCache() {
        return mCache;
    }
    /** 是否需要缓存处理 */
    public void setCache(boolean cache) {
        mCache = cache;
    }


    /** 缓存url-可手动设置 */
    public String getCacheUrl() {
        return mCacheUrl;
    }
    /** 缓存url-可手动设置 */
    public void setCacheUrl(String cacheUrl) {
        mCacheUrl = cacheUrl;
    }

    /** 方法-如果需要缓存必须设置这个参数；不需要不用設置*/
    public String getMethod() {
        return mMethod;
    }
    /** 方法-如果需要缓存必须设置这个参数；不需要不用設置*/
    public void setMethod(String method) {
        mMethod = method;
    }


    /**
     * 获取缓存地址
     *
     * @return
     */
    public String getUrl() {
        /*在没有手动设置url情况下，简单拼接*/
        if (null == getCacheUrl() || "".equals(getCacheUrl())) {
            return getBaseUrl() + getMethod();
        }
        return getCacheUrl();
    }
}
