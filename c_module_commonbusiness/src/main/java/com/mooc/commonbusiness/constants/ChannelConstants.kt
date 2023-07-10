package com.mooc.commonbusiness.constants

import com.mooc.common.utils.SystemUtils
import com.mooc.commonbusiness.base.BaseApplication

class ChannelConstants {
    companion object {
        //默认是moocnd，App初始化会动态赋值,testUpgrade//测试升级
        val channelName = BaseApplication.instance?.let { SystemUtils.getChannel(it) } ?: "Moocnd"
    }
}