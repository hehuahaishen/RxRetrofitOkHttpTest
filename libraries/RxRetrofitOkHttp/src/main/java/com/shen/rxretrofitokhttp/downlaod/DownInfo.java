package com.shen.rxretrofitokhttp.downlaod;

import com.shen.rxretrofitokhttp.listener.HttpDownOnNextListener;

import java.io.Serializable;

/**
 * apk下载请求数据基础类
 * Created by WZG on 2016/10/20.
 */
public class DownInfo implements Serializable {

    private long id;
    /** 存储位置*/
    private String savePath;
    /** 文件总长度*/
    private long countLength;
    /** 下载长度*/
    private long readLength;

    /** 下载唯一的HttpService*/
    private HttpDownService service;
    /** 回调监听*/
    private HttpDownOnNextListener listener;
    /** 超时设置*/
    private  int connectonTime=20;
    /** state状态数据库保存*/
    private int stateInte;
    /** url*/
    private String url;

    public DownInfo(String url,HttpDownOnNextListener listener) {
        setUrl(url);
        setListener(listener);
    }

    public DownInfo(String url) {
        setUrl(url);
    }


    public DownInfo(long id, String savePath, long countLength, long readLength,
            int connectonTime, int stateInte, String url) {
        this.id = id;
        this.savePath = savePath;
        this.countLength = countLength;
        this.readLength = readLength;
        this.connectonTime = connectonTime;
        this.stateInte = stateInte;
        this.url = url;
    }

    public DownInfo() {
    }


    public DownState getState() {
        switch (getStateInte()){
            case 0:
                return DownState.START;
            case 1:
                return DownState.DOWN;
            case 2:
                return DownState.PAUSE;
            case 3:
                return DownState.STOP;
            case 4:
                return DownState.ERROR;
            case 5:
            default:
                return DownState.FINISH;
        }
    }

    public void setState(DownState state) {
        setStateInte(state.getState());
    }


    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }



    /** 存储位置*/
    public String getSavePath() {
        return savePath;
    }
    /** 存储位置*/
    public void setSavePath(String savePath) {
        this.savePath = savePath;
    }

    /** 文件总长度*/
    public long getCountLength() {
        return countLength;
    }
    /** 文件总长度*/
    public void setCountLength(long countLength) {
        this.countLength = countLength;
    }

    /** 下载长度*/
    public long getReadLength() {
        return readLength;
    }
    /** 下载长度*/
    public void setReadLength(long readLength) {
        this.readLength = readLength;
    }

    /** 下载唯一的HttpService*/
    public HttpDownService getService() {
        return service;
    }
    /** 下载唯一的HttpService*/
    public void setService(HttpDownService service) {
        this.service = service;
    }

    /** 回调监听*/
    public HttpDownOnNextListener getListener() {
        return listener;
    }
    /** 回调监听*/
    public void setListener(HttpDownOnNextListener listener) {
        this.listener = listener;
    }

    /** 超时设置*/
    public int getConnectonTime() {
        return this.connectonTime;
    }
    /** 超时设置*/
    public void setConnectonTime(int connectonTime) {
        this.connectonTime = connectonTime;
    }

    /** state状态数据库保存*/
    public int getStateInte() {
        return stateInte;
    }
    /** state状态数据库保存*/
    public void setStateInte(int stateInte) {
        this.stateInte = stateInte;
    }

    /** url*/
    public String getUrl() {
        return url;
    }
    /** url*/
    public void setUrl(String url) {
        this.url = url;
    }


}
