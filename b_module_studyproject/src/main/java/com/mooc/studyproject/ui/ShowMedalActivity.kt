package com.mooc.studyproject.ui

import android.app.Activity
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import androidx.annotation.NonNull
import androidx.annotation.Nullable
import com.alibaba.android.arouter.facade.annotation.Route
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.route.Paths
import com.mooc.studyproject.R
import com.mooc.studyproject.databinding.ActivityShowMedalStudyBinding
//import kotlinx.android.synthetic.main.activity_show_medal_study.*
/**
 *展示勋章的弹框
 * @author limeng
 * @date 2021/3/17
 */
@Route(path = Paths.PAGE_SHOW_MEDAL)
class ShowMedalActivity : Activity() {
    var imageUrl: String? = null

    private lateinit var inflater: ActivityShowMedalStudyBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        inflater = ActivityShowMedalStudyBinding.inflate(layoutInflater)
        setContentView(inflater.root)
        initData()
        initListener()
    }
    private fun initData() {
        imageUrl= intent.getStringExtra(IntentParamsConstants.INTENT_IMAGE_URL)
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
            finish()
        }
    }

    private fun setTipsViewAnimation() {
        inflater.ivMedalContent.visibility = View.VISIBLE
        val animation1 = AnimationUtils.loadAnimation(this, R.anim.set_scale_animation)
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
        val animation = AnimationUtils.loadAnimation(this, R.anim.set_animation_image)
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




}