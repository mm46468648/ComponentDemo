package com.mooc.home.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.common.ktextends.IntExtentionKt;
import com.mooc.home.R;

public class HomeBottomTabLayout extends FrameLayout {
    private Context mContext;
    private LinearLayout llBottomContainer;
    private ImageView ivCenterTab;
    private int currentSelect = -1;

    public HomeBottomTabLayout(@NonNull Context context) {
        this(context, null);
    }

    public HomeBottomTabLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HomeBottomTabLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        LayoutInflater.from(context).inflate(R.layout.home_view_bottom_tablayout, this);

        initView();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int measuredWidth = getMeasuredWidth();
        setMeasuredDimension(measuredWidth, (int) IntExtentionKt.dp2px(80));
    }

    private void initView() {
        llBottomContainer = findViewById(R.id.llBottomContainer);
        ivCenterTab = findViewById(R.id.ivCenterTab);
        for (int i = 0; i < llBottomContainer.getChildCount(); i++) {
            View childAt = llBottomContainer.getChildAt(i);
            int finalI = i;
            childAt.setOnClickListener(v -> clickSelect(finalI));
        }

        ivCenterTab.setOnClickListener(v -> clickSelect(2));
    }

    private void clickSelect(int position){
        select(position);
        if (itemSelectListener != null)
            itemSelectListener.onSelect(position);
    }
    public void select(int position) {
        if(position == currentSelect){
            return;
        }
        currentSelect = position;
        ivCenterTab.setSelected(position == 2);
        for (int i = 0; i < llBottomContainer.getChildCount(); i++) {
            View childAt = llBottomContainer.getChildAt(i);
            childAt.setSelected(i == position);
        }
    }

    public int getSelectedTabPosition(){
        return currentSelect;
    }
    private ItemSelectListener itemSelectListener;

    public void setOnItemSelectListener(ItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public interface ItemSelectListener {
        void onSelect(int position);
    }
}
