package com.mooc.commonbusiness.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.webkit.WebView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 可以设置最大高度的WebView
 */
public class MaxHeightWebView extends WebView {
    private int mMaxHeight = -1;

    private float mDownPosX;
    private float mDownPosY;
    private float mMovePosX;
    private float mMovePosY;

    public MaxHeightWebView(@NonNull Context context) {
        super(context);
    }

    public MaxHeightWebView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public MaxHeightWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public MaxHeightWebView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public void setMaxHeight(int height) {
        mMaxHeight = height;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mMaxHeight > -1 && getMeasuredHeight() > mMaxHeight) {
            setMeasuredDimension(getMeasuredWidth(), mMaxHeight);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        requestDisallowInterceptTouchEvent(true);

        // WebView的高度
        float webcontent = getContentHeight() * getScale();
        // 当前WebView的高度
        float webnow = getHeight() + getScrollY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownPosX = event.getX();
                mDownPosY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                mMovePosX = event.getX();
                mMovePosY = event.getY();
                if (Math.abs(mMovePosX - mDownPosX) < Math.abs(mMovePosY - mDownPosY)) {
                    if (mMovePosY - mDownPosY > 0) {
                        // 向下滑动
                        //并且滑动到顶部
                        float getY = getScrollY();
                        int height = getHeight();

                        Log.e("TEST","getY: " + getY + "getHeight:" + getHeight());
                        if(getY <= 0){
                            requestDisallowInterceptTouchEvent(false);
                        }else {
                            requestDisallowInterceptTouchEvent(true);
                        }
                    } else {
                        // 向上滑动
//并且滑动到底部
                        float getY = getScrollY();
                        int height = getHeight();
                        Log.e("TEST","webcontent: " + webcontent + "webnow:" + webnow);
                        if(Math.abs(webcontent - webnow) < 10){
                            requestDisallowInterceptTouchEvent(false);
                        }else {
                            requestDisallowInterceptTouchEvent(true);
                        }
                    }
                }
                break;
            default:
                break;
        }
        return super.onTouchEvent(event);
    }
}
