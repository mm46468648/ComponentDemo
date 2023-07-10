package com.mooc.resource.widget.pullrefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;

import com.mooc.common.R;
import com.scwang.smart.refresh.layout.api.RefreshHeader;
import com.scwang.smart.refresh.layout.api.RefreshKernel;
import com.scwang.smart.refresh.layout.api.RefreshLayout;
import com.scwang.smart.refresh.layout.constant.RefreshState;
import com.scwang.smart.refresh.layout.constant.SpinnerStyle;
import com.scwang.smart.refresh.layout.util.SmartUtil;

import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

public class MoocPullRefreshHeader extends LinearLayout implements RefreshHeader {

    private GifImageView mProgressView;//刷新动画视图
    private Context context;

    public MoocPullRefreshHeader(Context context) {
        super(context);
        initView(context);
    }

    public MoocPullRefreshHeader(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public MoocPullRefreshHeader(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context);


    }


    @Override
    public View getView() {
        return this;
    }


    @Override
    public SpinnerStyle getSpinnerStyle() {
        return SpinnerStyle.Translate;//指定为平移，不能null
    }

    @Override
    public void setPrimaryColors(int... colors) {

    }

    @Override
    public void onInitialized(RefreshKernel kernel, int height, int maxDragHeight) {
    }

    @Override
    public void onMoving(boolean isDragging, float percent, int offset, int height, int maxDragHeight) {

    }

    @Override
    public void onReleased(RefreshLayout refreshLayout, int height, int maxDragHeight) {

    }

    @Override
    public void onStartAnimator(RefreshLayout refreshLayout, int height, int maxDragHeight) {
//        Glide.with(this).asGif().load(R.drawable.common_pull_refresh_loading).into(mProgressView);
        mProgressView.setImageResource(R.drawable.common_pull_refresh_loading);
    }

    @Override
    public int onFinish(RefreshLayout refreshLayout, boolean success) {
        GifDrawable drawable = (GifDrawable) mProgressView.getDrawable();
        if (drawable != null && drawable.isRunning()) {
            drawable.stop();
        }
        return 500;
    }

    @Override
    public void onHorizontalDrag(float percentX, int offsetX, int offsetMax) {

    }

    @Override
    public boolean isSupportHorizontalDrag() {
        return false;
    }

    @Override
    public void onStateChanged(RefreshLayout refreshLayout, RefreshState oldState, RefreshState newState) {

    }

    private void initView(Context context) {
        this.context = context;
        setGravity(Gravity.CENTER);

        mProgressView = new GifImageView(context);
        mProgressView.setImageResource(R.drawable.common_pull_refresh_loading);
        addView(mProgressView, SmartUtil.dp2px(30), SmartUtil.dp2px(37));
        setMinimumHeight(SmartUtil.dp2px(45));
    }
}
