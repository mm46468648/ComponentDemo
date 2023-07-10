package com.moocnd.`in`.militarycerebrum

import com.mooc.battle.GameConstant
import com.mooc.common.ktextends.logd
import java.util.*


/**
 * 音频定时关闭
 * 倒计时管理类
 */
class AnswerTimeManager {

    var TAG = AnswerTimeManager::class.simpleName

    var currentTimeCallBack: ((t: Int) -> Unit)? = null
        set(value) {
            field = value
        }

    /**
     * 作答时间定时器
     */
    private val timer: Timer = Timer("AnswerTimer")

    /**
     * 定时任务
     */
    private var timerTask: MyTimeTask? = null

    var totalTime = 30    //单位秒

//    /**
//     * 开始计时
//     * @param totalTime 单位秒
//     */
//    fun startTime() {
//        totalTime = 30
//        AnyExtentionKt.logd(TAG,"开始计时: $totalTime")
//        this.totalTime = totalTime
//
//        if (timerTask != null) {
//            timerTask?.cancel() //将原任务从队列中移除
//        }
//        timer.purge()
//        timerTask = MyTimeTask()
//        timer.schedule(timerTask, 0, 1000)
//
//    }

    /**
     * 开始计时
     * @param totalTime 单位秒
     */
    fun startTime(t: Int = 30) {
        totalTime = t * GameConstant.CUT_DOWN_UNIT
        logd("开始计时: $totalTime")
        this.totalTime = totalTime

        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        timerTask = MyTimeTask()
        timer.schedule(timerTask, 0, 1000L / GameConstant.CUT_DOWN_UNIT)

    }

    /**
     * 开始计时
     * @param totalTime 单位秒
     * @param 回调频率 单位毫秒
     */
    fun startTime(t: Int,period:Long) {
        totalTime = t
        logd("开始计时: $totalTime")
        this.totalTime = totalTime

        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        timerTask = MyTimeTask()
        timer.schedule(timerTask, 0, period)

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


    }

    inner class MyTimeTask : TimerTask() {
        override fun run() {
            currentTimeCallBack?.invoke(totalTime)
            if (totalTime <= 0) {
                stopTime()
            }
            totalTime--
        }
    }


}