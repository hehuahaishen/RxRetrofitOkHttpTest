package com.shen.rxretrofitokhttp.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Base64;

import com.shen.rxretrofitokhttp.RxRetrofitApp;
import com.shen.rxretrofitokhttp.downlaod.DownInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 断点续传                 <p>
 * 数据库工具类-geendao运用
 *
 */

public class DownSPUtil {

    private static DownSPUtil sp;
    private Context mContext;


    public DownSPUtil() {
        mContext = RxRetrofitApp.getApplication();
    }


    /**
     * 获取单例
     * @return
     */
    public static DownSPUtil getInstance() {
        if (sp == null) {
            synchronized (DownSPUtil.class) {
                if (sp == null) {
                    sp = new DownSPUtil();
                }
            }
        }
        return sp;
    }


    public void save(DownInfo info){
        SPUtils.put(mContext, info.getUrl(), info);
    }

    public void update(DownInfo info){
        SPUtils.put(mContext, info.getUrl(), info);
    }

    public void deleteDowninfo(DownInfo info){
        SPUtils.remove(mContext, info.getUrl());
    }


    public DownInfo queryDownBy(String  url) {
        DownInfo into = new DownInfo();
        DownInfo intoTemp = (DownInfo) SPUtils.get(mContext, url, into);
        return intoTemp;
    }

    public List<DownInfo> queryDownAll() {
        List<DownInfo> list = new ArrayList<>();

        Map<String, ?> map = SPUtils.getAll(mContext);

        for(Map.Entry<String, ?> entry : map.entrySet()){
            String key = entry.getKey();
            // Object o= entry.getValue();
            String value = (String) entry.getValue();

            DownInfo downInfo = payDownInfo(value);
            if(downInfo != null){
                list.add(downInfo);
            }
        }

        return list;
    }

    private DownInfo payDownInfo(String value){
        Object obj = null;
        ByteArrayInputStream bais = null;
        ObjectInputStream ois = null;
        try {
            String base64 = value;
            if (base64.equals("")) {
                return null;
            }
            @SuppressLint({"NewApi", "LocalSuppress"})
            byte[] base64Bytes = Base64.decode(base64.getBytes(), 1);
            bais = new ByteArrayInputStream(base64Bytes);
            ois = new ObjectInputStream(bais);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
            obj = null;
        }finally {
            try {
                if(bais != null)
                    bais.close();
                if(ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return (DownInfo) obj;
    }
}
