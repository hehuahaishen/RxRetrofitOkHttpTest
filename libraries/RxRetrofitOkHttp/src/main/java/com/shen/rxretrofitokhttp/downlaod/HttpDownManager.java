package com.shen.rxretrofitokhttp.downlaod;

import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import com.shen.rxretrofitokhttp.downlaod.DownLoadListener.DownloadInterceptor;
import com.shen.rxretrofitokhttp.exception.HttpTimeException;
import com.shen.rxretrofitokhttp.exception.RetryWhenNetworkException;
import com.shen.rxretrofitokhttp.observer.ProgressDownObserver;
import com.shen.rxretrofitokhttp.utils.DownSPUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Retrofit;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import static com.shen.rxretrofitokhttp.utils.AppUtil.getBasUrl;

/**
 * http下载处理类
 *
 */
public class HttpDownManager {

    /** 记录下载数据*/
    private Set<DownInfo> mDownInfos;
    /** 回调sub队列 */
    private HashMap<String, ProgressDownObserver> mObserverMap;
    /** 单利对象*/
    private volatile static HttpDownManager INSTANCE;
    /** 数据库类*/
    private DownSPUtil db;

    private HttpDownManager() {
        mDownInfos = new HashSet<>();
        mObserverMap = new HashMap<>();
        db = DownSPUtil.getInstance();
    }

    /**
     * 获取单例
     *
     * @return
     */
    public static HttpDownManager getInstance() {
        if (INSTANCE == null) {
            synchronized (HttpDownManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpDownManager();
                }
            }
        }
        return INSTANCE;
    }


    /**
     * 开始下载
     */
    public void startDown(final DownInfo info) {
        /*正在下载不处理*/
        if (info == null || mObserverMap.get(info.getUrl()) != null) {
            mObserverMap.get(info.getUrl()).setDownInfo(info);
            return;
        }

        /*添加回调处理类*/
        ProgressDownObserver downObserver = new ProgressDownObserver(info);
        /*记录回调sub*/
        mObserverMap.put(info.getUrl(), downObserver);

        /*获取service，多次请求公用一个sercie*/
        HttpDownService httpService;
        if (mDownInfos.contains(info)) {                 // 包含
            httpService = info.getService();
        } else {
            DownloadInterceptor interceptor = new DownloadInterceptor(downObserver);
            OkHttpClient.Builder builder = new OkHttpClient.Builder();
            //手动创建一个OkHttpClient并设置超时时间
            builder.connectTimeout(info.getConnectonTime(), TimeUnit.SECONDS);
            builder.addInterceptor(interceptor);

            Retrofit retrofit = new Retrofit.Builder()
                    .client(builder.build())
                    .addConverterFactory(ScalarsConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .baseUrl(getBasUrl(info.getUrl()))
                    .build();

            httpService = retrofit.create(HttpDownService.class);
            info.setService(httpService);
            mDownInfos.add(info);
        }

        /*得到rx对象-上一次下載的位置開始下載*/
        httpService.download("bytes=" + info.getReadLength() + "-", info.getUrl())
                .subscribeOn(Schedulers.io())                   // 指定上部分运行线程
                .unsubscribeOn(Schedulers.io())                 // 解除绑定的线程
                .retryWhen(new RetryWhenNetworkException())     // 失败后的retry配置
                .map(new Function<ResponseBody, DownInfo>() {
                         @Override
                         public DownInfo apply(ResponseBody responseBody) throws Exception {
                            writeCaches(responseBody, new File(info.getSavePath()), info);
                            return info;
                         }
                     })
                .observeOn(AndroidSchedulers.mainThread())      // 指定下部分运行的线程 -- 回调线程
                .subscribe(downObserver);                         /* 数据回调 */

    }


    /**
     * 停止下载
     */
    public void stopDown(DownInfo info) {
        if (info == null) return;
        info.setState(DownState.STOP);
        info.getListener().onStop();
        if (mObserverMap.containsKey(info.getUrl())) {
            ProgressDownObserver downObserver = mObserverMap.get(info.getUrl());
            downObserver.onCancel();
            mObserverMap.remove(info.getUrl());
        }

        db.save(info);                              /*保存数据库信息和本地文件*/
    }


    /**
     * 暂停下载
     *
     * @param info
     */
    public void pause(DownInfo info) {
        if (info == null)
            return;
        info.setState(DownState.PAUSE);
        info.getListener().onPuase();
        if (mObserverMap.containsKey(info.getUrl())) {
            ProgressDownObserver downObserver = mObserverMap.get(info.getUrl());
            downObserver.onCancel();
            mObserverMap.remove(info.getUrl());
        }

        db.update(info);   /*这里需要讲info信息写入到数据中，可自由扩展，用自己项目的数据库*/
    }

    /**
     * 停止全部下载
     */
    public void stopAllDown() {
        for (DownInfo downInfo : mDownInfos) {
            stopDown(downInfo);
        }
        mObserverMap.clear();
        mDownInfos.clear();
    }

    /**
     * 暂停全部下载
     */
    public void pauseAll() {
        for (DownInfo downInfo : mDownInfos) {
            pause(downInfo);
        }
        mObserverMap.clear();
        mDownInfos.clear();
    }


    /**
     * 返回全部正在下载的数据
     *
     * @return
     */
    public Set<DownInfo> getDownInfos() {
        return mDownInfos;
    }

    /**
     * 移除下载数据
     *
     * @param info
     */
    public void remove(DownInfo info) {
        mObserverMap.remove(info.getUrl());
        mDownInfos.remove(info);
    }


    /**
     * 写入文件
     *
     * @param file
     * @param info
     * @throws IOException
     */
    public void writeCaches(ResponseBody responseBody, File file, DownInfo info) {
        try {
            RandomAccessFile randomAccessFile = null;
            FileChannel channelOut = null;
            InputStream inputStream = null;
            try {
                if (!file.getParentFile().exists())                     // 如果这个文件没有"父目录"
                    file.getParentFile().mkdirs();

                long allLength = 0 == info.getCountLength() ?           // 根据情况：是否是断点下载
                        responseBody.contentLength() :
                        info.getReadLength() + responseBody.contentLength();

                inputStream = responseBody.byteStream();
                randomAccessFile = new RandomAccessFile(file, "rwd");

                channelOut = randomAccessFile.getChannel();
                MappedByteBuffer mappedBuffer = channelOut.map(
                        FileChannel.MapMode.READ_WRITE,                 // 读写
                        info.getReadLength(),                           // start
                        allLength - info.getReadLength());          // end

                byte[] buffer = new byte[1024 * 4];
                int len;
                while ((len = inputStream.read(buffer)) != -1) {
                    mappedBuffer.put(buffer, 0, len);
                }
            } catch (IOException e) {
                throw new HttpTimeException(e.getMessage());
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (channelOut != null) {
                    channelOut.close();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            }
        } catch (IOException e) {
            throw new HttpTimeException(e.getMessage());
        }
    }

}
