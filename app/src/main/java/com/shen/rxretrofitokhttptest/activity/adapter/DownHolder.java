package com.shen.rxretrofitokhttptest.activity.adapter;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.jude.easyrecyclerview.adapter.BaseViewHolder;
import com.shen.rxretrofitokhttp.downlaod.DownInfo;
import com.shen.rxretrofitokhttp.downlaod.DownState;
import com.shen.rxretrofitokhttp.downlaod.HttpDownManager;
import com.shen.rxretrofitokhttp.listener.HttpDownOnNextListener;
import com.shen.rxretrofitokhttptest.R;

/**
 * 下载item
 *
 */

public class DownHolder extends BaseViewHolder<DownInfo> implements View.OnClickListener{
    private TextView tvMsg;
    private NumberProgressBar progressBar;
    private DownInfo apkApi;
    private HttpDownManager manager;

    public DownHolder(ViewGroup parent) {
        super(parent, R.layout.view_item_holder);

        manager= HttpDownManager.getInstance();
        $(R.id.btn_rx_down).setOnClickListener(this);
        $(R.id.btn_rx_pause).setOnClickListener(this);
        progressBar=$(R.id.number_progress_bar);
        tvMsg=$(R.id.tv_msg);
    }

    @Override
    public void setData(DownInfo data) {
        super.setData(data);
        data.setListener(httpProgressOnNextListener);
        apkApi=data;
        progressBar.setMax((int) apkApi.getCountLength());
        progressBar.setProgress((int) apkApi.getReadLength());
        /*第一次恢复 */
        switch (apkApi.getState()){
            case START:
                /*起始状态*/
                break;
            case PAUSE:
                tvMsg.setText("暂停中");
                break;
            case DOWN:
                manager.startDown(apkApi);
                break;
            case STOP:
                tvMsg.setText("下载停止");
                break;
            case ERROR:
                tvMsg.setText("下載錯誤");
                break;
            case  FINISH:
                tvMsg.setText("下载完成");
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_rx_down:
                if(apkApi.getState()!= DownState.FINISH){
                    manager.startDown(apkApi);
                }
                break;
            case R.id.btn_rx_pause:
                manager.pause(apkApi);
                break;
        }
    }

    /*下载回调*/
    HttpDownOnNextListener<DownInfo> httpProgressOnNextListener = new HttpDownOnNextListener<DownInfo>() {
        @Override
        public void onNext(DownInfo baseDownEntity) {
            tvMsg.setText("提示：下载完成");
            Toast.makeText(getContext(),baseDownEntity.getSavePath(),Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onStart() {
            tvMsg.setText("提示:开始下载");
        }

        @Override
        public void onComplete() {
            tvMsg.setText("提示：下载结束");
        }

        @Override
        public void onError(Throwable e) {
            super.onError(e);
            tvMsg.setText("失败:"+e.toString());
        }


        @Override
        public void onPuase() {
            super.onPuase();
            tvMsg.setText("提示:暂停");
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void updateProgress(long readLength, long countLength) {
            tvMsg.setText("提示:下载中");
            // progressBar.setMax((int) countLength);
            // progressBar.setProgress((int) readLength);
            progressBar.setMax(100);
            progressBar.setProgress(countLength == 0 ? 0 : (int)((readLength * 100 / countLength)));

        }
    };
}