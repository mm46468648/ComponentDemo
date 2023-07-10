package com.mooc.discover.window

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.RelativeLayout
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.discover.R
import com.mooc.discover.model.ActivityJoinBean
import com.mooc.resource.widget.MoocImageView
/**
 *活动弹框
 * @author limeng
 * @date 2021/4/13
 */
class ShowActivityPop(private val mContext: Context, private val parent: View) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: RelativeLayout? = null
     var joinBean: ActivityJoinBean? = null
    private var actImg: MoocImageView? = null
    private var closeImg: MoocImageView? = null


    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.home_pop_act, null) as RelativeLayout?
        actImg = container?.findViewById(R.id.act_point)
        closeImg = container?.findViewById(R.id.act_close)
    }

    private fun initData() {}
    private fun initListener() {
        actImg?.setOnClickListener {
            if (joinBean != null) {
                ResourceTurnManager.turnToResourcePage(joinBean!!)
//                Utils.launchToPageWithTypeId(mContext, joinBean)
            }
            if (mPopup != null) {
                mPopup?.dismiss()
            }
        }
        closeImg?.setOnClickListener {
            if (mPopup != null) {
                mPopup?.dismiss()
            }
        }
    }

    private fun initPopup() {
        mPopup = PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
        mPopup?.contentView = container
        mPopup?.setBackgroundDrawable(ColorDrawable(0))
        mPopup?.isOutsideTouchable = true
        mPopup?.setOnDismissListener(this)
    }

    fun show() {
        if (mPopup == null) {
            initPopup()
        }
        if (joinBean != null) {
            if (!TextUtils.isEmpty(joinBean?.picture_frame)) {
                actImg?.setImageUrl(joinBean?.picture_frame, 0)
            }
            if (!TextUtils.isEmpty(joinBean?.picture_close)) {
                closeImg?.setImageUrl(joinBean?.picture_close, 0)
            }
        }
        setBackgroundAlpha(0.3f)
        mPopup?.showAtLocation(parent, Gravity.CENTER, 0, 0)
    }

    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window
                .attributes
        lp.alpha = bgAlpha
        mContext.window.attributes = lp
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }

    init {
        initView()
        initData()
        initListener()
        initPopup()
    }
}