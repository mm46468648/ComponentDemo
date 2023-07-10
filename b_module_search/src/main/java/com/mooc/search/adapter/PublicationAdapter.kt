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
import com.mooc.commonbusiness.model.search.PublicationBean
import com.mooc.search.R

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class PublicationAdapter(
    data: MutableList<PublicationBean>?,
    layoutResId: Int = R.layout.search_item_publication
) : BaseQuickAdapter<PublicationBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: PublicationBean) {
        holder.setText(R.id.tvTitle, item.magname)

        val imageView = holder.getViewOrNull<ImageView>(R.id.ivImg)
        Glide.with(context)
            .load(item.coverurl)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
        if (TextUtils.isEmpty(item.unit)) {
            holder.setText(R.id.tvSource, "")
        } else {
            holder.setText(R.id.tvSource, item.unit)

        }
        if (TextUtils.isEmpty(item.year)) {
            item.year = ""
        }
        if (TextUtils.isEmpty(item.term)) {
            item.term = ""
        }
        holder.setText(R.id.tvTime, "更新至${item.year}年第${item.term}期")

    }


}