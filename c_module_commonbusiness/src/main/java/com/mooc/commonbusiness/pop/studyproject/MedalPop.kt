package com.mooc.commonbusiness.pop.studyproject

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.CommonMedalBinding
//import kotlinx.android.synthetic.main.common_medal.view.*

/**
勋章显示弹框
 * @Author limeng
 * @Date 2021/4/19-5:12 PM
 */
class MedalPop (context: Context,var imageUrl: String? = "",var onDismiss:(()->Unit)? = null):CenterPopupView(context) {
    override fun getImplLayoutId(): Int {
        return R.layout.common_medal
    }

    private lateinit var inflater: CommonMedalBinding
    override fun onCreate() {
        super.onCreate()

        inflater = CommonMedalBinding.bind(popupImplView)
        initData()
        initListener()
    }
    private fun initData() {
        Glide.with(this)
                .asBitmap()
                .load(imageUrl)
                .into(object : CustomTarget<Bitmap?>() {
                    override  fun onResourceReady(@NonNull resource: Bitmap, @Nullable transition: Transition<in Bitmap?>?) {
                        inflater.ivMedalContent.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(@Nullable placeholder: Drawable?) {}
                })
        setTipsViewAnimation()
    }
    private fun initListener() {
        inflater.rlContent.setOnClickListener {
           dismiss()
        }
    }

    private fun setTipsViewAnimation() {
        inflater.ivMedalContent.visibility = View.VISIBLE
        val animation1 = AnimationUtils.loadAnimation(context, R.anim.common_set_scale_animation)
        animation1.fillAfter = true
        inflater.ivMedalContent.animation = animation1
        animation1.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                inflater.ivMedalContent.clearAnimation()
                inflater.ivMedalContent.invalidate()
                inflater.ivMedalLight.visibility = View.VISIBLE
                setRotateAnimation()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    private fun setRotateAnimation() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.common_set_animation_image)
        val lin = LinearInterpolator()
        animation.interpolator = lin
        animation.fillAfter = true
        inflater.ivMedalLight.startAnimation(animation)
        animation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation) {}
            override fun onAnimationEnd(animation: Animation) {
                inflater.ivMedalLight.clearAnimation()
                inflater.ivMedalLight.invalidate()
            }

            override fun onAnimationRepeat(animation: Animation) {}
        })
    }

    override fun onDismiss() {
        super.onDismiss()

        onDismiss?.invoke()
    }
}