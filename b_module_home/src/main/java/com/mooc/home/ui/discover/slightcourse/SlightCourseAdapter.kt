package com.mooc.home.ui.discover.slightcourse

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.MicroBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R


class SlightCourseAdapter(list:ArrayList<MicroBean>)
    : BaseQuickAdapter<MicroBean,BaseViewHolder>(R.layout.home_item_discover_audio,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: MicroBean) {
        holder.setText(R.id.tvTitle,item.title)
        holder.getView<MoocImageView>(R.id.ivCover).setImageUrl(item.picture,3)
    }
}