package com.mooc.my.adapter

import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R
import com.mooc.my.model.ReadBean
import jp.wasabeef.glide.transformations.BlurTransformation

/**

 * @Author limeng
 * @Date 2020/9/1-4:42 PM
 */
class MyReadHistoryAdapter(data: ArrayList<ReadBean>, layoutResId: Int = R.layout.my_item_read_history) : BaseQuickAdapter<ReadBean, BaseViewHolder>(layoutResId, data) {
    override fun convert(holder: BaseViewHolder, item: ReadBean) {


        Glide.with(context).load(item.image_url)
                .placeholder(R.mipmap.my_bg_dailyread_load)
                .error(R.mipmap.my_bg_dailyread_load)
                .transform(RoundedCorners(20))
                .into(holder.getView(R.id.iv_read))
        Glide.with(context)
                .load(item.image_url)
                .transform(BlurTransformation(20, 3))
                .into(holder.getView(R.id.iv_bg))
    }
}