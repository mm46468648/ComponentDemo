package com.mooc.battle.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

/**
 * 解决子View中含有Scrollview造成的手势冲突
 */
public class NoInterceptScrollView extends ScrollView {


    public NoInterceptScrollView(Context context) {
        super(context);
    }

    public NoInterceptScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NoInterceptScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        maxY = getChildAt(0).getMeasuredHeight() - getMeasuredHeight();
    }

    //在Y轴上可以滑动的最大距离 = 总长度 - 当前展示区域长度
    private int maxY;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
//        switch (ev.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                getParent().getParent().requestDisallowInterceptTouchEvent(true);
//                break;
//            case MotionEvent.ACTION_MOVE:
//                if (getScrollY() > 0 && getScrollY() < maxY)
//                    getParent().requestDisallowInterceptTouchEvent(true);
//                else
//                    getParent().requestDisallowInterceptTouchEvent(false);
//                /*if (getScrollY()==0)
//                    Log.i("kkk","滑到头了");
//                if (getChildAt(0).getMeasuredHeight() == getScrollY() + getHeight())
//                    Log.i("kkk","滑到底了");*/
//                break;
//            case MotionEvent.ACTION_UP:
//                getParent().getParent().requestDisallowInterceptTouchEvent(false);
//                break;
//        }
        getParent().getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }
}
