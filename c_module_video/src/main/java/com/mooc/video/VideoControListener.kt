package com.mooc.video

/**
 * 视频控制器回调
 */
interface VideoControListener {

    fun onShowController(b:Boolean)

    /**
     * 点击重新播放
     */
    fun onClickReply()

}