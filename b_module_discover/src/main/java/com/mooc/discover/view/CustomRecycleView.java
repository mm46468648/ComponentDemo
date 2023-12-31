package com.mooc.discover.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 点击item自动滑动到剧中位置
 */
public class CustomRecycleView extends RecyclerView {

    private static final String TAG = CustomRecycleView.class.getSimpleName();
    Scroller mScroller;

    public CustomRecycleView(Context context) {
        super(context);
        init(context);
    }

    public CustomRecycleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CustomRecycleView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    private void init(Context context) {
        mScroller = new Scroller(context);
    }

    int mLastx;


    /**
     * 避免滑动手势冲突
     * @param ev
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        getParent().requestDisallowInterceptTouchEvent(true);
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        //computeScrollOffset返回true表示滚动还在继续，持续时间应该就是startScroll设置的时间
        if (mScroller != null && mScroller.computeScrollOffset()) {
            scrollBy(mLastx - mScroller.getCurrX(), 0);
            mLastx = mScroller.getCurrX();
            postInvalidate();//让系统继续重绘，则会继续重复执行computeScroll
        }
    }

    int mTargetPos;

    public void smoothToCenter(int position) {
        int parentWidth = getWidth();//获取父视图的宽度
        int childCount = getChildCount();//获取当前视图可见子view的总数
        //获取可视范围内的选项的头尾位置
        int firstvisiableposition = ((LinearLayoutManager) getLayoutManager()).findFirstVisibleItemPosition();
        int lastvisiableposition = ((LinearLayoutManager) getLayoutManager()).findLastVisibleItemPosition();
        int count = ((LinearLayoutManager) getLayoutManager()).getItemCount();//获取item总数
        Log.i(TAG, "count:" + count);
        mTargetPos = Math.max(0, Math.min(count - 1, position));//获取目标item的位置（参考listview中的smoothScrollToPosition方法）
        Log.i(TAG, "firstposition:" + firstvisiableposition + "   lastposition:" + lastvisiableposition + "   position:" + position +
                "   mTargetPos:" + mTargetPos);
        View targetChild = getChildAt(mTargetPos - firstvisiableposition);//获取目标item在当前可见视图item集合中的位置
        View firstChild = getChildAt(0);//当前可见视图集合中的最左view
        View lastChild = getChildAt(childCount - 1);//当前可见视图集合中的最右view
        Log.i(TAG, "first-->left:" + firstChild.getLeft() + "   right:" + firstChild.getRight());
        Log.i(TAG, "last-->left:" + lastChild.getLeft() + "   right:" + lastChild.getRight());
        int childLeftPx = targetChild.getLeft();//子view相对于父view的左边距
        int childRightPx = targetChild.getRight();//子view相对于父view的右边距
        Log.i(TAG, "target-->left:" + targetChild.getLeft() + "   right:" + targetChild.getRight());


        int childWidth = targetChild.getWidth();
        int centerLeft = parentWidth / 2 - childWidth / 2;//计算子view居中后相对于父view的左边距
        int centerRight = parentWidth / 2 + childWidth / 2;//计算子view居中后相对于父view的右边距
        Log.i(TAG, "rv width:" + parentWidth + "   item width:" + childWidth + "   centerleft:" + centerLeft + "   centerRight:" + centerRight);
        if (childLeftPx > centerLeft) {//子view左边距比居中view大（说明子view靠父view的右边，此时需要把子view向左平移
            //平移的起始位置就是子view的左边距，平移的距离就是两者之差
            mLastx = childLeftPx;
            mScroller.startScroll(childLeftPx, 0, centerLeft - childLeftPx, 0, 600);//600为移动时长，可自行设定
            postInvalidate();
        } else if (childRightPx < centerRight) {
            mLastx = childRightPx;
            mScroller.startScroll(childRightPx, 0, centerRight - childRightPx, 0, 600);
            postInvalidate();
        }


    }


}
