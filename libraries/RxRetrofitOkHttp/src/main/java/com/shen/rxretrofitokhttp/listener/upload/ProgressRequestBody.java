package com.shen.rxretrofitokhttp.listener.upload;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

/**
 * 自定义回调加载速度类RequestBody --  extends RequestBody
 *
 * 上传的参数吧 -- 如：文件
 */
public class ProgressRequestBody extends RequestBody {

    /** 实际起作用的RequestBody -- mDelegate:委托 -- */
    private RequestBody mDelegate;
    /** 进度回调接口*/
    private final UploadProgressListener mUploadProgressListener;

    /** Sink -- 接收者 */
    private CountingSink mCountingSink;


    public ProgressRequestBody(RequestBody requestBody, UploadProgressListener uploadProgressListener) {
        mDelegate = requestBody;
        mUploadProgressListener = uploadProgressListener;
    }

    @Override
    public MediaType contentType() {
        return mDelegate.contentType();  // 获取这个 (RequestBody) 要上传的类型(头) -- 如："Content-Type: application/json"
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {

        mCountingSink = new CountingSink(sink);
        //将CountingSink转化为BufferedSink供writeTo()使用
        BufferedSink bufferedSink = Okio.buffer(mCountingSink);

        mDelegate.writeTo(bufferedSink);         // 上传东西
        bufferedSink.flush();                    // 刷新下
    }

    /**
     * 返回文件 -- 总的字节大小               <p>
     * 如果文件大小获取失败则返回-1
     * @return
     */
    @Override
    public long contentLength(){
        try {
            return mDelegate.contentLength();
        } catch (IOException e) {
            return -1;
        }
    }


    /*------------------------  CountingSink extends ForwardingSink --------------------*/
    /**
     * 一个具有委托功能的抽象类ForwardingSink和ForwardingSource
     *
     * 这个主要作用 -- 方便进度条更新(计算)
     *
     * Sink -- 接收者
     *
     * 上传的数据是经过这里的!!!!
     */
    protected final class CountingSink extends ForwardingSink {

        /** 一共上传了多少 */
        private long byteWritten;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        /**
         * 上传时调用该方法,在其中调用回调函数将上传进度暴露出去,该方法提供了缓冲区的自己大小
         *
         * @param source
         * @param byteCount
         * @throws IOException
         */
        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);

            byteWritten += byteCount;
            mUploadProgressListener.onProgress(byteWritten, contentLength());
        }
    }


}