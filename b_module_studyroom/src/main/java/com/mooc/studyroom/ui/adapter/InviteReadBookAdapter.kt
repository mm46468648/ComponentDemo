package com.mooc.studyroom.ui.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.module.LoadMoreModule
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.studyroom.R

/**

 * @Author 纪博学
 * @Date 2020/3/11-2:24 PM
 */
class InviteReadBookAdapter(data: MutableList<EBookBean>?, layoutResId: Int= R.layout.studyroom_invite_read_item_book) : BaseQuickAdapter<EBookBean, BaseViewHolder>(layoutResId, data) , LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: EBookBean) {




        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_source, item.press)



        if (!item.picture.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                    .load(item.picture)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                    .into(imageView!!)
        }

    }

}