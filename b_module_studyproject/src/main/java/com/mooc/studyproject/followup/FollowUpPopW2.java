package com.mooc.studyproject.followup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.mooc.studyproject.R;
import com.mooc.studyproject.model.LoopBean;

/**
 * Created by huangzuoliang on 2017/9/15
 * 跟读上传音频加载弹窗
 */

public class FollowUpPopW2 {
    private PopupWindow mPopupWindow;
    Context mContext;
    ConstraintLayout container;
    ProgressBar progressBar;
    TextView tvProgres;
    private String status;

    public static final String STATUS_UPLOAD = "当前音频上传中，请稍候...";
    public static final String STATUS_QUEUE = "当前评测繁忙，排队中，\n请稍候...";
    public static final String STATUS_TEST = "语音正在通过系统智能评测中，\n请稍候...";

    public FollowUpPopW2(Context context) {
        mContext = context;
        initView();
        initData();
        initListener();
        initPop();

    }

    public void setPercent(int percent) {
        progressBar.setProgress(percent);
    }

    @SuppressLint("InflateParams")
    private void initView() {
        container = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.studyproject_pop_upload_loading, null);

        progressBar = container.findViewById(R.id.progress_bar);
        tvProgres = container.findViewById(R.id.tv_progress);
        ImageView ivGif = container.findViewById(R.id.gif);
        Glide.with(mContext).asGif().load(R.drawable.studyproject_gif_uploadaudio_loading).into(ivGif);

    }

    public void setStatusData(LoopBean data){
        if("2".equals(this.status) && "1".equals(data.getStatus())){
            return;
        }

        this.status = data.getStatus();
        if ("1".equals(data.getStatus())) {
            setPorText(STATUS_QUEUE);
            setPercent(data.getPercent());
        } else if ("2".equals(data.getStatus())) {
            setPorText(STATUS_TEST);
            setPercent(data.getPercent());
        }else {
            setPorText(STATUS_TEST);
        }
    }

    public void setPorText(String text) {
        tvProgres.setText(text);
    }


    private void initData() {

    }

    private void initListener() {


    }

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }


    public void show() {
        if (mPopupWindow == null) {
            initPop();
        }

        if(mPopupWindow!=null && mPopupWindow.isShowing()){
            return;
        }
        mPopupWindow.showAtLocation(container, Gravity.CENTER, 0, 0);
    }


    private void initPop() {
        mPopupWindow = new PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setContentView(container);
//        ColorDrawable drawable = (ColorDrawable) mContext.getResources().getDrawable(R.color.transparent50);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
            @Override
            public void onDismiss() {
                reset();
            }
        });

    }

    void reset(){
        setPorText("");
        setPercent(0);
        this.status = "";
    }

}
