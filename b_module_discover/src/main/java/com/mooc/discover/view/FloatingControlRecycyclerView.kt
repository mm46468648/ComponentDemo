package com.mooc.discover.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.recyclerview.widget.RecyclerView

/**
 * 控制首页浮窗显示隐藏
 * RecyclerView
 */
class FloatingControlRecycyclerView @JvmOverloads constructor(
    var mContext: Context,
    var attributeSet: AttributeSet? = null,
    var defaultInt: Int = 0
) :
    RecyclerView(mContext, attributeSet, defaultInt) {

    init {
        _init()
    }

    private fun _init() {
        addOnScrollListener(object : OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)

                if(newState == RecyclerView.SCROLL_STATE_IDLE){
                    //show view
                    DiscoverAcitivtyFloatView.showControllLiveData.value = true
                    return
                }

                //hide view
                DiscoverAcitivtyFloatView.showControllLiveData.value = false
            }
        })
    }
}