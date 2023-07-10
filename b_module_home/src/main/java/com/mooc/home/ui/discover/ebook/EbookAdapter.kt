package com.mooc.home.ui.discover.ebook

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
import com.mooc.home.R


class EbookAdapter(list:ArrayList<EBookBean>)
    : BaseQuickAdapter<EBookBean,BaseViewHolder>(R.layout.home_item_discover_ebook,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: EBookBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_author, item.writer)

        var sourse = "${item.platform_zh} | ${item.press}"
        if(item.platform_zh.isEmpty() || item.press.isEmpty()){ sourse = sourse.replace("|","")}
        holder.setText(R.id.tv_source, sourse)
        holder.setText(R.id.tv_word_num, StringFormatUtil.formatPlayCount(item.word_count.toLong())+"字")



        if (!item.picture.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            imageView?.let {
                Glide.with(context)
                        .load(item.picture)
                        .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                        .into(it)
            }
        }
    }
}