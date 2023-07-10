package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.alibaba.android.arouter.facade.template.IProvider

interface ApkUpdateService : IProvider{
    override fun init(context: Context?) {}
    fun checkApkUpdate(lifecycleOwner: LifecycleOwner, fromHome:Boolean = true)

    fun getHasNewVersion() : Boolean
}