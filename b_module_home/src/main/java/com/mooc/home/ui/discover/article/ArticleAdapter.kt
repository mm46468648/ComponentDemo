package com.mooc.home.ui.discover.article

import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.glide.GlideTransform
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.resource.widget.MoocImageView
import com.mooc.home.R

/**
 * 发现推荐下面的文章适配器
 */
class ArticleAdapter(list:ArrayList<ArticleBean>)
    : BaseQuickAdapter<ArticleBean,BaseViewHolder>(R.layout.home_item_discover_article,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: ArticleBean) {
        holder.setText(R.id.tvTitle,item.title)
        holder.setText(R.id.tvOrgan,item.source)
//        holder.getView<MoocImageView>(R.id.ivCover).setImageUrl(item.picture,2)

        val ivCouver = holder.getView<MoocImageView>(com.mooc.discover.R.id.ivCover)
//        view.setImageUrl(item.plan_img, 2.dp2px())
//        Glide.with(context).load(item.plan_img).centerCrop().transform(RoundedCorners(2.dp2px())).into(ivCouver)
        Glide.with(context).load(item.picture).transform(GlideTransform.centerCropAndRounder2).into(ivCouver)

    }
}