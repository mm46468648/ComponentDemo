package com.mooc.login

import com.alibaba.android.arouter.facade.annotation.Route
import com.mooc.commonbusiness.route.Paths
import com.mooc.commonbusiness.route.routeservice.LoginService
import com.mooc.login.manager.LoginDebugManager
import com.mooc.login.manager.LoginManager
import com.mooc.login.manager.WechatManager

@Route(path = Paths.SERVICE_LOGIN)
class LoginServiveImpl : LoginService {
    override fun registerWx() {
        WechatManager.regWx()
    }


    override fun toLogin(onSuccess: (() -> Unit)?) {
        LoginManager.toWxLogin(onSuccess)
    }

    override fun toWeixinProgram() {
        WechatManager.toWxLaunchMiniProgram()
    }

    override fun loginDebug(hashMap: HashMap<String, String>) {
        LoginDebugManager.postTestLogin(hashMap)
    }


}