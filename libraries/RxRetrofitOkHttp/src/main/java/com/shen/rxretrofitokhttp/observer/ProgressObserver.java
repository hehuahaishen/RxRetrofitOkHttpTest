package com.shen.rxretrofitokhttp.observer;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import com.shen.rxretrofitokhttp.RxRetrofitApp;
import com.shen.rxretrofitokhttp.api.BaseApi;
import com.shen.rxretrofitokhttp.cookie.CookieResult;
import com.shen.rxretrofitokhttp.exception.ApiException;
import com.shen.rxretrofitokhttp.exception.CodeException;
import com.shen.rxretrofitokhttp.exception.HttpTimeException;
import com.shen.rxretrofitokhttp.listener.HttpOnNextListener;
import com.shen.rxretrofitokhttp.utils.AppUtil;
import com.shen.rxretrofitokhttp.utils.CookieSPUtil;

import java.lang.ref.SoftReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;


/**
 * 用于在Http请求开始时，自动显示一个ProgressDialog        <br>
 * 在Http请求结束是，关闭ProgressDialog                  <br>
 * 调用者自己对请求数据进行处理                           <br>
 */
public class ProgressObserver<T> implements Observer<T> {

    /** 是否弹框*/
    private boolean mShowPorgress = true;
    /** 回调接口*/
    private SoftReference<HttpOnNextListener> mHttpOnNextListener;
    /** 软引用反正内存泄露*/
    private SoftReference<Context> mContext;
    /** 加载框可自己定义*/
    private ProgressDialog pd;
    /** 请求数据*/
    private BaseApi mApi;


    /** 可用于取消订阅 */
    Disposable mDisposable = null;

    /**
     * 构造
     *
     * @param api
     */
    public ProgressObserver(BaseApi api, HttpOnNextListener httpOnNextListener, Context context) {

        mApi = api;
        mHttpOnNextListener = new SoftReference(httpOnNextListener);
        mContext = new SoftReference(context);

        setShowPorgress(api.isShowProgress());

        if (api.isShowProgress()) {
            initProgressDialog(api.isCancel());
        }
    }

    /*--------------------------------- PorgressDialog  start ----------------------------------*/

    /**
     * 显示加载框
     */
    private void showProgressDialog() {
        if (!isShowPorgress())
            return;

        Context context = mContext.get();

        if (pd == null || context == null)
            return;

        if (!pd.isShowing()) {
            pd.show();
        }
    }


    /**
     * 隐藏
     */
    private void dismissProgressDialog() {
        if (!isShowPorgress())
            return;

        if (pd != null && pd.isShowing()) {
            pd.dismiss();
        }
    }


    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancel() {
        // isDisposed() -- true:资源被释放了 ; false:资源没被释放了
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }


    /**
     * 查看 -- 设置了"弹框" 还是 "没弹框"
     * @return
     */
    public boolean isShowPorgress() {
        return mShowPorgress;
    }

    /**
     * 是否需要弹框设置
     * @param showPorgress
     */
    public void setShowPorgress(boolean showPorgress) {
        mShowPorgress = showPorgress;
    }
    /*--------------------------------- PorgressDialog  end ----------------------------------*/




    /*--------------------------------- Observer  start --------------------------------*/

    /**
     * 显示ProgressDialog
     */
    public void onStart() {
        showProgressDialog();

        // 设置了"缓存" 并且 "有网"
        if (mApi.isCache() && AppUtil.isNetworkAvailable(RxRetrofitApp.getApplication())) {
            // 获取缓存数据
            CookieResult cookieResulte = CookieSPUtil.getInstance().queryCookieBy(mApi.getUrl());

            if (cookieResulte != null) {
                long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;
                // 有网 -- 情况下的本地缓存时间 -- 默认60秒
                if (time < mApi.getCookieNetWorkTime()) {                    // 没有超过"缓存时间" -- 直接返回这个
                    if (mHttpOnNextListener.get() != null) {
                        mHttpOnNextListener.get().onNext(cookieResulte.getResulte(), mApi.getMethod());
                    }
                    onComplete();
                    onCancel();              // 解除 -- 订阅
                }
            }
        }
    }

    

