package com.mooc.home.ui.discover.adapter

import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.home.R
import com.mooc.home.model.RecommendBannerBean

/**
 * 发现页banner适配器
 */
class DiscoverBannerAdapter constructor(var list: ArrayList<RecommendBannerBean>) : BaseQuickAdapter<RecommendBannerBean,BaseViewHolder>(
        -1,list){

    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        // 创建自己的布局
        val layout = ImageView(context);
        layout.scaleType = ImageView.ScaleType.FIT_XY
        layout.layoutParams = RecyclerView.LayoutParams(RecyclerView.LayoutParams.MATCH_PARENT,RecyclerView.LayoutParams.MATCH_PARENT)
        layout.setBackgroundResource(R.drawable.shape_radius2_62percent_black)
        return createBaseViewHolder(layout);
    }
    override fun convert(holder: BaseViewHolder, item: RecommendBannerBean) {
        Glide.with(context).load(item.picture).into(holder.itemView as ImageView)
    }
}