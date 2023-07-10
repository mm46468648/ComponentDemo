package com.mooc.battle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.mooc.battle.R;

public class MainOptionView extends FrameLayout {

    Context mContext;
    public MainOptionView(@NonNull Context context) {
        super(context);
        this.mContext = context;
    }

    public MainOptionView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        this.mContext = context;
        _init(attrs);
    }

    public MainOptionView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mContext = context;
        _init(attrs);
    }

    void _init(@Nullable AttributeSet attributeSet){
        View.inflate(mContext, R.layout.view_main_option,this);


        TypedArray a = mContext.obtainStyledAttributes(attributeSet, R.styleable.MainOptionView);
        String mainText = a.getString(R.styleable.MainOptionView_mov_text);
        int leftIcon = a.getResourceId(R.styleable.MainOptionView_mov_left_icon, -1);
        a.recycle();

        ((TextView)findViewById(R.id.tvTitle)).setText(mainText);
        if(leftIcon!=-1){
            ((ImageView)findViewById(R.id.ivLeft)).setImageResource(leftIcon);
        }
    }
}
