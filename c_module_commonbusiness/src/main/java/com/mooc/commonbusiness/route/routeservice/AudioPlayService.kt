package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * 音频模块播放服务
 */
interface AudioPlayService : IProvider {
    override fun init(context: Context?) {

    }

    fun initSdk()

    fun stopPlay()

    fun setStudyPlanId(ownTrackID: String, studyPlayId: String)

    fun isPlaying(): Boolean

    fun postErrorPoint()
}