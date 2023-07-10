package com.mooc.login

import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.mooc.login.manager.WechatManager
import com.mooc.commonbusiness.base.BaseApplication
import com.mooc.login.receiver.AppRegister
import com.tencent.mm.opensdk.constants.ConstantsAPI

class LoginApplication : BaseApplication() {
    override fun init() {
        //通过代码注册微信启动监听
//        val myBroadcast = AppRegister()
//        val intentFilter = IntentFilter()
//        intentFilter.addAction(ConstantsAPI.ACTION_REFRESH_WXAPP);
//        instance?.let {
//            LocalBroadcastManager.getInstance(it).registerReceiver(myBroadcast, intentFilter) };

        //将应用注册到微信
//        WechatManager.regWx()
    }
}