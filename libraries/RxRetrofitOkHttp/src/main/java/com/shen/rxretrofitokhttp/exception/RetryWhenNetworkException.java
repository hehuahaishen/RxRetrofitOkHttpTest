package com.shen.rxretrofitokhttp.exception;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import io.reactivex.Observable;
import io.reactivex.functions.BiFunction;
import io.reactivex.functions.Function;


/**
 * retry条件 -- 重新请求（失败了重新请求）
 *
 */
public class RetryWhenNetworkException implements Function<Observable<? extends Throwable>, Observable<?>> {

    /** retry次数 */
    private int mCount = 1;
    /** 延迟 */
    private long mDelay = 100;
    /** 叠加延迟 */
    private long mIncreaseDelay = 100;


    public RetryWhenNetworkException() {

    }

    public RetryWhenNetworkException(int count, long delay) {
        mCount = count;
        mDelay = delay;
    }

    /**
     *
     * @param count                 retry次数
     * @param delay                 延迟
     * @param increaseDelay         叠加延迟
     */
    public RetryWhenNetworkException(int count, long delay, long increaseDelay) {
        mCount = count;
        mDelay = delay;
        mIncreaseDelay = increaseDelay;
    }


    // 这里实现重试 -- 并传递异常
    @Override
    public Observable<?> apply(Observable<? extends Throwable> observable) {
        //---------------------------------------------------------------
            /** FlatMap将--"一个发送事件的上游Observable"变换为 -- "多个发送事件的Observable"，
                然后将它们发射的事件合并后放进一个单独的Observable里. */
        //---------------------------------------------------------------
            /** Range(n, m)
                range操作符的作用:
                    Range操作符根据出入的"初始值n"和"数目m"发射一系列大于等于n的m个值。 */
        //---------------------------------------------------------------

        return observable
                .zipWith(Observable.range(1, mCount + 1), new BiFunction<Throwable, Integer, Wrapper>() {
                    // Observable.range(1, mCount + 1) 发送 "mCount+1次"
                    @Override
                    public Wrapper apply(Throwable throwable, Integer integer) throws Exception {
                        return new Wrapper(throwable, integer);
                    }

                }).flatMap(new Function<Wrapper, Observable<?>>() {
                    @Override
                    public Observable<?> apply(Wrapper wrapper) throws Exception {
                        if ((wrapper.throwable instanceof ConnectException
                                || wrapper.throwable instanceof SocketTimeoutException
                                || wrapper.throwable instanceof TimeoutException)
                                && wrapper.index < mCount + 1) { //如果超出重试次数也抛出错误，否则默认是会进入onCompleted

                            return Observable.timer(mDelay + (wrapper.index - 1) * mIncreaseDelay,
                                    TimeUnit.MILLISECONDS);
                        }

                        return Observable.error(wrapper.throwable);
                    }
                });
    }


    /**
     * Throwable 包装 -- 封装
     */
    private class Wrapper {

        /** 第几次重新请求 */
        private int index;
        private Throwable throwable;

        public Wrapper(Throwable throwable, int index) {
            this.index = index;
            this.throwable = throwable;
        }
    }

}
