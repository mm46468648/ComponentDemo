package com.mooc.home.ui.discover.publication

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.PublicationBean
import com.mooc.home.R


class PublicationAdapter(list:ArrayList<PublicationBean>)
    : BaseQuickAdapter<PublicationBean,BaseViewHolder>(R.layout.home_item_discover_ebook,list),LoadMoreModule{
    override fun convert(holder: BaseViewHolder, item: PublicationBean) {
        holder.setText(R.id.tv_title,item.magname)
        holder.setText(R.id.tv_author,"更新至${item.year}第${item.term}期")
        holder.setText(R.id.tv_source,item.unit)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        imageView?.let {
            Glide.with(context)
                .load(item.coverurl)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                .into(it)
        }
    }
}