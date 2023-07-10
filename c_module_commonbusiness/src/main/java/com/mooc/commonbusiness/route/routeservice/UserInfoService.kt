package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface UserInfoService : IProvider {
    override fun init(context: Context?) {}

//    fun getUserInfo(): UserInfo?

//    fun getAppAccessToken():String?

//    fun getXtToken():String?

}