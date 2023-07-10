package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import com.mooc.common.ktextends.loge
import kotlin.math.absoluteValue


/**
 * 当父布局是ViewPager的时候
 * 解决嵌套引起的手势冲突
 */
class NoIntercepterViewPager(var mContext: Context, attr: AttributeSet) :
    ViewPager(mContext, attr) {

    var lastX = 0f
    var lastY = 0f
    val TouchSlop = ViewConfiguration.get(mContext).scaledTouchSlop

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val curPosition = this.currentItem
        val count = this.adapter?.count ?: 0
        when (ev.action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.x
                lastY = ev.y
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val absoluteValue = (ev.y - lastY).absoluteValue
                val upOrDown = absoluteValue > TouchSlop
//                val upOrDown = false
//                Log.e("NoIntercepterViewPager","absoluteValue: $absoluteValue === TouchSlop: $TouchSlop")
//                if(upOrDown){     //上下滑动，直接分发
//                    parent.requestDisallowInterceptTouchEvent(false)
//                    return super.dispatchTouchEvent(ev)
//                }
                val left = ev.x - lastX < -TouchSlop
                val right = ev.x - lastX > TouchSlop
//                curPosition = this.currentItem
//                val count = this.adapter?.count ?: 0
//                loge("curY:=${ev.y}")
//                // 当当前页面在第0页,并且是右滑（意思是前一夜）的时候，
//                // 或者最后一页并且向左滑（意思是下一页）的时候，由父亲拦截触摸事件
                if (upOrDown || ev.y < 500 ) {
                    parent.requestDisallowInterceptTouchEvent(false)
                } else if (right && curPosition == 0) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    loge("右滑:curPosition:=$curPosition","父亲拦截")
                } else if (left && curPosition == count - 1) {
                    parent.requestDisallowInterceptTouchEvent(false)
                    loge("左滑:curPosition:=$curPosition","父亲拦截")
                } else {
                    loge("自己处理")
                    parent.requestDisallowInterceptTouchEvent(true)
                }
//                if ((!upOrDown && right && curPosition == 0) || (!upOrDown && left && curPosition == count - 1)) {
//                    parent.requestDisallowInterceptTouchEvent(false)
//                } else {//其他情况，由孩子拦截触摸事件
//                    parent.requestDisallowInterceptTouchEvent(true)
//                }
            }

        }
        return super.dispatchTouchEvent(ev)
    }


}