package com.shen.rxretrofitokhttptest;

import android.app.Application;
import android.content.Context;

import com.shen.rxretrofitokhttp.RxRetrofitApp;

/**
 * Created by WZG on 2016/10/25.
 */

public class MyApplication extends Application{
    public static Context app;

    @Override
    public void onCreate() {
        super.onCreate();
        app=getApplicationContext();
        RxRetrofitApp.init(this,BuildConfig.DEBUG);
    }
}
