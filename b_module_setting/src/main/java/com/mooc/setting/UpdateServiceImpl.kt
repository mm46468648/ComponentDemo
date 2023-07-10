package com.mooc.setting

import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.ApkUpdateService

@Route(path = Paths.SERVICE_UPDATE_APK)
class UpdateServiceImpl : ApkUpdateService {
    override fun checkApkUpdate(lifecycleOwner: LifecycleOwner, fromHome: Boolean) {
        UpdateManager.checkApkUpdate(lifecycleOwner,fromHome)
    }

    override fun getHasNewVersion() : Boolean{
        return UpdateManager.hasUpdateButIgnor
    }


}