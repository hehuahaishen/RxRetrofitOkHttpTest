package com.shen.rxretrofitokhttp.http;


import io.reactivex.functions.Function;

/**
 *
 * 剥离 最终数据
 */

public class PayLoad<T> implements Function<BaseResponse<T>, T> {
    @Override
    public T apply(BaseResponse<T> tBaseResponse) { //获取数据失败时，包装一个Fault 抛给上层处理错误
        if(!tBaseResponse.isSuccess()){
            throw new Fault(tBaseResponse.status, tBaseResponse.message);
        }

        return tBaseResponse.data;
    }
}


//public class PayLoad<T> implements Func1<BaseResponse<T>,T> {
//    @Override
//    public T call(BaseResponse<T> tBaseResponse) { //获取数据失败时，包装一个Fault 抛给上层处理错误
//        if(!tBaseResponse.isSuccess()){
//            throw new Fault(tBaseResponse.status, tBaseResponse.message);
//        }
//
//        return tBaseResponse.data;
//    }
//}