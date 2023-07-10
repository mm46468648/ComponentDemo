package com.mooc.commonbusiness.adapter

import com.bumptech.glide.Glide
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.model.image.ImageBean

/**
 * 图片上传需要复审
 */
class ImgCheckAdapter(data: ArrayList<ImageBean>?, layoutId: Int = R.layout.my_item_publish_img) : BaseQuickAdapter<ImageBean, BaseViewHolder>(layoutId, data) {
    override fun convert(holder: BaseViewHolder, item: ImageBean) {

        if (item.url.isNullOrEmpty()) {
            holder.setVisible(R.id.img_del, false)
            Glide.with(context)
                    .load(item.url)
                    .error(R.mipmap.common_add_img_publish)
                    .placeholder(R.mipmap.common_add_img_publish)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.getView(R.id.imageView))
        } else {
            holder.setVisible(R.id.img_del, true)
            Glide.with(context)
                    .load(item.url)
                    .error(R.mipmap.common_bg_cover_default)
                    .placeholder(R.mipmap.common_bg_cover_default)
                    .centerCrop()
                    .override(200, 200)
                    .into(holder.getView(R.id.imageView))
        }

    }
}