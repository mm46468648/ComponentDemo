package com.mooc.commonbusiness.pop.my

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
import com.mooc.commonbusiness.R

class UploadPicturePop(private val mContext: Context, private val parent: View) : PopupWindow.OnDismissListener {
    private var mPopup: PopupWindow? = null
    private var container: View? = null
    private var tvCamera: TextView? = null
    private var tvPhoto: TextView? = null
    private var tvCancel: TextView? = null
     var onCameraClick: (() -> Unit)? = null
     var onPhotoClick: (() -> Unit)? = null

    @SuppressLint("InflateParams")
    private fun initView() {
        container = LayoutInflater.from(mContext).inflate(R.layout.my_pop_upload_picture, null)
        tvCamera = container?.findViewById<View>(R.id.tv_upload_camera) as TextView
        tvPhoto = container?.findViewById<View>(R.id.tv_upload_photo) as TextView
        tvCancel = container?.findViewById<View>(R.id.tv_upload_cancel) as TextView
    }

    private fun initData() {}
    private fun initListener() {
        container?.setOnClickListener {
            if (mPopup != null) {
                mPopup?.dismiss()
            }
        }
        tvCamera?.setOnClickListener {
            onCameraClick?.invoke()
            mPopup?.dismiss()
        }
        tvPhoto?.setOnClickListener {
            mPopup?.dismiss()
            onPhotoClick?.invoke()
        }
        tvCancel?.setOnClickListener {
            mPopup?.dismiss()
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
        initView()
        initData()
        initListener()
        if (mPopup == null) {
            initPopup()
        }
        setBackgroundAlpha(0.5f)
        mPopup?.showAtLocation(parent, Gravity.BOTTOM, 0, 0)
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
//
//    interface OnUploadClickListener {
//        fun onCameraClick()
//        fun onPhotoClick()
//    }
}