package com.shen.rxretrofitokhttp.cookie;

import java.io.Serializable;

/**
 * post請求緩存数据
 *
 */
public class CookieResult implements Serializable {

    private Long id;
    /** url */
    private String url;
    /** 返回结果 */
    private String resulte;
    /** 时间 */
    private long time;

    public CookieResult(String url, String resulte, long time) {
        this.url = url;
        this.resulte = resulte;
        this.time = time;
    }

    public CookieResult(Long id, String url, String resulte, long time) {
        this.id = id;
        this.url = url;
        this.resulte = resulte;
        this.time = time;
    }

    public CookieResult() {
    }


    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    /** url */
    public String getUrl() {
        return this.url;
    }
    /** url */
    public void setUrl(String url) {
        this.url = url;
    }

    /** 返回结果 */
    public String getResulte() {
        return this.resulte;
    }
    /** 返回结果 */
    public void setResulte(String resulte) {
        this.resulte = resulte;
    }

    /** 时间 */
    public long getTime() {
        return this.time;
    }
    /** 时间 */
    public void setTime(long time) {
        this.time = time;
    }


}
