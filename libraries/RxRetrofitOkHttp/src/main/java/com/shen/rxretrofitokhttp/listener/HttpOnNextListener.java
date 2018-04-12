package com.shen.rxretrofitokhttp.listener;


import com.shen.rxretrofitokhttp.exception.ApiException;

/**
 * 成功回调处理
 */
public interface HttpOnNextListener {

    /**
     * 成功后回调方法
     *
     * @param resulte
     * @param method
     */
    void onNext(String resulte, String method);

    /**
     * 失败<p>
     * 失败或者错误方法<br>
     * 自定义异常处理
     *
     * @param e             回调统一请求异常
     * @param method
     */
    void onError(ApiException e, String method);
}
