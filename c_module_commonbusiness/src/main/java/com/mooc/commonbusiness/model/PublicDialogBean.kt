package com.mooc.commonbusiness.model

import android.text.SpannableString

class PublicDialogBean {
    var strMsg: String? = null//弹框描述
    var strSpan: SpannableString? = null//弹框描述

    var strLeft: String? = null//左侧按钮文字
    var strRight: String? = null//右侧按钮文字
    var strTv: String? = null//一个按钮时的按钮文字
    var isLeftGreen: Int = 1  //设置左侧按钮为绿色按钮1，还是右侧为绿色0
}