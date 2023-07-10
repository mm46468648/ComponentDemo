package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.mooc.common.R;
import com.mooc.resource.ktextention.dp2px

/**
 * 首页占位120dp
 * 防止列表被底部遮挡脚view
 */
class Space80EmptyView @JvmOverloads constructor(
    var mContext: Context,
    attributes: AttributeSet? = null,
    defaultAttr: Int = 0
) : FrameLayout(mContext, attributes, defaultAttr) {


    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setMeasuredDimension(measuredWidth, 80.dp2px())

    }

}