package com.mooc.search.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.search.R

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class AlbumAdapter(
    data: MutableList<AlbumBean>?,
    layoutResId: Int = R.layout.search_item_audio_album
) : BaseQuickAdapter<AlbumBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: AlbumBean) {
        holder.setText(R.id.tv_title, item.album_title)
        holder.setText(R.id.tv_play_num, StringFormatUtil.formatPlayCount(item.play_count))
        holder.setText(R.id.tv_collection, "${item.include_track_count}集")

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.cover_url_small)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)

    }

}