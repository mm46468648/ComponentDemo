package com.mooc.search.adapter

import android.text.TextUtils
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.search.R

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class ArticleAdapter(
    data: MutableList<ArticleBean>?,
    layoutResId: Int = R.layout.search_item_article
) : BaseQuickAdapter<ArticleBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: ArticleBean) {
        holder.setText(R.id.tv_title, item.title)

        var sourceStr = item.platform_zh
        if (!TextUtils.isEmpty(item.source)) {
            sourceStr += " | ${item.source}"
        }
        holder.setText(R.id.tv_source, sourceStr)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.picture)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)

    }

}