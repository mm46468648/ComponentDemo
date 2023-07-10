package com.mooc.search.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.common.utils.TimeUtils
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.utils.format.StringFormatUtil
import com.mooc.search.R

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class TrackAdapter(data: MutableList<TrackBean>?, layoutResId: Int = R.layout.search_item_audio) :
    BaseQuickAdapter<TrackBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: TrackBean) {
        holder.setText(R.id.tv_title, item.track_title)
        holder.setText(R.id.tv_source, item.albumTitle)
        holder.setText(R.id.tv_play_num, StringFormatUtil.formatPlayCount(item.play_count.toInt()))
        holder.setText(R.id.tv_duration, TimeUtils.timeParse(item.duration))

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.cover_url_small)
            .error(R.mipmap.common_bg_cover_square_default)
            .placeholder(R.mipmap.common_bg_cover_square_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)

    }

}