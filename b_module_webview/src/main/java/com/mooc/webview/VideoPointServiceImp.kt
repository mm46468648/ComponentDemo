package com.mooc.webview

import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.common.global.AppGlobals.getApplication
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.WebService
import com.tencent.smtt.sdk.QbSdk
import com.tencent.smtt.sdk.QbSdk.PreInitCallback

@Route(path = Paths.SERVICE_WEB_INITX5)
class WebServiceImp : WebService{
    override fun initX5() {
        QbSdk.initX5Environment(getApplication(), object : PreInitCallback {
            override fun onCoreInitFinished() {}
            override fun onViewInitFinished(b: Boolean) {
                WebviewApplication.x5InitFinish = b
            }
        })
    }
}