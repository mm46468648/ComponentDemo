package com.mooc.commonbusiness.route.routeservice

import android.app.Activity
import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider

/**
 * 视频播放行为记录服务
 */
interface VideoActionService : IProvider {
    override fun init(context: Context?) {

    }

    fun setPageInfo(activity: Activity,classId:String,classRoomId:String)

    fun getTimerStatus() : Boolean

    fun startTime()

    fun pauseTime()

    fun release()

}