package com.shen.rxretrofitokhttptest;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * 测试接口service-post相关
 */

public interface HttpPostService {


    @GET("AppFiftyToneGraph/videoLink/{once_no}")
    Observable<String> getAllVedioBy(@Query("once_no") boolean once_no);

}
