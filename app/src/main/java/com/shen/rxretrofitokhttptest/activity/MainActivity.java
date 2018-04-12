package com.shen.rxretrofitokhttptest.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.daimajia.numberprogressbar.NumberProgressBar;
import com.shen.rxretrofitokhttp.exception.ApiException;
import com.shen.rxretrofitokhttp.listener.HttpOnNextListener;
import com.shen.rxretrofitokhttp.listener.upload.ProgressRequestBody;
import com.shen.rxretrofitokhttp.listener.upload.UploadProgressListener;
import com.shen.rxretrofitokhttp.manager.HttpManager;
import com.shen.rxretrofitokhttp.observer.ProgressObserver;
import com.shen.rxretrofitokhttptest.R;
import com.shen.rxretrofitokhttptest.entity.api.SubjectPostApi;
import com.shen.rxretrofitokhttptest.entity.api.UploadApi;
import com.shen.rxretrofitokhttptest.entity.resulte.BaseResultEntity;
import com.shen.rxretrofitokhttptest.entity.resulte.SubjectResulte;
import com.shen.rxretrofitokhttptest.entity.resulte.UploadResulte;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, HttpOnNextListener {

    /** rx+retrofit：统一api方案 -- btn_rx_all */
    Button mBtnRxAll;
    /** retrofit绝对封装 -- btn_rx */
    Button mBtnRx;
    /** rx+retrofit上传 -- btn_rx_uploade */
    Button mBtnRxUploade;
    /** 多任务断点续传下载 -- btn_rx_mu_down */
    Button mBtnRxMuDown;

    /** 显示下载的 文本数据  */
    private TextView tvMsg;
    /** 进度条 -- 带数字的 */
    private NumberProgressBar progressBar;
    /** 显示下载的图片 */
    private ImageView img;

    /** 公用一个HttpManager */
    private HttpManager manager;
    /** post请求接口信息 */
    private SubjectPostApi mSubjectPostApi;
    /** 上传接口信息 */
    private UploadApi mUploadApi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvMsg = (TextView) findViewById(R.id.tv_msg);

        mBtnRxAll = (Button) findViewById(R.id.btn_rx_all);
        mBtnRx = (Button) findViewById(R.id.btn_rx);
        mBtnRxUploade = (Button) findViewById(R.id.btn_rx_uploade);
        mBtnRxMuDown = (Button) findViewById(R.id.btn_rx_mu_down);

        mBtnRxAll.setOnClickListener(this);
        mBtnRx.setOnClickListener(this);
        mBtnRxUploade.setOnClickListener(this);
        mBtnRxMuDown.setOnClickListener(this);

        //-------------------------------------------------------------------------------

        img = (ImageView) findViewById(R.id.img);
        progressBar = (NumberProgressBar) findViewById(R.id.number_progress_bar);

        /*初始化数据*/
        //manager = new HttpManager(this, this);
        manager = new HttpManager();

        //--------------------------------- retrofit绝对封装 ---------------------------------------
        mSubjectPostApi = new SubjectPostApi();
        mSubjectPostApi.setAll(true);


        /*上传接口内部接口有token验证，所以需要换成自己的接口测试，检查file文件是否手机存在*/
        mUploadApi = new UploadApi();
        File file = new File("/storage/emulated/0/Shen/CacheImage.jpg");
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/jpeg"), file);

        MultipartBody.Part part = MultipartBody.Part.createFormData("file_name", file.getName(),
                new ProgressRequestBody(requestBody, new UploadProgressListener() {

                    @Override
                    public void onProgress(final long currentBytesCount, final long totalBytesCount) {

                        /*回到主线程中，可通过timer等延迟或者循环避免快速刷新数据*/
                        Observable.just(currentBytesCount)
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe(new Observer<Long>() {
                                               @Override
                                               public void onSubscribe(Disposable d) {

                                               }

                                               @Override
                                               public void onNext(Long aLong) {
                                                    tvMsg.setText("提示:上传中");
                                                    progressBar.setMax((int) totalBytesCount);
                                                    progressBar.setProgress((int) currentBytesCount);
                                               }

                                               @Override
                                               public void onError(Throwable e) {

                                               }

                                               @Override
                                               public void onComplete() {

                                               }
                                           });

                    }
                }));

        mUploadApi.setPart(part);
    }



    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.btn_rx_all:                               // rx+retrofit：统一api方案
                Intent intentC = new Intent(this, CombinApiActivity.class);
                startActivity(intentC);
                break;

            case R.id.btn_rx:                                   // retrofit绝对封装
                manager.start(mSubjectPostApi, new ProgressObserver(mSubjectPostApi,
                        MainActivity.this, MainActivity.this));
                break;

            case R.id.btn_rx_uploade:                           // rx+retrofit上传
                manager.start(mUploadApi, new ProgressObserver(mUploadApi,
                                MainActivity.this, MainActivity.this));
                break;

            case R.id.btn_rx_mu_down:                           // 多任务断点续传下载
                Intent intent = new Intent(this, DownLaodActivity.class);
                startActivity(intent);
                break;

        }
    }


    //---------------------- interface HttpOnNextListener --- start   ----------------------
    @Override
    public void onNext(String resulte, String mothead) {

        // post返回处理
        if (mothead.equals(mSubjectPostApi.getMethod())) {

            // TypeReference 反序列化
            BaseResultEntity<ArrayList<SubjectResulte>> subjectResulte =
                    JSONObject.parseObject(resulte, new TypeReference<BaseResultEntity<ArrayList<SubjectResulte>>>() {});

            tvMsg.setText("post返回：\n" + subjectResulte.getData().toString());
        }

        // 上传返回处理
        if (mothead.equals(mUploadApi.getMethod())) {

            // TypeReference 反序列化
            BaseResultEntity<UploadResulte> subjectResulte =
                    JSONObject.parseObject(resulte, new TypeReference<BaseResultEntity<UploadResulte>>(){});

            UploadResulte uploadResulte = subjectResulte.getData();

            tvMsg.setText("上传成功返回：\n" + uploadResulte.getHeadImgUrl());
            Glide.with(MainActivity.this).load(uploadResulte.getHeadImgUrl()).skipMemoryCache(true).into(img);
        }
    }

    @Override
    public void onError(ApiException e, String method) {
        tvMsg.setText("失败："+method+"\ncode=" + e.getCode() + "\nmsg:" + e.getDisplayMessage());
    }

    //---------------------- interface HttpOnNextListener --- end   ----------------------
}
