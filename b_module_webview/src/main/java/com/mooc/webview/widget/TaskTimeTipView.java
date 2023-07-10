package com.mooc.webview.widget;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.webview.R;
import com.qmuiteam.qmui.nestedScroll.IQMUIContinuousNestedTopView;

public class TaskTimeTipView extends FrameLayout {
    Context mContext;
    public TaskTimeTipView(@NonNull Context context) {
        super(context);

        mContext = context;
        _init();
    }

    public TaskTimeTipView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mContext = context;
        _init();
    }

    public TaskTimeTipView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;
        _init();
    }

    void _init(){
        View.inflate(mContext, R.layout.webview_layout_task_tip,this);

    }
}
