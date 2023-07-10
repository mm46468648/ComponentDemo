package com.mooc.resource.widget

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.common.ktextends.dp2px
import com.mooc.resource.utils.PixUtil.Companion.dp2px

class MoocImageView : AppCompatImageView {
    constructor(context: Context) : super(context) {}
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {}
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
    }


    /**
     * 设置圆形
     */
    fun setImageUrl(imageUrl: String = "", isCircle: Boolean = false,errorId:Int?=null) {
        setImageInErrorUrl(this, imageUrl, isCircle,0,errorId)
    }


    /**
     * 设置圆角
     */
    @JvmOverloads
    fun setImageUrl(imageUrl: String?, radius: Int = 0,errorId:Int?=null) {
        setImageInErrorUrl(this, imageUrl, false, radius,errorId)
    }

    /**
     * 设置本地图片资源远郊
     */
    fun setImageUrl(imageId: Int = -1, radius: Int = 0) {
        setImageUrl(this, imageId, false, radius)
    }

    companion object {
        @JvmStatic
        @BindingAdapter(value = ["image_url", "isCircle"])
        fun setImageUrl(view: MoocImageView, imageUrl: String?, isCircle: Boolean) {
            setImageUrl(view, imageUrl, isCircle, 0)
        }

        @JvmStatic
        @BindingAdapter(value = ["image_url", "isCircle", "radius"], requireAll = false)
        fun setImageUrl(view: MoocImageView, imageUrl: String?, isCircle: Boolean, radius: Int) {
            val builder = Glide.with(view.context).load(imageUrl)
            //        DrawableTypeRequest<String> builder = load;
//        RequestBuilder<Drawable> builder = load;
            if (isCircle) {
                builder.transform(CircleCrop())
            } else if (radius > 0) {
                builder.transform(RoundedCorners(dp2px(radius.toFloat()).toInt()))
            }
            val layoutParams = view.layoutParams
            if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                builder.override(layoutParams.width, layoutParams.height)
            }
            builder.into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.setImageDrawable(resource)
                    view.setBackgroundColor(Color.TRANSPARENT)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        }

        @JvmStatic
        @BindingAdapter(value = ["image_res", "isCircle", "radius"], requireAll = false)
        fun setImageUrl(view: MoocImageView, imageRes: Int, isCircle: Boolean, radius: Int) {
            val builder = Glide.with(view.context).load(imageRes)
            //        RequestBuilder<Drawable> builder = load;
            if (isCircle) {
                builder.transform(CircleCrop())
            } else if (radius > 0) {
                builder.transform(RoundedCorners(radius.dp2px()))
            }


            val layoutParams = view.layoutParams
            if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                builder.override(layoutParams.width, layoutParams.height)
            }
            builder.into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {
                    view.setImageDrawable(resource)
                    view.setBackgroundColor(Color.TRANSPARENT)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        }
        fun setImageInErrorUrl(view: MoocImageView, imageUrl: String?, isCircle: Boolean, radius: Int,errorId:Int?=null) {
            val builder = Glide.with(view.context).load(imageUrl)
            //        DrawableTypeRequest<String> builder = load;
//        RequestBuilder<Drawable> builder = load;
            if (isCircle) {
                builder.transform(CircleCrop())
            } else if (radius > 0) {
                builder.transform(RoundedCorners(dp2px(radius.toFloat()).toInt()))
            }
            if (errorId!=null) {
                builder.error(errorId)
            }
            val layoutParams = view.layoutParams
            if (layoutParams != null && layoutParams.width > 0 && layoutParams.height > 0) {
                builder.override(layoutParams.width, layoutParams.height)
            }
            builder.into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                        resource: Drawable,
                        transition: Transition<in Drawable>?
                ) {
                    view.setImageDrawable(resource)
                    view.setBackgroundColor(Color.TRANSPARENT)
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                }
            })
        }

    }
}