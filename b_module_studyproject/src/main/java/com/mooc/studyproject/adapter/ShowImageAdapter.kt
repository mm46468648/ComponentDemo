package com.mooc.studyproject.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.studyproject.R
import com.mooc.common.excutor.DispatcherExecutor
import java.util.*

/**

 * @Author limeng
 * @Date 2021/1/12-3:51 PM
 */
class ShowImageAdapter(var context: Context) : PagerAdapter() {
     var mImageList = ArrayList<String>()
    var strBaseUrl = ""
    var onSucessListener:((boo:Boolean)->Unit)?=null

    override fun getCount(): Int {
        return if (mImageList != null && mImageList.size > 0) {
            mImageList.size
        } else {
            0
        }
    }

    override fun isViewFromObject(view: View, any: Any): Boolean {
        return view ==any
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {

        val view: View = LayoutInflater.from(context).inflate(
                R.layout.studyproject_item_show_webimage, container, false)
        val imageView: ImageView = view.findViewById(R.id.show_webimage_imageview)
        var imageUrl: String? = mImageList[position]
        imageUrl = imageUrl?.let { getImageUrl(it) }
        //判断图片是否加载成功
        DispatcherExecutor.getIOExecutor().submit {
            Glide.with(context).asBitmap().circleCrop().load(imageUrl).into(object : CustomTarget<Bitmap>() {
                override fun onLoadCleared(placeholder: Drawable?) {

                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    imageView.setImageBitmap(resource)
                    onSucessListener?.invoke(true)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    super.onLoadFailed(errorDrawable)
                    imageView.setImageResource(R.mipmap.common_bg_cover_big_default)
                    onSucessListener?.invoke(false)


                }

            })
        }


        container.addView(view)

        return view
    }

    override fun destroyItem(container: ViewGroup, position: Int, any: Any) {
        super.destroyItem(container, position, any)
        val view = any as LinearLayout?
        container.removeView(view)
    }

    /**
     * 查看url地址结构不符合
     */
    private fun getImageUrl(url: String): String? {
        var url = url
        val index = url.substring(0, 4)
        return if (index.contains("http")) {
            url
        } else {
            val first = url.substring(0, 3)
            if (first.contains("//")) {
                url = "http:$url"
            } else {
                if (!TextUtils.isEmpty(strBaseUrl)) {
                    url = "$strBaseUrl/$url"
                }
            }
            url
        }
    }

}