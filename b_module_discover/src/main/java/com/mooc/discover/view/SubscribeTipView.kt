package com.mooc.discover.view

import android.animation.*
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.mooc.discover.R

class SubscribeTipView @JvmOverloads constructor(var mContext: Context, attributeSet: AttributeSet? = null, defaultValue: Int = 0)
    : FrameLayout(mContext, attributeSet, defaultValue), LifecycleObserver {


    var scaleNum = 0.6f
    var translateXDiff = 30f

    var animaDuration = 500L
    var animaDelay = 2000L


    val ivIcon: ImageView by lazy { findViewById<ImageView>(R.id.ivIcon) }

    val tvText: TextView by lazy { findViewById<TextView>(R.id.tvText) }


    var revers = false   //正向还是反向动画
    var currentAnima = ValueAnimator()

    init {
        LayoutInflater.from(mContext).inflate(R.layout.view_subscribe_tip, this)
        initAnimation()
    }


    private fun initAnimation() {
        currentAnima.duration = animaDuration
        currentAnima.startDelay = animaDelay
        currentAnima.addListener(object : Animator.AnimatorListener {
            override fun onAnimationRepeat(p0: Animator) {

            }

            override fun onAnimationEnd(p0: Animator) {
                if (revers) {
                    startAnima()
                } else {
                    startReverseAnima()
                }


            }

            override fun onAnimationCancel(p0: Animator) {
            }

            override fun onAnimationStart(p0: Animator) {
                revers = !revers
            }
        })
    }

    var ivX = 0f
    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)

        //开始动画
        //图标左边平移，并缩小
        //文字放大并改变透明度
        //播放一秒

        ivX = ivIcon.x
        startAnima()

    }


    @Synchronized
    fun startAnima() {
        if (currentAnima.isRunning) {
            currentAnima.cancel()
        }
        currentAnima.setFloatValues(1f, scaleNum)
        currentAnima.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            val animatedFraction = it.animatedFraction

            //iv属性变化
            val translatex = -animatedFraction * 30
            ivIcon.scaleX = animatedValue
            ivIcon.scaleY = animatedValue
            ivIcon.translationX = translatex

            //tv属性变化
            val alpha = animatedFraction * 1
            tvText.alpha = alpha
            tvText.scaleX = alpha
            tvText.scaleY = alpha
        }
        currentAnima.start()
    }

    @Synchronized
    fun startReverseAnima() {
        if (currentAnima.isRunning) {
            currentAnima.cancel()
        }
        currentAnima.setFloatValues(scaleNum, 1f)
        currentAnima.addUpdateListener {
            val animatedValue = it.animatedValue as Float
            val animatedFraction = it.animatedFraction

            //iv属性变化
            val translatex = -30 + animatedFraction * 30

            ivIcon.scaleX = animatedValue
            ivIcon.scaleY = animatedValue
            ivIcon.translationX = translatex


            //tv属性变化
            val alpha = 1 - animatedFraction
            tvText.alpha = alpha
            tvText.scaleX = alpha
            tvText.scaleY = alpha
        }
        currentAnima.start()

    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    open fun onObserverPause() {
        currentAnima.pause()

    }

//    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    open fun onObserverResume() {
        if (currentAnima.isRunning) {
            return
        }
        if (revers) {
            startAnima()
        } else {
            startReverseAnima()
        }
    }

}