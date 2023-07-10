package com.mooc.studyproject.window

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.mooc.studyproject.R

class DynamicPublishSucPop : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: View? = null
    private var mContext: Context
    private var tvShut: TextView? = null
    private var parent: View
    private var isComment //是否是评论成功（false代表动态）
            = false

    constructor(context: Context, parent: View) {
        mContext = context
        this.parent = parent
    }

    constructor(context: Context, parent: View, isComment: Boolean) {
        mContext = context
        this.parent = parent
        this.isComment = isComment
    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.common_pop_dynamic_publish_suc, null)
        tvShut = container?.findViewById<View>(R.id.tv_shut) as TextView
        val tvCommentSuccess = container?.findViewById<View>(R.id.tvCommentSuccess) as TextView
        val tvCommentTip = container?.findViewById<View>(R.id.tvCommentTip) as TextView
        var middleStr = "动态"
        var tipString = "请等待发起人审核，您可通过\"我的动态\"查看审核结果"
        if (isComment) {         //如果是评论，显示评论文案
            middleStr = "评论"
            tipString = "请等待发起人审核"
            tvCommentTip.text = tipString
            tvCommentTip.gravity = Gravity.CENTER
        } else {
            val spannableString = SpannableString(tipString)
            val foregroundColorSpan = ForegroundColorSpan(ContextCompat.getColor(mContext, R.color.colorPrimary))
            spannableString.setSpan(foregroundColorSpan, tipString.indexOf("" +
                    "\"我的"), tipString.indexOf("\"我的") + 6, Spanned.SPAN_INCLUSIVE_EXCLUSIVE)
            tvCommentTip.text = spannableString
        }
        val tipSuccess = middleStr + "提交成功"
        tvCommentSuccess.text = tipSuccess
    }

    private fun initData() {}
    private fun initListener() {
        tvShut?.setOnClickListener {
                mPopup?.dismiss()
        }
    }

    private fun initPopup() {
        mPopup = PopupWindow(container, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT, true)
        mPopup?.contentView = container
        mPopup?.setBackgroundDrawable(ColorDrawable(0))
        mPopup?.isFocusable = false
        mPopup?.isOutsideTouchable = false
        mPopup?.setOnDismissListener(this)
    }

    fun show() {
        initView()
        initData()
        initListener()
        if (mPopup == null) {
            initPopup()
        }
        setBackgroundAlpha(0.5f)
        mPopup?.showAtLocation(parent, Gravity.CENTER, 0, 0)
    }

    private fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window
                .attributes
        lp.alpha = bgAlpha
        (mContext as Activity).window.attributes = lp
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }
}