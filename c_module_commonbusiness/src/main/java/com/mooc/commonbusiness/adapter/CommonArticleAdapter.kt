package com.mooc.commonbusiness.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.ArticleBean
import com.mooc.common.R;

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class CommonArticleAdapter(
    data: MutableList<ArticleBean>?,
    layoutResId: Int = R.layout.commonrs_item_article
) : BaseQuickAdapter<ArticleBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: ArticleBean) {
        holder.setText(R.id.tv_title, item.title)

        if (!item.source.isEmpty()) {
            holder.setText(R.id.tv_source, item.source)
            holder.setVisible(R.id.tv_source, true)
        } else {
            holder.setVisible(R.id.tv_source, false)
        }
        if (!item.author.isNullOrEmpty()) {
            holder.setText(R.id.tv_author, item.author)
        } else {
            holder.setVisible(R.id.tv_author, false)

        }
        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        imageView?.let {
            Glide.with(context)
                .load(item.picture)
                .error(R.mipmap.common_bg_cover_vertical_default)
                .placeholder(R.mipmap.common_bg_cover_vertical_default)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }


    }

}