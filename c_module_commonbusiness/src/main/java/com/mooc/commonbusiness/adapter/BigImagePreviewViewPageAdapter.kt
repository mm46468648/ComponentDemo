package com.mooc.commonbusiness.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.lxj.xpopup.photoview.PhotoView
import com.mooc.commonbusiness.R
import com.mooc.common.ktextends.runOnMain

/**
 * 大图预览适配器
 */
class BigImagePreviewViewPageAdapter(list:ArrayList<String>)
    : BaseQuickAdapter<String,BaseViewHolder>(R.layout.common_item_bigimage_preview,list){

//    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val photoView = PhotoView(context)
//        return createBaseViewHolder(photoView)
//    }
    override fun convert(holder: BaseViewHolder, item: String) {
        val photoView = holder.getView<PhotoView>(R.id.photoView)

        //base64格式
        if(item.startsWith("data:image")){
            //将Base64编码字符串解码成Bitmap
            val decodedString = Base64.decode(item.split(",")[1], Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size);
            //设置ImageView图片
            photoView.setImageBitmap(decodedByte)
        }else if(item.startsWith("http")){
            //glide加载
            Glide.with(context).asBitmap()
                    .load(item)
                    .placeholder(R.mipmap.common_bg_cover_big_default)
                    .into(object : CustomTarget<Bitmap>(){
                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                            runOnMain {
                                photoView.setImageBitmap(resource)
                            }
                        }
                        override fun onLoadCleared(placeholder: Drawable?) {

                        }
                    })
        }

    }


}