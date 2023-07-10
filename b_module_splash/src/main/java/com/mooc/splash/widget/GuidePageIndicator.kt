package com.mooc.splash.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.viewpager2.widget.ViewPager2
import com.mooc.splash.R
import com.mooc.common.ktextends.dp2px

/**
 * 引导页底部导航控件
 */
class GuidePageIndicator @JvmOverloads constructor(var mContext: Context,  attributes: AttributeSet?,  defaultAttr:Int = 0)
    : LinearLayout(mContext,attributes,defaultAttr){


    var viewPager2:ViewPager2? = null

    /**
     * 关联Viewpager
     */
    fun setUpWithViewPager(viewPage2:ViewPager2){
        viewPager2 = viewPage2

        initPoint()

        viewPage2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                setPointPosition(position)
            }
        })
    }

    /**
     * 初始化指示器
     */
    private fun initPoint() {

        removeAllViews()
        val viewPageCount = viewPager2?.adapter?.itemCount ?: 0
        for (i in 0 until viewPageCount){
            val view = View(context)
            val layoutParams = LinearLayout.LayoutParams(17.dp2px(), 5.dp2px())
            layoutParams.leftMargin = 10.dp2px()
            layoutParams.rightMargin = 10.dp2px()
            view.layoutParams = layoutParams
            view.setBackgroundResource(R.drawable.splash_selector_guideindicator_point)
            view.setOnClickListener {
                viewPager2?.setCurrentItem(i)
            }
            addView(view)
        }

        setPointPosition(0)


    }

    /**
     * 设置指示器位置
     */
    private fun setPointPosition(position: Int) {
        if (childCount <= position) return

        for (i in 0 until childCount) {
            val view: View = getChildAt(i)
            view.isSelected = position == i
        }
    }

}