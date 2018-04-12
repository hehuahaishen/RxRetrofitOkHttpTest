package com.shen.rxretrofitokhttp.downlaod.DownLoadListener;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * 成功回调处理
 */
public class DownloadInterceptor implements Interceptor {

    private DownloadProgressListener mDownloadProgressListener;

    public DownloadInterceptor(DownloadProgressListener listener) {
        mDownloadProgressListener = listener;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Response originalResponse = chain.proceed(chain.request());

        return originalResponse.newBuilder()
                .body(new DownloadResponseBody(originalResponse.body(), mDownloadProgressListener))
                .build();
    }
}
