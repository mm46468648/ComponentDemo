package com.mooc.commonbusiness.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.mooc.common.ktextends.dp2px
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.CommonLayoutPublicOneDialogBinding
import com.mooc.commonbusiness.model.PublicDialogBean
//import kotlinx.android.synthetic.main.common_layout_public_one_dialog.view.*

@SuppressLint("ViewConstructor")
class PublicOneDialog
(context: Context, var publicDialogBean: PublicDialogBean) : BasePopupDialog(context) {
    var mContext: Context = context
    var mPublicDialogBean: PublicDialogBean = publicDialogBean

    private lateinit var inflater: CommonLayoutPublicOneDialogBinding
    override fun getMaxWidth(): Int {
        return 300.dp2px()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.common_layout_public_one_dialog
    }

    override fun onCreate() {
        super.onCreate()

        inflater = CommonLayoutPublicOneDialogBinding.bind(popupImplView)
        initView()
        initListener()
    }

    fun initListener() {
        inflater.tvButtonOne.setOnClickListener {
            dismiss()
        }
    }

    fun initView() {
        if (!TextUtils.isEmpty(mPublicDialogBean.strMsg)) {
            inflater.tvMsg.text = mPublicDialogBean.strMsg
        }
        if (!TextUtils.isEmpty(mPublicDialogBean.strTv)) {
            inflater.tvButtonOne.visibility = View.VISIBLE
            inflater.tvButtonOne.text = mPublicDialogBean.strTv
        } else {
            inflater.tvButtonOne.visibility = View.GONE
        }

    }

}

