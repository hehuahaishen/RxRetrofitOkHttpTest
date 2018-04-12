package com.shen.rxretrofitokhttp;

import android.app.Application;

/**
 * 全局app
 *
 * new 要传递 -- Application
 *
 * 主要作用 -- 判断网络是否连接 -- 拿到上下文 Context
 */
public class RxRetrofitApp  {

    private static Application application;
    private static boolean debug = false;


    public static void init(Application app){
        setApplication(app);
        setDebug(true);
    }

    public static void init(Application app, boolean debug){
        setApplication(app);
        setDebug(debug);
    }

    public static Application getApplication() {
        return application;
    }

    private static void setApplication(Application application) {
        RxRetrofitApp.application = application;
    }

    public static boolean isDebug() {
        return debug;
    }

    public static void setDebug(boolean debug) {
        RxRetrofitApp.debug = debug;
    }
}

/*   在app的全局变量中必须设置这个!!!!! 全局 -- Application  */
//    public class MyApplication extends Application{
//        public static Context app;
//
//        @Override
//        public void onCreate() {
//            super.onCreate();
//            app=getApplicationContext();
//            RxRetrofitApp.init(this,BuildConfig.DEBUG);
//        }
//    }