    @Override
    public void onSubscribe(Disposable d) {
        onStart();
        mDisposable = d;
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
         /*缓存处理*/
        if (mApi.isCache()) {
            CookieResult resulte = CookieSPUtil.getInstance().queryCookieBy(mApi.getUrl());
            long time = System.currentTimeMillis();

            /*保存和更新本地数据*/
            if (resulte == null) {
                resulte = new CookieResult(mApi.getUrl(), t.toString(), time);
                CookieSPUtil.getInstance().saveCookie(resulte);
            } else {
                resulte.setResulte(t.toString());
                resulte.setTime(time);
                CookieSPUtil.getInstance().updateCookie(resulte);
            }
        }

        if (mHttpOnNextListener.get() != null) {
            mHttpOnNextListener.get().onNext((String) t, mApi.getMethod());
        }
    }

    /**
     * 对错误进行统一处理 <p>
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        // 需要緩存并且本地有缓存才返回
        if (mApi.isCache()) {
            getCache();             //
        } else {
            errorDo(e);
        }
        dismissProgressDialog();
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        dismissProgressDialog();
    }

    /*--------------------------------- Observer  end ----------------------------------*/




    /**
     * 初始化加载框
     * @param cancel 是否在 -- 取消"进度框"，同时取消"订阅"
     */
    private void initProgressDialog(boolean cancel) {
        Context context = mContext.get();
        if (pd == null && context != null) {
            pd = new ProgressDialog(context);
            pd.setCancelable(cancel);
            if (cancel) {
                pd.setOnCancelListener(new DialogInterface.OnCancelListener() { // 取消"进度框"事件
                    @Override
                    public void onCancel(DialogInterface dialogInterface) {
                        // 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
                        ProgressObserver.this.onCancel();
                    }
                });
            }
        }
    }



    /**
     * 获取cache数据 -- 一般是在出错的时候获取缓存
     */
    private void getCache() {

        Observable.just(mApi.getUrl())
                .subscribe(new Observer<String>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(String s) {
                        // 获取缓存数据
                        CookieResult cookieResulte = CookieSPUtil.getInstance().queryCookieBy(s);

                        if (cookieResulte == null) {
                            throw new HttpTimeException(HttpTimeException.NO_CHACHE_ERROR);     // 抛无缓存异常
                        }

                        long time = (System.currentTimeMillis() - cookieResulte.getTime()) / 1000;

                        // 判断"是否超时" -- 无网络 -- 的情况下本地缓存时间 -- 默认30天
                        if (time < mApi.getCookieNoNetWorkTime()) {                /* 没有超过"缓存时间" */
                            if (mHttpOnNextListener.get() != null) {
                                mHttpOnNextListener.get().onNext(cookieResulte.getResulte(), mApi.getMethod());
                            }
                        } else {                                                   /* 超过缓存时间 */
                            CookieSPUtil.getInstance().deleteCookie(cookieResulte);                 // 删除记录
                            throw new HttpTimeException(HttpTimeException.CHACHE_TIMEOUT_ERROR);    // 抛缓存过时异常
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        errorDo(e);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }


    /**
     * 错误统一处理
     *
     * @param e
     */
    private void errorDo(Throwable e) {
        Context context = mContext.get();
        if (context == null)
            return;
        HttpOnNextListener httpOnNextListener = mHttpOnNextListener.get();

        if (httpOnNextListener == null)
            return;

        if (e instanceof ApiException) {
            httpOnNextListener.onError((ApiException) e, mApi.getMethod());

        } else if (e instanceof HttpTimeException) {
            HttpTimeException exception = (HttpTimeException) e;
            httpOnNextListener.onError(new ApiException(exception, CodeException.RUNTIME_ERROR, exception.getMessage()), mApi.getMethod());
        } else {
            httpOnNextListener.onError(new ApiException(e, CodeException.UNKNOWN_ERROR, e.getMessage()), mApi.getMethod());
        }
    }
}