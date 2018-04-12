package com.shen.rxretrofitokhttp.listener.upload;

/**
 * 上传进度回调类
 *
 */

public interface UploadProgressListener {

    /**
     * 上传进度                 <p>
     * 手动回调到主线程中
     *
     * @param currentBytesCount     当前大小（当前进度）
     * @param totalBytesCount       总大小（总进度）
     */
    void onProgress(long currentBytesCount, long totalBytesCount);
}