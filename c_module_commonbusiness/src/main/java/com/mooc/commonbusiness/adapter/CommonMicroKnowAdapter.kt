package com.mooc.commonbusiness.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean

class CommonMicroKnowAdapter(
    data: MutableList<MicroKnowBean>?,
    layoutResId: Int = R.layout.common_item_micro_know
) : BaseQuickAdapter<MicroKnowBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: MicroKnowBean) {
        holder.setText(R.id.tvTitleMicroKnow, item.title)
        holder.setText(R.id.tvNumLearnMicroKnow, item.click_num + "人学习")
        holder.setText(R.id.tvNumPassMicroKnow, item.exam_num + "人通过微测试")
        holder.setText(R.id.tvNumLikeMicroKnow, item.like_num + "人点赞")

        Glide.with(context)
            .load(item.pic)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .transform(GlideTransform.centerCropAndRounder2)
            .into(holder.getView(R.id.ivImgMicroKnow))

    }

}