package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.viewpager.widget.ViewPager
import com.mooc.common.ktextends.loge
import kotlin.math.absoluteValue


/**
 * 禁止滑动的viewPager
 */
class NoScrollViewPager(var mContext: Context, attr: AttributeSet) :
    ViewPager(mContext, attr) {


    override fun onInterceptTouchEvent(ev: MotionEvent?): Boolean {
        return false
    }


}