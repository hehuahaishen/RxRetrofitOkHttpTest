package com.shen.rxretrofitokhttptest.entity.resulte;

/**
 * 回调信息统一封装类
 *
 */
public class BaseResultEntity<T>{
    /** 判断标示*/
    private int ret;
    /** 提示信息*/
    private String msg;
    /** 显示数据（用户需要关心的数据）*/
    private T data;



    //-------------------------------------------------------------

    /** 判断标示*/
    public int getRet() {
        return ret;
    }
    /** 判断标示*/
    public void setRet(int ret) {
        this.ret = ret;
    }

    /** 提示信息*/
    public String getMsg() {
        return msg;
    }
    /** 提示信息*/
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /** 显示数据（用户需要关心的数据）*/
    public T getData() {
        return data;
    }
    /** 显示数据（用户需要关心的数据）*/
    public void setData(T data) {
        this.data = data;
    }
}
