package com.mooc.commonbusiness.adapter

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.manager.BigImagePreview


/**

 * @Author limeng
 * @Date 2020/12/17-4:01 PM
 */
class DynamicImagesAdapter(var list: ArrayList<String>?, layoutId: Int = R.layout.common_item_dynamic_img) : BaseQuickAdapter<String, BaseViewHolder>(layoutId, list) {

    override fun convert(holder: BaseViewHolder, item: String) {
        Glide.with(context).load(item)
                .placeholder(R.mipmap.common_bg_cover_default)
                .error(R.mipmap.common_bg_cover_default)
                .transform(RoundedCorners(2.dp2px()))
                .into(holder.getView(R.id.imageView))
        holder.getView<ImageView>(R.id.imageView).setOnClickListener {
            //   ShowWebImageActivity  因为涉及到好多页面的要添加的跳转，因此写这里了!!
            if (data.size > 0) {
                list?.let { it1 ->
                    BigImagePreview
                            .setPosition(getItemPosition(item))
                            .setImageList(it1)
                            .start()
                }
            }
        }
    }

}