package com.mooc.audio.manager

import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import java.util.*


/**
 * 音频增加积分
 * 倒计时管理类
 */
class TrackAddScoreTimerManager {

    var currentTimeCallBack: (( title: String, type: String, url: String) -> Unit)? = null


    /**
     * 定时器
     */
    private val timer: Timer = Timer("trackPlayTimer")

    /**
     * 定时任务
     */
    private var timerTask: MyTimeTask? = null

    var totalTime = 0L     //单位秒


    /**
     * 开始计时
     * @param totalTime 单位秒
     */
    fun startTime(title: String, type: String, url: String) {
        this.totalTime = (30 + Random().nextInt(60)).toLong()
        loge("开始计时: $totalTime resourceId: ${url}" )

        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        timerTask = MyTimeTask(title,type,url)
        timer.schedule(timerTask, 1000, 1000)

    }

    /**
     * 停止计时
     */
    fun stopTime() {
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
    }

    inner class MyTimeTask constructor(var title: String,var type: String, var url: String) : TimerTask() {

        override fun run() {
            if(!XiMaUtile.getInstance().isPlaying) return
            if(totalTime <= 0){ //为0的时候
                if(currentTimeCallBack!=null){ //回调不为空，代表当前页面正在展示，需要展示积分弹框
                    currentTimeCallBack?.invoke(title , type, url)
                }else{
                    //学习资源增加积分
                    AddPointManager.startAddPointsTask(null,title , type, url)
                }

                loge("执行上报积分: $title $type $url")
                stopTime()
            }
            totalTime--
        }
    }




}