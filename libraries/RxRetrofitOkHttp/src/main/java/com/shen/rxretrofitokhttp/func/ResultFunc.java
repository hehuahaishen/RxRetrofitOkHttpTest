package com.shen.rxretrofitokhttp.func;


import com.shen.rxretrofitokhttp.exception.HttpTimeException;
import io.reactivex.functions.Function;

/**
 * 服务器返回数据判断
 */

public class ResultFunc implements Function<Object,Object> {
    @Override
    public Object apply(Object o) throws Exception {
        if (o == null || "".equals(o.toString())) {
            throw new HttpTimeException("数据错误");
        }
        return o;
    }
}
