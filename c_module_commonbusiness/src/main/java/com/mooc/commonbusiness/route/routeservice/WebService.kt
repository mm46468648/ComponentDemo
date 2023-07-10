package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider


interface WebService : IProvider {
    override fun init(context: Context?) {

    }

    fun initX5()
}