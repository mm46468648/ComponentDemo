package com.mooc.resource.widget;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.common.R;;
import com.mooc.resource.ktextention.IntExtentionKt;


public class EmptyView extends LinearLayout {
    private final ImageView iconView;
    private final TextView titleView;
    private final Button actionView;

    public EmptyView(@NonNull Context context) {
        this(context, null);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public EmptyView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER);
        LayoutInflater.from(context).inflate(R.layout.common_layout_empty_view, this, true);

        iconView = findViewById(R.id.empty_icon);
        titleView = findViewById(R.id.empty_text);
        actionView = findViewById(R.id.empty_action);
    }

    public void setEmptyIcon(@DrawableRes int iconRes) {
        iconView.setImageResource(iconRes);
    }

    public void isShowEmptyIcon( boolean iShow) {
        if (iShow){
            iconView.setVisibility(VISIBLE);
        }else {
            iconView.setVisibility(GONE);
        }
    }

    public void setIconOverride(int width, int height) {
        iconView.setLayoutParams(new LayoutParams(width, height));
    }

    public void setTitle(String text) {
        if (TextUtils.isEmpty(text)) {
            titleView.setVisibility(GONE);
        } else {
            titleView.setText(text);
            titleView.setVisibility(VISIBLE);
        }

    }

    public void setButton(String text, OnClickListener listener) {
        if (TextUtils.isEmpty(text)) {
            actionView.setVisibility(GONE);
        } else {
            actionView.setText(text);
            actionView.setVisibility(VISIBLE);
            actionView.setOnClickListener(listener);
        }
    }

    /**
     * 设置居顶
     *
     * @param topMargin 距离顶部距离
     */
    public void setTitleViewBottom(final int topMargin) {
        LinearLayout.LayoutParams layoutParams= (LayoutParams) titleView.getLayoutParams();
        layoutParams.setMargins(0,0,0,IntExtentionKt.dp2px(topMargin));
        titleView.setLayoutParams(layoutParams);
    }

    /**
     * 设置居顶
     *
     * @param topMargin 距离顶部距离
     */
    public void setGravityTop(final int topMargin) {
        setGravity(Gravity.TOP);
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (layoutParams instanceof MarginLayoutParams)
            ((MarginLayoutParams) layoutParams).setMargins(0, IntExtentionKt.dp2px(topMargin), 0, 0);
    }
}
