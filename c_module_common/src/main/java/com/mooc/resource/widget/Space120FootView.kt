package com.mooc.resource.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.LinearLayout
import com.mooc.common.R;

/**
 * 首页占位120dp
 * 防止列表被底部遮挡脚view
 */
class Space120FootView @JvmOverloads constructor(var mContext: Context, attributes: AttributeSet? = null, defaultAttr: Int = 0)
: FrameLayout(mContext, attributes, defaultAttr) {

    init {
//        setBackgroundColor(Color.BLACK)
//        layoutParams = LayoutParams(LayoutParams.MATCH_PARENT,120.dp2px())
        View.inflate(mContext,R.layout.common_view_load_more_120,this)
        findViewById<LinearLayout>(R.id.ll_loading_view).visibility = View.GONE
        findViewById<FrameLayout>(R.id.ll_load_end_view).visibility = View.VISIBLE

    }
}