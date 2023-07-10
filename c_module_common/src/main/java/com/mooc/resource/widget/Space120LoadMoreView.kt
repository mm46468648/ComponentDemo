package com.mooc.resource.widget

import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.TextView
import com.chad.library.adapter.base.loadmore.BaseLoadMoreView
import com.chad.library.adapter.base.util.getItemView
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.R;

/**
 * 首页占位120dp
 * 防止列表被底部遮挡加载更多view
 *
 */
class Space120LoadMoreView(var customCompleteStr : String = "") : BaseLoadMoreView() {


    override fun getLoadComplete(holder: BaseViewHolder): View {
        val view = holder.getView<FrameLayout>(R.id.ll_load_complete_view)
        val findViewById = view.findViewById<TextView>(R.id.tvComplete)
        if(customCompleteStr.isNotEmpty()){
            findViewById.text = customCompleteStr
        }
        return view
    }

    override fun getLoadEndView(holder: BaseViewHolder): View {
        return holder.getView(R.id.ll_load_end_view)
    }

    override fun getLoadFailView(holder: BaseViewHolder): View {
        return holder.getView(R.id.ll_load_fail_view)
    }

    override fun getLoadingView(holder: BaseViewHolder): View {
        return holder.getView(R.id.ll_loading_view)
    }


    override fun getRootView(parent: ViewGroup): View {
        return parent.getItemView(R.layout.common_view_load_more_120)
    }


}