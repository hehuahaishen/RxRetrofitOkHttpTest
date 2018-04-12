package com.shen.rxretrofitokhttptest.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.shen.rxretrofitokhttp.exception.ApiException;
import com.shen.rxretrofitokhttp.listener.HttpOnNextListener;
import com.shen.rxretrofitokhttptest.R;
import com.shen.rxretrofitokhttptest.entity.api.CombinApi;
import com.shen.rxretrofitokhttptest.entity.resulte.BaseResultEntity;
import com.shen.rxretrofitokhttptest.entity.resulte.SubjectResulte;

import java.util.ArrayList;


/**
 * 统一api类处理方案界面
 *
 */
public class CombinApiActivity extends AppCompatActivity implements HttpOnNextListener {

    private TextView tvMsg;
    CombinApi api;

    /** rx+retrofit：统一api方案:1 -- btn_rx_all */
    Button mBtnRxAll;
    /** rx+retrofit：统一api方案:2 -- btn_rx_all_2 */
    Button mBtnRxAll2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_combin_api);

        // api = new CombinApi(this, this);       // CombinApi
        api = new CombinApi();       // CombinApi

        tvMsg = (TextView) findViewById(R.id.tv_msg);

        mBtnRxAll = (Button) findViewById(R.id.btn_rx_all);
        mBtnRxAll2 = (Button) findViewById(R.id.btn_rx_all_2);

        mBtnRxAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.postApi(true);
            }
        });

        mBtnRxAll2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                api.postApiOther(true);
            }
        });
    }

    //---------------------- interface HttpOnNextListener --- start   ----------------------
    @Override
    public void onNext(String resulte, String method) {
        BaseResultEntity<ArrayList<SubjectResulte>> subjectResulte =
                JSONObject.parseObject(resulte,
                        new TypeReference<BaseResultEntity<ArrayList<SubjectResulte>>>() {
                });
        tvMsg.setText("统一post返回：\n" + subjectResulte.getData().toString());
    }

    @Override
    public void onError(ApiException e, String method) {
        tvMsg.setText("失败："+method+"\ncode=" + e.getCode() + "\nmsg:" + e.getDisplayMessage());
    }
    //---------------------- interface HttpOnNextListener --- end   ----------------------
}
