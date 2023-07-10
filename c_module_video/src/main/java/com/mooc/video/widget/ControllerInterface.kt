package com.mooc.video.widget

import com.google.android.exoplayer2.ui.PlayerControlView

/**
 * 控制器接口
 */
interface ControllerInterface {

    fun isLand():Boolean

    fun getControllerView():PlayerControlView

}