package com.mooc.commonbusiness.dialog

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.utils.graysetting.GrayModeSetting

/**
 * 统一设置dialog灰度模式
 */
open class BasePopupDialog(context: Context) : CenterPopupView(context) {

    override fun onCreate() {
        super.onCreate()
        GrayModeSetting.setGrayMode(contentView)
    }
}