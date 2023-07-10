package com.mooc.audio.manager

import com.mooc.common.ktextends.loge
import java.util.*


/**
 * 音频定时关闭
 * 倒计时管理类
 */
class TrackCloseTimerManager {

    var currentTimeCallBack: ((t: Long) -> Unit)? = null

    /**
     * 定时器
     */
    private val timer: Timer = Timer("traclCloseTimer")

    /**
     * 定时任务
     */
    private var timerTask: MyTimeTask? = null

    var totalTime = 0L     //单位秒

    /**
     * 开始计时
     * @param totalTime 单位秒
     */
    fun startTime(totalTime: Long) {
        loge("开始计时: $totalTime")
        this.totalTime = totalTime

        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        timerTask = MyTimeTask()
        timer.schedule(timerTask, 1000, 1000)

    }

    /**
     * 停止计时
     * 将选中位置置为未开始
     */
    fun stopTime() {
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
        XiMaUtile.getInstance().currentCloseTimerIndex = -1
        currentTimeCallBack = null
        mTimeCloseListener = null
    }

    inner class MyTimeTask : TimerTask() {
        override fun run() {
            currentTimeCallBack?.invoke(totalTime)
            mTimeCloseListener?.selectTime(totalTime)
            if(totalTime <= 0){ //为0的时候，暂停音频播放，并停止计时
//                TrackPlayManger.pause()

                XiMaUtile.getInstance().pause()
                stopTime()
            }
            totalTime--
        }
    }

    var mTimeCloseListener : TimeCloseListener? = null

    interface TimeCloseListener {
        fun selectTime(t: Long)
    }

}