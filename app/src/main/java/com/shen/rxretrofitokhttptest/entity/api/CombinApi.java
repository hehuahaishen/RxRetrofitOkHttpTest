package com.shen.rxretrofitokhttptest.entity.api;



import com.shen.rxretrofitokhttp.api.BaseApi;
import com.shen.rxretrofitokhttp.test.server.HttpUploadService;
import com.shen.rxretrofitokhttptest.HttpPostService;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;

/**
 * 多api共存方案
 */

public class CombinApi extends BaseApi {

//    public CombinApi(HttpOnNextListener onNextListener, AppCompatActivity appCompatActivity) {
//        super(onNextListener, appCompatActivity);
//        /*统一设置*/
//        setCache(true);
//    }
//
//    public CombinApi(HttpOnNextSubListener onNextSubListener, AppCompatActivity appCompatActivity) {
//        super(onNextSubListener, appCompatActivity);
//        /*统一设置*/
//        setCache(true);
//    }

    private boolean mAll = false;

    public CombinApi(){

    }

    /**
     * post请求演示
     * api-1
     *
     * @param all 参数
     */
    public void postApi(final boolean all) {
        /*也可单独设置请求，会覆盖统一设置*/
        setCache(false);
        setMethod("AppFiftyToneGraph/videoLink");
        mAll = all;
    }

    /**
     * post请求演示
     * api-2
     *
     * @param all 参数
     */
    public void postApiOther(boolean all) {
        setCache(true);
        setMethod("AppFiftyToneGraph/videoLink");
        mAll = all;
    }


    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpService = retrofit.create(HttpPostService.class);
        return httpService.getAllVedioBy(mAll);
    }
}
