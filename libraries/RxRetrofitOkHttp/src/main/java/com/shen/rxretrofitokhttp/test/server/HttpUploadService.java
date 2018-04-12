package com.shen.rxretrofitokhttp.test.server;

import io.reactivex.Observable;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

/**
 * 测试接口service-上传相关
 */

public interface HttpUploadService {

    String UploadImageUrl = "AppYuFaKu/uploadHeadImg";
    String UploadStatisticsUrl = "AddShenServer.do";

    /*上传文件*/
    @Multipart
    @POST(UploadImageUrl)
    Observable<String> uploadImage(@Part("uid") RequestBody uid,
                                   @Part("auth_key") RequestBody auth_key,
                                   @Part MultipartBody.Part file);

    /*上传统计*/
    @Headers("Content-Type: application/json")
    //@Headers({"Content-Type: application/json","Accept: application/json"}) //需要添加头
    @POST(UploadStatisticsUrl)
    Observable<String> uploadStatistics(@Body RequestBody statisticsJson);

}
