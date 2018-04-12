package com.shen.rxretrofitokhttp.exception;

/**
 * 运行时自定义错误信息           <p>
 * 自由添加错误，需要自己扩展
 */
public class HttpTimeException extends RuntimeException {

    /** 网络错误 未知错误 */
    public static final int UNKOWN_ERROR = 0x1002;
    /** 本地无缓存错误 */
    public static final int NO_CHACHE_ERROR = 0x1003;
    /** 缓存过时错误 */
    public static final int CHACHE_TIMEOUT_ERROR = 0x1004;


    /**
     * 构造函数 -- 如果传过来的是 -- int错误码 -- 就要转转成字符串
     * @param resultCode
     */
    public HttpTimeException(int resultCode) {
        super(getApiExceptionMessage(resultCode));
    }

    /**
     * 构造函数
     * @param detailMessage
     */
    public HttpTimeException(String detailMessage) {
        super(detailMessage);
    }

    /**
     * 转换错误数据
     *
     * @param code
     * @return
     */
    private static String getApiExceptionMessage(int code) {
        switch (code) {
            case UNKOWN_ERROR:
                return "错误：网络错误";
            case NO_CHACHE_ERROR:
                return "错误：无缓存数据";
            case CHACHE_TIMEOUT_ERROR:
                return "错误：缓存数据过期";
            default:
                return "错误：未知错误";
        }
    }
}

