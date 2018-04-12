package com.shen.rxretrofitokhttp.test;

import com.shen.rxretrofitokhttp.api.BaseApi;
import com.shen.rxretrofitokhttp.test.server.HttpUploadService;

import io.reactivex.Observable;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Retrofit;


/**
 * 上传请求api
 */

public class UploadStatisticsApi extends BaseApi {

    /** 需要上传的文件*/
    private String mJson;

    public UploadStatisticsApi() {
        // 设置缓存 -- 这时候就要设置 -- 请求Url(用于作为 -- SP的KEY)
        setCache(true);
        setMethod(HttpUploadService.UploadStatisticsUrl);
    }

    public String getJson(){
        return mJson;
    }

    public void setJson(String json){
        mJson = json;
    }


    @Override
    public Observable getObservable(Retrofit retrofit) {
        HttpUploadService httpService = retrofit.create(HttpUploadService.class);
        RequestBody json= RequestBody.create(MediaType.parse("application/json; charset=utf-8"), mJson);
        return httpService.uploadStatistics(json);
    }


}
