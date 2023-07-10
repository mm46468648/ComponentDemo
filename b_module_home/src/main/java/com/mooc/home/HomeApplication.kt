package com.mooc.home

//import com.amitshekhar.DebugDB
import com.mooc.changeskin.SkinManager
import com.mooc.commonbusiness.base.BaseApplication
import com.mooc.commonbusiness.manager.ShakeFeedbackObserver
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.loge
import com.mooc.home.manager.ActivityStackObserver

class HomeApplication : BaseApplication() {
    override fun init() {
        AppGlobals.getApplication()?.registerActivityLifecycleCallbacks(ShakeFeedbackObserver())
        AppGlobals.getApplication()?.registerActivityLifecycleCallbacks(ActivityStackObserver())
        SkinManager.getInstance().init(instance)
//        val addressLog = DebugDB.getAddressLog()
//        loge("dbtestip: $addressLog")
    }
}