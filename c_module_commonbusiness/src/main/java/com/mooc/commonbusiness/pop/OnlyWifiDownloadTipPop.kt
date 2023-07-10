package com.mooc.commonbusiness.pop

import android.content.Context
import android.widget.Button
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.commonbusiness.R
import com.lxj.xpopup.core.CenterPopupView
import com.mooc.commonbusiness.route.Paths

class OnlyWifiDownloadTipPop(context: Context) : CenterPopupView(context) {

    override fun getImplLayoutId(): Int {
        return R.layout.common_only_wifi_download
    }

    override fun onCreate() {
        super.onCreate()

        findViewById<Button>(R.id.btConfirm).setOnClickListener {
            ARouter.getInstance().build(Paths.PAGE_SETTING).navigation()
            dismiss()
        }

        findViewById<Button>(R.id.btCancle).setOnClickListener {
            dismiss()
        }
    }
}