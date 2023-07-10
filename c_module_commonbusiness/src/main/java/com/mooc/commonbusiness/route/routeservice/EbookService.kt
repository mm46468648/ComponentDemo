package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface EbookService : IProvider {
    override fun init(context: Context?) {
    }

    fun initSdk()
    fun setDisallowPrivacy(boolean: Boolean)
    fun turnToZYReader(context : Context,id : String)
}