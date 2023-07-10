package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.recyclerview.widget.RecyclerView

/**
 * 请求父类不拦截滑动事件
 * 避免手势冲突
 *
 * 上下滑动交给父组件
 */
class NoIntercepteRecyclerView @JvmOverloads constructor(
    var mContext: Context,
    attributes: AttributeSet? = null
) : RecyclerView(mContext, attributes) {

    var switch = true  //开关
//    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
//        parent.requestDisallowInterceptTouchEvent(switch)
//        return super.dispatchTouchEvent(ev)
//    }
    var lastX = 0f
    var lastY = 0f
    val TouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                // 计算移动距离

                // 计算移动距离
                val deltaX: Float = Math.abs(ev.x - lastX)
                val deltaY: Float = Math.abs(ev.y - lastY)
                // 判断滑动方向
                // 判断滑动方向
                if (deltaX > deltaY) {
                    // 左右滑动，不拦截事件
                    parent.requestDisallowInterceptTouchEvent(true)
                } else {
                    // 上下滑动，拦截事件
                    parent.requestDisallowInterceptTouchEvent(false)
                }


            }

        }
        return super.dispatchTouchEvent(ev)
    }
}