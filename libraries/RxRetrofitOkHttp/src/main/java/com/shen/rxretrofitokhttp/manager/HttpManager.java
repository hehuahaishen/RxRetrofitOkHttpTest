package com.shen.rxretrofitokhttp.manager;

import android.util.Log;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.shen.rxretrofitokhttp.RxRetrofitApp;
import com.shen.rxretrofitokhttp.api.BaseApi;
import com.shen.rxretrofitokhttp.exception.RetryWhenNetworkException;
import com.shen.rxretrofitokhttp.func.ExceptionFunc;
import com.shen.rxretrofitokhttp.func.ResultFunc;
import com.shen.rxretrofitokhttp.interceptor.HttpCommonInterceptor;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 *
 */
public class HttpManager {

    private enum Type{
        STRING,
        GSON
    }

    public HttpManager(){

    }

    public void start(BaseApi basePar, Observer observer){
        getObservable(basePar, Type.STRING)
                .onErrorResumeNext(new ExceptionFunc())             // 异常处理  -- 有异常才触发???
                .map(new ResultFunc())                              // 返回数据统一判断
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())                     // 取消订阅？
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);                               // 订阅
    }


    public void startRetry(BaseApi basePar, Observer observer){
        getObservable(basePar, Type.STRING)
                .retryWhen(new RetryWhenNetworkException(           // 重试
                                basePar.getRetryCount(),
                                basePar.getRetryDelay(),
                                basePar.getRetryIncreaseDelay()
                        )
                )
                .onErrorResumeNext(new ExceptionFunc())             // 异常处理  -- 有异常才触发???
                .map(new ResultFunc())                              // 返回数据统一判断
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())                     // 取消订阅？
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);                               // 订阅
    }


    public void startGson(BaseApi basePar, Observer observer){
        getObservable(basePar, Type.GSON)
                .onErrorResumeNext(new ExceptionFunc())             // 异常处理  -- 有异常才触发???
                .map(new ResultFunc())                              // 返回数据统一判断
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())                     // 取消订阅？
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);                               // 订阅
    }


    public void startGsonRetry(BaseApi basePar, Observer observer){
        getObservable(basePar, Type.GSON)
                .retryWhen(new RetryWhenNetworkException(           // 重试
                                basePar.getRetryCount(),
                                basePar.getRetryDelay(),
                                basePar.getRetryIncreaseDelay()
                        )
                )
                .onErrorResumeNext(new ExceptionFunc())             // 异常处理  -- 有异常才触发???
                .map(new ResultFunc())                              // 返回数据统一判断
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())                     // 取消订阅？
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);                               // 订阅
    }
    /*-----------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------*/
    /*-----------------------------------------------------------------------------------*/

    /**
     *
     * @param basePar 封装的请求数据
     * @return
     */
    private Observable getObservable(BaseApi basePar, Type type){
        Retrofit retrofit = getRetrofit(basePar, type);
        return basePar.getObservable(retrofit);
    }


    private Retrofit getRetrofit(BaseApi baseApi, Type type){
        // 创建 OKHttpClient
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(baseApi.getConnectionTime(), TimeUnit.SECONDS);  // 连接超时时间
        builder.writeTimeout(baseApi.getWriteTime(), TimeUnit.SECONDS);         // 写操作 超时时间
        builder.readTimeout(baseApi.getReadTime(), TimeUnit.SECONDS);           // 读操作超时时间

        if (RxRetrofitApp.isDebug()) {
            builder.addInterceptor(getHttpLoggingInterceptor());                // 日志输出
        }

        // 添加公共参数拦截器
        HttpCommonInterceptor commonInterceptor = new HttpCommonInterceptor.Builder()
                .addHeaderParams("paltform","android")
                // .addHeaderParams("userToken","1234343434dfdfd3434")
                // .addHeaderParams("userId","123445")
                .build();
        builder.addInterceptor(commonInterceptor);

        Retrofit retrofit = null;
        switch (type){
            case STRING:
                retrofit = new Retrofit.Builder()
                        .client(builder.build())
                        .baseUrl(baseApi.getBaseUrl())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
                break;

            case GSON:
                retrofit = new Retrofit.Builder()
                        .client(builder.build())
                        .baseUrl(baseApi.getBaseUrl())
                        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                break;
        }

//        // 创建Retrofit
//        Retrofit retrofit = new Retrofit.Builder()
//                .client(builder.build())
//                .baseUrl(baseApi.getBaseUrl())
//                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
//                .addConverterFactory(ScalarsConverterFactory.create())
//                .build();

        return retrofit;
    }



    /**
     * 日志输出 <p>
     * 自行判定是否添加
     *
     * @return
     */
    private HttpLoggingInterceptor getHttpLoggingInterceptor() {

        // 日志显示级别
        HttpLoggingInterceptor.Level level = HttpLoggingInterceptor.Level.BODY;
        // 新建log拦截器
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor(new HttpLoggingInterceptor.Logger() {
            @Override
            public void log(String message) {
                Log.d("RxRetrofit", "Retrofit --> Message:" + message);
            }
        });
        loggingInterceptor.setLevel(level);
        return loggingInterceptor;
    }
}
