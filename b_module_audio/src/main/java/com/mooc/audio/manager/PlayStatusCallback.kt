package com.mooc.audio.manager

interface PlayStatusCallback {
    /**
     * 准备播放
     * 回传音频时长
     */
    fun onPrepare(duration: Int)

    /**
     * 播放完毕
     */
    fun onPlayComplete()

    /**
     * 播放中
     * @param currentPosition 当前播放位置
     * @param totalDuration 总时长
     */
    fun onPlaying(currentPosition:Int,totalDuration:Int)

    /**
     * 暂停中
     */
    fun onPause()
}