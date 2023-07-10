package com.mooc.home.ui.discover.microknow

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.commonbusiness.model.search.CourseBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R



class MicroKnowAdapter(data: MutableList<MicroKnowBean>?,
                       layoutResId: Int = R.layout.discover_item_micro_know
) : BaseQuickAdapter<MicroKnowBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: MicroKnowBean) {
        holder.setText(com.mooc.commonbusiness.R.id.tvTitleMicroKnow, item.title)
        holder.setText(com.mooc.commonbusiness.R.id.tvNumLearnMicroKnow, item.click_num + "人学习")
        holder.setText(com.mooc.commonbusiness.R.id.tvNumPassMicroKnow, item.exam_num + "人通过微测试")
        holder.setText(com.mooc.commonbusiness.R.id.tvNumLikeMicroKnow, item.like_num + "人点赞")

        Glide.with(context)
            .load(item.pic)
            .error(com.mooc.commonbusiness.R.mipmap.common_bg_cover_default)
            .placeholder(com.mooc.commonbusiness.R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(holder.getView(com.mooc.commonbusiness.R.id.ivImgMicroKnow))

    }

}