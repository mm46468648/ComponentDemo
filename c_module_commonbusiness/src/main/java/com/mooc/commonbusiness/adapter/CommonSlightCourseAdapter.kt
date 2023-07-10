package com.mooc.commonbusiness.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.MicroBean
import com.mooc.commonbusiness.utils.format.TimeFormatUtil
import com.mooc.common.R;

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class CommonSlightCourseAdapter(
    data: MutableList<MicroBean>?,
    var isGonelable: Boolean = false,
    layoutResId: Int = R.layout.commonrs_item_slight_course
) : BaseQuickAdapter<MicroBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: MicroBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.platform_zh)
        holder.setText(
            R.id.tv_duration,
            TimeFormatUtil.formatAudioPlayTime(item.video_duration * 1000)
        )
        holder.setGone(R.id.tvLable, isGonelable)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.picture)
            .error(R.mipmap.common_bg_cover_default)
            .placeholder(R.mipmap.common_bg_cover_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
    }

}