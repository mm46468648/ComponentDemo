package com.mooc.ebook

import android.app.Application
import android.content.Context
import com.ireader.plug.api.ZYReaderPluginApi
import com.mooc.common.global.AppGlobals
import com.mooc.commonbusiness.base.BaseApplication

class EbookApplication : BaseApplication() {


    override fun onAttach(a : Application,c: Context) {
        super.onAttach(a,c)


        ZYReaderPluginApi.disallowPrivacy(true)
        // 集成过程中遇到问题可以打开debug，若调试已经无问题，则这里直接传参false即可
//        ZYReaderPluginApi.setDebugMode(true)
        ZYReaderPluginApi.initPlugWhenAPPAttachBaseContext(a, c)
//        loge("onAttach")


    }

    override fun init() {
//        if(AppGlobals.getApplication() == null){
//            System.out.println("application is null")
//            return
//        }
//        ZYReaderPluginApi.initPlugWhenAPPOncreate(AppGlobals.getApplication())
//        ZYReaderPluginApi.initPlugWhenAPPOncreate(instance)
//        loge("init")
    }

    override fun initWithContext(a: Application?) {
        if(a == null){
            System.out.println("application is null")
            return
        }
        ZYReaderPluginApi.initPlugWhenAPPOncreate(a)
    }
}