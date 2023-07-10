package com.mooc.my.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.my.R

/**
 反馈对话框中的图片
 * @Author limeng
 * @Date 2020/10/29-3:55 PM
 */
class FeedBackListImageAdapter(data:MutableList<String>?,layoutId:Int= R.layout.my_feed_back_list_img)
    :BaseQuickAdapter<String,BaseViewHolder>(layoutId,data) {
    override fun convert(holder: BaseViewHolder, item: String) {
        Glide.with(context).load(item).placeholder(R.mipmap.my_iv_vertical_option)
                .error(R.mipmap.my_iv_vertical_option).into(holder.getView(R.id.img))
    }

}