package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import android.widget.FrameLayout
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.loge
import kotlin.math.absoluteValue


/**
 * 可以左右滑动的FramLayout
 */
class ScrollFramLayout(var mContext: Context, attr: AttributeSet) :
    FrameLayout(mContext, attr) {

    private var touchSlop //系统值
            = 20.dp2px()


    public var mScrollChangeListener : OnScrollChange? = null
    init {
        touchSlop = ViewConfiguration.get(context).getScaledTouchSlop() * 2;

    }
    var lastX = 0f
    var lastY = 0f

    override fun dispatchTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.getAction()

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                lastX = ev.getX()
                lastY = ev.getY()
                parent.requestDisallowInterceptTouchEvent(true)
            }
            MotionEvent.ACTION_MOVE -> {
                val absoluteValue = (ev.y - lastY).absoluteValue
                val upOrDown = absoluteValue > touchSlop

                val left = ev.x - lastX < -touchSlop
                val right = ev.x - lastX > touchSlop

//                loge("ev.getX(): ${ev.getX()} , lastX: ${lastX} , mTouchSlop: ${touchSlop}, absoluteValue: ${absoluteValue}, upOrDown: ${upOrDown}")
                if (right || left) {
                    parent.requestDisallowInterceptTouchEvent(true)
                }  else {
                    parent.requestDisallowInterceptTouchEvent(false)
                }
            }

            MotionEvent.ACTION_UP -> {
                if (Math.abs(ev.getX() - lastX) > touchSlop) {
                    if (ev.getX() - lastX >= 0) {
                        mScrollChangeListener?.scrollOritation(Oritation.PRE)
                    } else {
                        mScrollChangeListener?.scrollOritation(Oritation.NEXT);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(ev);

    }

    interface OnScrollChange {
        fun scrollOritation(oratation: Oritation)
    }

    enum class Oritation {
        NEXT, PRE
    }

}