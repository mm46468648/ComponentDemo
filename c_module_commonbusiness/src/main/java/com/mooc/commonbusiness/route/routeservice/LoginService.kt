package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.mooc.commonbusiness.base.BaseApplication

/**
 * 微信登录相关服务
 */
interface LoginService : IProvider {
    override fun init(context: Context?) {}

    ///应用注册到微信
    fun registerWx()
    /**
     * 登录
     * @param onSuccess 成功回调
     */
    fun toLogin(onSuccess:(()->Unit)? = null)

    /**
     * 去微信小程序
     */
    fun toWeixinProgram()


    fun loginDebug(hashMap: HashMap<String,String>)
}