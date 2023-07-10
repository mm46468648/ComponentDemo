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
import com.mooc.search.R

/**

 * @Author limeng
 * @Date 2020/8/13-4:28 PM
 */
class EBookAdapter(data: MutableList<EBookBean>?, layoutResId: Int=R.layout.search_item_e_book) : BaseQuickAdapter<EBookBean, BaseViewHolder>(layoutResId, data) , LoadMoreModule {
    override fun convert(holder: BaseViewHolder, item: EBookBean) {
        holder.setText(R.id.tv_title, item.title)
        holder.setText(R.id.tv_author, item.writer)
        holder.setText(R.id.tv_source, item.press)
        holder.setText(R.id.tv_word_num, "${item.word_count}字")



        if (!item.picture.isNullOrBlank()) {
            val imageView = holder.getViewOrNull<ImageView>(R.id.iv_img)
            Glide.with(context)
                    .load(item.picture)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(2.dp2px())))
                    .into(imageView!!)
        }

    }

}