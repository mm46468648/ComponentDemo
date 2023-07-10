package com.mooc.search.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.search.R

/**

 * @Author 纪博学
 * @Date 2021/3/13-4:28 PM
 */
class SearchBookAdapter(
    data: MutableList<EBookBean>?,
    layoutResId: Int = R.layout.search_item_add_book
) : BaseQuickAdapter<EBookBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: EBookBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_author, item.writer)
        holder.setText(R.id.tv_source, item.platform_zh + " | " + item.press)
        holder.setText(
            R.id.tv_word_num,
            StringFormatUtil.formatPlayCount(item.word_count) + "字"
        )

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