package com.mooc.commonbusiness.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R

/**
图片适配器
 * @Author limeng
 * @Date 2020/10/21-3:21 PM
 */
class FeedbackImgAdapter( data: ArrayList<String>?, layoutId: Int = R.layout.my_item_publish_img):BaseQuickAdapter<String, BaseViewHolder>(layoutId, data){
    override fun convert(holder: BaseViewHolder, item: String) {

        if (item.isNullOrEmpty()) {
            holder.setVisible(R.id.img_del, false)
        }else{
            holder.setVisible(R.id.img_del, true)

        }

        Glide.with(context)
                .load(item)
                .error(R.mipmap.common_add_img_publish)
                .placeholder(R.mipmap.common_add_img_publish)
                .centerCrop()
                .override(200,200)
                .into(holder.getView(R.id.imageView))


    }
}