package com.mooc.home.widget

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.mooc.home.R

/**
 * 发现页专栏与猜你喜欢之间
 */
class HomeDiscoverGuessLikeHeadView @JvmOverloads constructor(var mContext: Context, var attributeSet: AttributeSet? = null, var defaultInt: Int = 0) :
        FrameLayout(mContext, attributeSet, defaultInt) {

    init {
        LayoutInflater.from(mContext).inflate(R.layout.home_view_discover_guesslike_head,this)
    }

}
