package com.mooc.home.ui.discover.mooc

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R



class DiscoverMoocAdapter(list:ArrayList<CourseBean>)
    : BaseQuickAdapter<CourseBean,BaseViewHolder>(R.layout.home_item_discover_course,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: CourseBean) {
        holder.setText(R.id.tvTitle,item.title)

        holder.getView<MoocImageView>(R.id.ivCover).setImageUrl(item.picture,3)
    }
}