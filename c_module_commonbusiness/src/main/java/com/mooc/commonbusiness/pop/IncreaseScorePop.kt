package com.mooc.commonbusiness.pop

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.lxj.xpopup.animator.PopupAnimator
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.R
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.databinding.CommonPopIncreseScoreTipBinding
//import kotlinx.android.synthetic.main.common_pop_increse_score_tip.view.*


/**
 * 增加积分方法
 */
class IncreaseScorePop(var mContent:Context,var score:Int,var day:Int) : CenterPopupView(mContent) {

    private lateinit var inflater: CommonPopIncreseScoreTipBinding
    override fun getImplLayoutId(): Int {
        return R.layout.common_pop_increse_score_tip
    }

    override fun onCreate() {
        super.onCreate()

        inflater = CommonPopIncreseScoreTipBinding.bind(popupImplView)
        popupInfo.customAnimator = MCustomAnimator()
        inflater.tvScore.text = "+${score}"
        if (day > 0) {
            inflater.tvStudyDay.visibility = View.VISIBLE
            inflater.tvStudyDay.text = "连续学习${day}天奖励"
        }
    }

    open inner class MCustomAnimator : PopupAnimator(){
        override fun animateShow() {
            val animation1 = AnimationUtils.loadAnimation(mContent, R.anim.common_set_tran_alpha_animation)
            animation1.fillAfter = true
            targetView?.animation = animation1
            animation1.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation) {}
                override fun onAnimationEnd(animation: Animation) {
                    targetView?.clearAnimation()
                    targetView?.invalidate()
                    targetView?.visibility = View.GONE
                    dismiss()

                }

                override fun onAnimationRepeat(animation: Animation) {}
            })
        }

        override fun animateDismiss() {

        }

        override fun initAnimator() {

        }

    }

}