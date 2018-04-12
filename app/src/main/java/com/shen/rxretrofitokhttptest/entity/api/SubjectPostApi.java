package com.shen.rxretrofitokhttptest.entity.api;

import com.shen.rxretrofitokhttp.api.BaseApi;
import com.shen.rxretrofitokhttptest.HttpPostService;

import io.reactivex.Observable;
import retrofit2.Retrofit;

/**
 * 测试数据
 *
 */
public class SubjectPostApi extends BaseApi {
    //    接口需要传入的参数 可自定义不同类型
    private boolean all;
    /*任何你先要传递的参数*/
//    String xxxxx;
//    String xxxxx;
//    String xxxxx;
//    String xxxxx;


    /**
     * 默认初始化需要给定初始设置
     * 可以额外设置请求设置加载框显示，回调等（可扩展）
     * 设置可查看BaseApi
     */
    public SubjectPostApi() {
        setCache(false);
        setMethod("AppFiftyToneGraph/videoLink");
    }


    public boolean isAll() {
        return all;
    }

    public void setAll(boolean all) {
        this.all = all;
    }


    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpPostService httpService = retrofit.create(HttpPostService.class);
        return httpService.getAllVedioBy(isAll());
    }

}
