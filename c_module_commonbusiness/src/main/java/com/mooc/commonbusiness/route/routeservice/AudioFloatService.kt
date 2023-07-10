package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

interface AudioFloatService : IProvider {
    override fun init(context: Context?) {}

    fun showAudioFloat(activity: com.mooc.commonbusiness.interfaces.AudioBottomPlayable)

    fun hide(activity: com.mooc.commonbusiness.interfaces.AudioBottomPlayable,show:Boolean)

    fun release()
}