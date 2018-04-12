package com.shen.rxretrofitokhttp.downlaod.DownLoadListener;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * 自定义精度的body
 * @author wzg
 */
public class DownloadResponseBody extends ResponseBody {

    private ResponseBody mResponseBody;
    private DownloadProgressListener mDownloadProgressListener;
    private BufferedSource mBufferedSource;

    public DownloadResponseBody(ResponseBody responseBody, DownloadProgressListener progressListener) {
        mResponseBody = responseBody;
        mDownloadProgressListener = progressListener;
    }

    /* 获取这个 (RequestBody) 要上传的类型(头) -- 如："Content-Type: application/json" */
    @Override
    public MediaType contentType() {
        return mResponseBody.contentType();
    }

    /**
     * 返回文件 -- 总的字节大小               <p>
     * 如果文件大小获取失败则返回-1
     * @return
     */
    @Override
    public long contentLength() {
        return mResponseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (mBufferedSource == null) {
            mBufferedSource = Okio.buffer(source(mResponseBody.source()));
        }
        return mBufferedSource;
    }

    /** 下载的数据是经过这里的 */
    private Source source(Source source) {

        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;

                if (null != mDownloadProgressListener) {
                    mDownloadProgressListener.update(totalBytesRead, mResponseBody.contentLength(), bytesRead == -1);
                }
                return bytesRead;
            }
        };

    }
}
