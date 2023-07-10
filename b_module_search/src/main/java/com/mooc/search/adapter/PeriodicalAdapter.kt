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
import com.mooc.commonbusiness.model.search.PeriodicalBean
import com.mooc.search.R

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class PeriodicalAdapter(
    data: MutableList<PeriodicalBean>?,
    layoutResId: Int = R.layout.search_item_periodical
) : BaseQuickAdapter<PeriodicalBean, BaseViewHolder>(layoutResId, data), LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: PeriodicalBean) {
        holder.setText(R.id.tv_title, item.title)

        val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
        Glide.with(context)
            .load(item.basic_cover_url)
            .error(R.mipmap.common_bg_cover_vertical_default)
            .placeholder(R.mipmap.common_bg_cover_vertical_default)
            .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
            .into(imageView!!)
        if (TextUtils.isEmpty(item.basic_creator)) {
            holder.setVisible(R.id.tv_author, false)
        } else {
            holder.setVisible(R.id.tv_author, true)
            holder.setText(R.id.tv_author, item.basic_creator)

        }
        if (TextUtils.isEmpty(item.basic_date_time)) {
            holder.setVisible(R.id.tv_publish_time, false)
        } else {
            holder.setVisible(R.id.tv_publish_time, true)
            holder.setText(R.id.tv_publish_time, item.basic_date_time + "年出版")
        }

    }


}