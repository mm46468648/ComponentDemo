package com.mooc.commonbusiness.dialog

import android.annotation.SuppressLint
import android.content.Context
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import com.mooc.common.ktextends.dp2px
import com.mooc.common.ktextends.getColorRes
import com.mooc.commonbusiness.R
import com.mooc.commonbusiness.databinding.CommonLayoutPublicDialogBinding
import com.mooc.commonbusiness.model.PublicDialogBean
//import kotlinx.android.synthetic.main.common_layout_public_dialog.view.*

@SuppressLint("ViewConstructor")
class PublicDialog(context: Context, var publicDialogBean: PublicDialogBean, var onLeftOrRightListener: ((i: Int) -> Unit)? = null) : BasePopupDialog(context) {
    var mContext: Context = context
    var mPublicDialogBean: PublicDialogBean = publicDialogBean
    var isClickRight: Int = -1        //点击事件在左侧按钮，还是右侧按钮 0为左侧，1为右侧

    private lateinit var inflater: CommonLayoutPublicDialogBinding
    override fun getMaxWidth(): Int {
        return 300.dp2px()
    }

    override fun getImplLayoutId(): Int {
        return R.layout.common_layout_public_dialog
    }

    override fun onCreate() {
        super.onCreate()

        inflater = CommonLayoutPublicDialogBinding.bind(popupImplView)
        initView()
        initListener()
    }

    fun initListener() {
        inflater.tvLeft.setOnClickListener {
            isClickRight = 0
            dismiss()
            onLeftOrRightListener?.invoke(isClickRight)
        }
        inflater.tvRight.setOnClickListener {
            isClickRight = 1
            dismiss()
            onLeftOrRightListener?.invoke(isClickRight)
        }
    }

    fun initView() {
        //设置弹框描述
        if (!TextUtils.isEmpty(mPublicDialogBean.strMsg)) {
            inflater.tvMsg.text = mPublicDialogBean.strMsg
        }
        if (mPublicDialogBean.strSpan != null) {
            inflater.tvMsg.text = mPublicDialogBean.strSpan
        }
        //设置左侧按钮文字
        if (!TextUtils.isEmpty(mPublicDialogBean.strLeft)) {
            inflater.tvLeft.visibility = View.VISIBLE
            inflater.tvLeft.text = mPublicDialogBean.strLeft
        } else {
            inflater.tvLeft.visibility = View.GONE
        }
        //设置左侧按钮背景及字体颜色
        if (mPublicDialogBean.isLeftGreen == 1) {//左侧绿色背景，白色字体
            inflater.tvLeft.setBackgroundResource(R.mipmap.common_ic_dialog_left)
            inflater.tvLeft.setTextColor(mContext.getColorRes(R.color.color_white))
        } else {//左侧灰色背景，白色字体
            inflater.tvLeft.setBackgroundResource(R.mipmap.common_bg_pop_cancel_left)
            inflater.tvLeft.setTextColor(mContext.getColorRes(R.color.color_787878))
        }
        //设置右侧按钮背景及字体颜色
        if (!TextUtils.isEmpty(mPublicDialogBean.strRight)) {
            inflater.tvRight.visibility = View.VISIBLE
            inflater.tvRight.text = mPublicDialogBean.strRight
        } else {
            inflater.tvRight.visibility = View.GONE
        }
        //设置左侧按钮背景及字体颜色
        if (mPublicDialogBean.isLeftGreen == 1) {//右侧灰色背景，白色字体
            inflater.tvRight.setBackgroundResource(R.mipmap.common_ic_dialog_right)
            inflater.tvRight.setTextColor(mContext.getColorRes(R.color.color_787878))
        } else {//右侧绿色背景，白色字体
            inflater.tvRight.setBackgroundResource(R.mipmap.common_bg_pop_ok_right)
            inflater.tvRight.setTextColor(mContext.getColorRes(R.color.color_white))
        }
    }

}