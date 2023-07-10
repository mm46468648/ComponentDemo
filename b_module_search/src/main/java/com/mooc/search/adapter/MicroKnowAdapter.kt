package com.mooc.search.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.microknowledge.MicroKnowBean
import com.mooc.search.R

class MicroKnowAdapter(
    data: MutableList<MicroKnowBean>?,
    layoutResId: Int = R.layout.search_item_micro_knowledge
) : BaseQuickAdapter<MicroKnowBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: MicroKnowBean) {
        holder.setText(R.id.tvTitleMicroKnow, item.title)
        holder.setText(R.id.tvNumLearnMicroKnow, item.click_num + "人学习")
        holder.setText(R.id.tvNumPassMicroKnow, item.exam_pass_num + "人通过微测试")
        holder.setText(R.id.tvNumLikeMicroKnow, item.like_num + "人点赞")

        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImgMicroKnow)
        Glide.with(context)
            .load(item.pic)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)

    }

}