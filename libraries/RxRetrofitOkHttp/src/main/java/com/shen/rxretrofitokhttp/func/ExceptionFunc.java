package com.shen.rxretrofitokhttp.func;

import android.util.Log;


import com.shen.rxretrofitokhttp.exception.FactoryException;

import io.reactivex.Observable;
import io.reactivex.functions.Function;

/**
 * 异常处理<p>
 *
 * 解析异常--发送异常
 */

public class ExceptionFunc implements Function<Throwable, Observable> {

    @Override
    public Observable apply(Throwable throwable){
        Log.i("shen","-------->"+throwable.getMessage());

        // 解析异常--发送异常
        return Observable.error(FactoryException.analysisExcetpion(throwable));
    }
}
