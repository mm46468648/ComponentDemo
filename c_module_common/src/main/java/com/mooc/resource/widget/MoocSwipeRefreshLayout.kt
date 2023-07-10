package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.mooc.common.R;


/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    主题色圆圈SwipeRefreshLayout
 * @Author:         xym
 * @CreateDate:     2020/8/20 7:00 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/20 7:00 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class MoocSwipeRefreshLayout @JvmOverloads constructor(var mContext: Context, attributes: AttributeSet? = null)
    : SwipeRefreshLayout(mContext, attributes) {
    private var mTouchSlop = 0

    // 上一次触摸时的X坐标
    private var mPrevX = 0f
    init {
        setColorSchemeResources(R.color.colorPrimary)
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }

    override fun setOnRefreshListener(listener: OnRefreshListener?) {
        super.setOnRefreshListener(listener)

        //todo 开启定时，超过10s自己关闭？
    }


    override fun onInterceptTouchEvent(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> mPrevX = event.x
            MotionEvent.ACTION_MOVE -> {
                val eventX = event.x
                val xDiff = Math.abs(eventX - mPrevX)
                // 增加60的容差，让下拉刷新在竖直滑动时就可以触发
                if (xDiff > mTouchSlop + 60) {
                    return false
                }
            }
        }
        return super.onInterceptTouchEvent(event)
    }
}