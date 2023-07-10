package com.mooc.discover.view

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import com.mooc.discover.R


/**
 *
 * @ProjectName:
 * @Package:
 * @ClassName:
 * @Description:    状态栏占位控件
 * @Author:         xym
 * @CreateDate:     2020/8/11 3:38 PM
 * @UpdateUser:     更新者
 * @UpdateDate:     2020/8/11 3:38 PM
 * @UpdateRemark:   更新内容
 * @Version:        1.0
 */
class StatusBarView @JvmOverloads constructor(var mContext: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0)
    : View(mContext, attrs, defStyleAttr){

    var statusBarHeight = 0
    init {
        statusBarHeight = getStatusBarHeight(mContext)

        val ta = getContext().obtainStyledAttributes(attrs, R.styleable.StatusBarView)
        val appBarColor = ta.getColor(R.styleable.StatusBarView_sb_appBarColor,Color.TRANSPARENT)
        ta.recycle()

        //根据自定义属性设置背景颜色，默认透明
        try {//防止色值传的有问题
            setBackgroundColor(appBarColor)
        }catch (e:java.lang.Exception){}
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, statusBarHeight)
    }

    /**
     * 获取状态栏高度
     * @param context
     * @return
     */
    fun getStatusBarHeight(context: Context): Int {
        var statusBarHeight = 0
        try {
            val resourceId = context.resources.getIdentifier("status_bar_height", "dimen",
                    "android")
            if (resourceId > 0) {
                statusBarHeight = context.resources.getDimensionPixelSize(resourceId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return statusBarHeight
    }

}