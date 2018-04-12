package com.shen.rxretrofitokhttp.observer;

import com.shen.rxretrofitokhttp.downlaod.DownInfo;
import com.shen.rxretrofitokhttp.downlaod.DownLoadListener.DownloadProgressListener;
import com.shen.rxretrofitokhttp.downlaod.DownState;
import com.shen.rxretrofitokhttp.downlaod.HttpDownManager;
import com.shen.rxretrofitokhttp.listener.HttpDownOnNextListener;
import com.shen.rxretrofitokhttp.utils.DownSPUtil;

import java.lang.ref.SoftReference;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;


/**
 * 断点下载处理类Subscriber                            <p>
 * 用于在Http请求开始时，自动显示一个ProgressDialog        <br>
 * 在Http请求结束是，关闭ProgressDialog                  <br>
 * 调用者自己对请求数据进行处理                           <br>
 */
public class ProgressDownObserver<T> implements Observer<T>, DownloadProgressListener {
    /** 弱引用结果回调 */
    private SoftReference<HttpDownOnNextListener> mHttpDownOnNextListener;
    /** 下载数据 */
    private DownInfo mDownInfo;

    /** 可用于取消订阅 */
    Disposable mDisposable = null;

    public ProgressDownObserver(DownInfo downInfo) {
        mHttpDownOnNextListener = new SoftReference<>(downInfo.getListener());
        mDownInfo = downInfo;
    }


    public void setDownInfo(DownInfo downInfo) {
        mHttpDownOnNextListener = new SoftReference<>(downInfo.getListener());
        mDownInfo = downInfo;
    }

    /**
     * 取消ProgressDialog的时候，取消对observable的订阅，同时也取消了http请求
     */
    public void onCancel() {
        // isDisposed() -- true:资源被释放了 ; false:资源没被释放了
        if(mDisposable != null && !mDisposable.isDisposed()){
            mDisposable.dispose();
        }
    }



    /**
     * 显示ProgressDialog
     */
    public void onStart() {
        if(mHttpDownOnNextListener.get()!=null){
            mHttpDownOnNextListener.get().onStart();
        }
        mDownInfo.setState(DownState.START);
    }


    @Override
    public void onSubscribe(Disposable d) {
        mDisposable = d;
        onStart();
    }

    /**
     * 将onNext方法中的返回结果交给Activity或Fragment自己处理
     *
     * @param t 创建Subscriber时的泛型类型
     */
    @Override
    public void onNext(T t) {
        if (mHttpDownOnNextListener.get() != null) {
            mHttpDownOnNextListener.get().onNext(t);
        }
    }

    /**
     * 对错误进行统一处理
     * 隐藏ProgressDialog
     *
     * @param e
     */
    @Override
    public void onError(Throwable e) {
        if(mHttpDownOnNextListener.get()!=null){
            mHttpDownOnNextListener.get().onError(e);
        }
        HttpDownManager.getInstance().remove(mDownInfo);
        mDownInfo.setState(DownState.ERROR);
        DownSPUtil.getInstance().update(mDownInfo);
    }

    /**
     * 完成，隐藏ProgressDialog
     */
    @Override
    public void onComplete() {
        if(mHttpDownOnNextListener.get()!=null){
            mHttpDownOnNextListener.get().onComplete();
        }
        HttpDownManager.getInstance().remove(mDownInfo);
        mDownInfo.setState(DownState.FINISH);
        DownSPUtil.getInstance().update(mDownInfo);
    }




    /*--------------------------- DownloadProgressListener  start -----------------------*/
    @Override
    public void update(long read, long count, boolean done) {
        if(mDownInfo.getCountLength() > count){
            read = mDownInfo.getCountLength() - count + read;
        }else{
            mDownInfo.setCountLength(count);
        }
        mDownInfo.setReadLength(read);
        if (mHttpDownOnNextListener.get() != null) {
            /*接受进度消息，造成UI阻塞，如果不需要显示进度可去掉实现逻辑，减少压力*/
            Observable.just(read)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Long>() {
                                   @Override
                                   public void onSubscribe(Disposable d) {
                                   }

                                   @Override
                                   public void onNext(Long aLong) {
                                        /*如果暂停或者停止状态延迟，不需要继续发送回调，影响显示*/
                                        if(mDownInfo.getState() == DownState.PAUSE ||
                                                mDownInfo.getState() == DownState.STOP)
                                            return;

                                        mDownInfo.setState(DownState.DOWN);
                                        mHttpDownOnNextListener.get().updateProgress(aLong, mDownInfo.getCountLength());
                                   }

                                   @Override
                                   public void onError(Throwable e) {
                                   }

                                   @Override
                                   public void onComplete() {
                                   }
                               }
                    );
        }
    }
    /*--------------------------- DownloadProgressListener  end ------------------------*/

}