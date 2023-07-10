package com.mooc

import android.content.Context
import com.mooc.common.ktextends.runOnMainDelayed
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseApplication
import com.mooc.commonbusiness.constants.ChannelConstants

class MoocApplication : BaseApplication() {

    override fun attachBaseContext(base: Context) {
        super.attachBaseContext(base)

//        ZYReaderPluginApi.initPlugWhenAPPAttachBaseContext(this,base)

    }
    override fun init() {
//        val channel: String = instance?.let {
//            WalleChannelReader.getChannel(it)
//        } ?:"refactoring"
//        ChannelConstants.channelName = channel

//        runOnMainDelayed(2000) {
//            toast("渠道: $channel  instance: ${instance}")
//        }
    }
}