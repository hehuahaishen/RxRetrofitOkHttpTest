package com.shen.rxretrofitokhttp.exception;

/**
 * ApiException extends Exception <p>
 * 回调统一请求异常
 *
 */
public class ApiException extends Exception{

    /** 错误码*/
    private int mCode;
    /** 显示的信息*/
    private String mDisplayMessage;

    public ApiException(Throwable e) {
        super(e);
    }

    /**
     *
     * @param cause         异常
     * @param code          错误码     -- 限定了错误码的范围
     * @param showMsg       显示的信息
     */
    public ApiException(Throwable cause, @CodeException.CodeEp int code, String showMsg) {
        super(showMsg, cause);
        setCode(code);
        setDisplayMessage(showMsg);
    }

    /** 错误码*/
    @CodeException.CodeEp
    public int getCode() {
        return mCode;
    }
    /** 错误码*/
    public void setCode(@CodeException.CodeEp int code) {
        this.mCode = code;
    }

    /** 显示的信息*/
    public String getDisplayMessage() {
        return mDisplayMessage;
    }
    /** 显示的信息*/
    public void setDisplayMessage(String displayMessage) {
        this.mDisplayMessage = displayMessage;
    }
}
