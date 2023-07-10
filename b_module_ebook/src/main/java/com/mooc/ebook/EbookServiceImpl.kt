package com.mooc.ebook

import android.content.Context
import com.alibaba.android.arouter.facade.annotation.Route
import com.ireader.plug.api.ZYReaderPluginApi
import com.mooc.common.global.AppGlobals
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.EbookService
import com.zhangyue.plugin.ZYReaderSdkHelper

@Route(path = Paths.SERVICE_EBOOK)
class EbookServiceImpl : EbookService {
    override fun initSdk() {
        ZYReaderSdkHelper.initSdk(AppGlobals.getApplication())
    }

    /**
     * 设置是否允许隐私权限
     * @param boolean true的时候是不同意,false代表同意
     */
    override fun setDisallowPrivacy(boolean: Boolean) {
        ZYReaderPluginApi.disallowPrivacy(boolean)
    }


    override fun turnToZYReader(context : Context, id: String) {
        ZYReaderSdkHelper.enterBookReading(context, id)
    }
}