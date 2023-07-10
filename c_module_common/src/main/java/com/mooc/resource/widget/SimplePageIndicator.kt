package com.mooc.resource.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.mooc.common.R;
import com.mooc.resource.ktextention.dp2px

/**
 * 简易ViewPager指示器
 *
 * todo直接设置颜色，并实现联动效果
 */
class SimplePageIndicator @JvmOverloads constructor(var mContext: Context, attributes: AttributeSet?, defaultAttr:Int = 0)
    : LinearLayout(mContext,attributes,defaultAttr){

    var viewPager2:ViewPager2? = null

    var dotHeight = 2.dp2px().toFloat()
    var dotWidth = 2.dp2px().toFloat()
    var dotMargin = 2.dp2px().toFloat()

    var dotDrawableRes: Int = -1   //需要一个状态选择器

    var selectColor : Int = -1     //默认主题颜色
    var unSelectColor:Int = Color.WHITE

    init {

        // get custom attrs
        val a = context.obtainStyledAttributes(attributes, R.styleable.SimplePageIndicator)
        dotHeight = a.getDimension(R.styleable.SimplePageIndicator_dotHeight,dotHeight.toFloat())
        dotWidth = a.getDimension(R.styleable.SimplePageIndicator_dotWidth, dotWidth.toFloat())
        dotMargin = a.getDimension(R.styleable.SimplePageIndicator_dotMargin, dotMargin.toFloat())
        dotDrawableRes = a.getResourceId(R.styleable.SimplePageIndicator_dotDrawable,-1)


        selectColor = a.getColor(R.styleable.SimplePageIndicator_dotSelectColor,ContextCompat.getColor(mContext,R.color.colorPrimary))
        unSelectColor = a.getColor(R.styleable.SimplePageIndicator_dotSelectColor,unSelectColor)

        a.recycle()

    }
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
            val layoutParams = LinearLayout.LayoutParams(dotWidth.toInt(), dotHeight.toInt())
            layoutParams.leftMargin = dotMargin.toInt()
            layoutParams.rightMargin = dotMargin.toInt()
            view.layoutParams = layoutParams

            if(dotDrawableRes!=-1){     //如果设置了指示器的图片选择器
                view.setBackgroundResource(dotDrawableRes)
            }

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