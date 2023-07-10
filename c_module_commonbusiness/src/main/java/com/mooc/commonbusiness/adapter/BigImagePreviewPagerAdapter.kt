package com.mooc.commonbusiness.adapter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Base64
import android.view.View
import android.view.ViewGroup
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.photoview.PhotoView
import com.mooc.commonbusiness.R
import com.mooc.common.ktextends.runOnMain

/**
 * 大图预览适配器
 */
class BigImagePreviewPagerAdapter(var list:ArrayList<String>)
    : PagerAdapter() {

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

    override fun getCount(): Int {
        return list.size
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val photoView = PhotoView(container.context)
        // call LoadImageListener
        val item = list[position]
        val mContext = container.context
        //加载vp的布局
//        val inflate = View.inflate(mContext, R.layout.common_item_bigimage_preview, null);
        //给布局控件赋值
//        val photoView = inflate.findViewById<PhotoView>(R.id.photoView);
        //base64格式
        if(item.startsWith("data:image")){
            //将Base64编码字符串解码成Bitmap
            val decodedString = Base64.decode(item.split(",")[1], Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size);
            //设置ImageView图片
            photoView.setImageBitmap(decodedByte)
        }else if(item.endsWith("gif")){
            Glide.with(mContext).asGif().load(item).into(photoView)
        }else{
            //glide加载
            Glide.with(mContext).asBitmap()
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
        //添加一个布局（不添加无效果）
        container.addView(photoView,ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT);
        return photoView
    }

//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        val item = list[position]
//        val mContext = container.context
//        //加载vp的布局
//        val inflate = View.inflate(mContext, R.layout.common_item_bigimage_preview, null);
//        //给布局控件赋值
//        val photoView = inflate.findViewById<PhotoView>(R.id.photoView);
//        //base64格式
//        if(item.startsWith("data:image")){
//            //将Base64编码字符串解码成Bitmap
//            val decodedString = Base64.decode(item.split(",")[1], Base64.DEFAULT)
//            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size);
//            //设置ImageView图片
//            photoView.setImageBitmap(decodedByte)
//        }else if(item.endsWith("gif")){
//            Glide.with(mContext).asGif().load(item).into(photoView)
//        }else{
//            //glide加载
//            Glide.with(mContext).asBitmap()
//                    .load(item)
//                    .placeholder(R.mipmap.common_bg_cover_big_default)
//                    .into(object : CustomTarget<Bitmap>(){
//                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                            runOnMain {
//                                photoView.setImageBitmap(resource)
//                            }
//                        }
//                        override fun onLoadCleared(placeholder: Drawable?) {
//
//                        }
//                    })
//        }
//        //添加一个布局（不添加无效果）
//        container.addView(inflate);
//        return inflate;
//
//    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //移除视图
        container.removeView(`object` as View)
    }
}

//    override fun onCreateDefViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
//        val photoView = PhotoView(context)
//        return createBaseViewHolder(photoView)
//    }
//    override fun convert(holder: BaseViewHolder, item: String) {
//        val photoView = holder.getView<PhotoView>(R.id.photoView)
//
//        //base64格式
//        if(item.startsWith("data:image")){
//            //将Base64编码字符串解码成Bitmap
//            val decodedString = Base64.decode(item.split(",")[1], Base64.DEFAULT)
//            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size);
//            //设置ImageView图片
//            photoView.setImageBitmap(decodedByte)
//        }else if(item.startsWith("http")){
//            //glide加载
//            Glide.with(context).asBitmap()
//                    .load(item)
//                    .placeholder(R.mipmap.common_bg_cover_big_default)
//                    .into(object : CustomTarget<Bitmap>(){
//                        override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
//                            runOnMain {
//                                photoView.setImageBitmap(resource)
//                            }
//                        }
//                        override fun onLoadCleared(placeholder: Drawable?) {
//
//                        }
//                    })
//        }
//
//    }


//}