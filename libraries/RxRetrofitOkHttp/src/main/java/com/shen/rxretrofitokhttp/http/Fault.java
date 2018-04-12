package com.shen.rxretrofitokhttp.http;

/**
 * 异常处理类，将异常包装成一个 Fault ,抛给上层统一处理
 */

public class Fault extends RuntimeException {
    private int mErrorCode;

    /**
     *
     * @param errorCode 网络请求的请求码
     * @param message
     */
    public Fault(int errorCode, String message){
        super(message);
        mErrorCode = errorCode;
    }

    public int getErrorCode() {
        return mErrorCode;
    }
}
