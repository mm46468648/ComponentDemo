package com.mooc.studyproject.followup;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
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

/**
 * Created by huangzuoliang on 2017/9/15
 * 跟读上传音频加载弹窗
 */

public class FollowUpPopW {
    private PopupWindow mPopupWindow;
    Context mContext;
    ConstraintLayout container;
    ProgressBar progressBar;
    TextView tvProgres;


    public FollowUpPopW(Context context) {
        mContext = context;
        initView();
        initData();
        initListener();
        initPop();
    }

    @SuppressLint("InflateParams")
    private void initView() {
        container = (ConstraintLayout) LayoutInflater.from(mContext).inflate(R.layout.studyproject_pop_upload_loading, null);

        progressBar = container.findViewById(R.id.progress_bar);
        tvProgres = container.findViewById(R.id.tv_progress);
        ImageView ivGif = container.findViewById(R.id.gif);
        Glide.with(mContext).asGif().load(R.drawable.studyproject_gif_uploadaudio_loading).into(ivGif);

    }


    private void initData() {

    }

    private void initListener() {


    }

    public void refreshProgress(int progress) {
        Log.i("huang", "progress:" + progress);
        progressBar.setProgress(progress);
        tvProgres.setText("已上传" + progress + "%");
        tvProgres.invalidate();
    }

    public void setPopDismissClickListener(OnPopDismissClickListener signSuccessClickListener) {
        this.signSuccessClickListener = signSuccessClickListener;
    }

    private OnPopDismissClickListener signSuccessClickListener;

    public void dismiss() {
        if (mPopupWindow != null) {
            mPopupWindow.dismiss();
        }
    }

    public interface OnPopDismissClickListener {
        void onSignPopDismiss();
    }

    public void setTips() {

    }

    public boolean isShowing() {
        return mPopupWindow.isShowing();
    }

    public void show() {
        if (mPopupWindow == null) {
            initPop();
        }
        mPopupWindow.showAtLocation(container, Gravity.CENTER, 0, 0);
    }


    private void initPop() {
        mPopupWindow = new PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, false);
        mPopupWindow.setContentView(container);
//        ColorDrawable drawable = (ColorDrawable) mContext.getResources().getDrawable(R.color.transparent50);
        mPopupWindow.setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        mPopupWindow.setOutsideTouchable(true);

    }

}
