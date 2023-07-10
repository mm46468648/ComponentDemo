package com.mooc.my.pop

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupWindow
import android.widget.TextView
import com.mooc.my.R

//已读未读选择弹框
class FeedScreenPop(private val mContext: Context, private val parent: View) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: View? = null
    private var tvUnRead: TextView? = null
    private var tvAll: TextView? = null
    var listener: ((b: Int) -> Unit)? = null
//    private var listener: OnFeedPopClickListener? = null
//    fun setListener(listener: OnFeedPopClickListener?) {
//        this.listener = listener
//    }

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.my_pop_feed_screen, null)
        tvUnRead = container?.findViewById<View>(R.id.tv_un_read) as TextView
        tvAll = container?.findViewById<View>(R.id.tv_feed_all) as TextView
    }

    private fun initData() {}
    private fun initListener() {
        container?.setOnClickListener {
            if (mPopup != null) {
                mPopup?.dismiss()

            }
            tvUnRead?.setOnClickListener {
                listener?.invoke(0)
                mPopup?.dismiss()
            }
            tvAll?.setOnClickListener {
                listener?.invoke(1)
                mPopup?.dismiss()
            }
        }
    }
        fun initPopup() {
            mPopup = PopupWindow(container, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT, true)
            mPopup?.contentView = container
            mPopup?.setBackgroundDrawable(ColorDrawable(0))
            mPopup?.isOutsideTouchable = true
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
            mPopup?.showAtLocation(parent, Gravity.BOTTOM, 0, 0)
        }
     fun setBackgroundAlpha(bgAlpha: Float) {
        val lp = (mContext as Activity).window
                .attributes
        lp.alpha = bgAlpha
        mContext.window.attributes = lp
    }

    override fun onDismiss() {
        setBackgroundAlpha(1.0f)
    }

}