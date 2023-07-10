package com.mooc.course.manager

import android.annotation.SuppressLint
import android.app.Activity
import android.text.TextUtils
import com.lxj.xpopup.XPopup
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.manager.BaseObserver
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.course.CourseApi
import com.mooc.course.model.VideoTipBean
import com.mooc.course.ui.pop.VideoContinueDialog
import java.lang.ref.WeakReference
import java.util.*

/**
 * 视频播放行为管理类
 */
object VideoActionManager {

    //action事件
    const val ACTION_PLAY = "play"
    const val ACTION_END = "end"
    const val ACTION_CONTINUE = "continue"
    const val ACTION_BREAK = "break"

    var classId: String = ""
    var classRoomId: String = ""

    var totalPlayTime = 0 //播放时长

    var context: WeakReference<Activity>? = null

    var timerStatus = false //是否正在倒计时
    /**
     * 定时器
     */
    private val timer: Timer = Timer("videoPlayTimer")

    /**
     * 定时任务
     */
    private var timerTask: MyTimeTask? = null

    /**
     * 上传用户动作
     */
    @SuppressLint("CheckResult")
    fun postAction(action: String) {

        when (action) {
            ACTION_PLAY, ACTION_CONTINUE -> {
                startTime()
            }
            ACTION_END, ACTION_BREAK -> {
                pauseTime()
            }
        }
        val map = HashMap<String, String>()
        map["action"] = action
        map["t"] = System.currentTimeMillis().toString()
        //roomId不为空时，传值
        if (!TextUtils.isEmpty(classRoomId)) {
            map["room_id"] = classRoomId
        }
        ApiService.getRetrofit().create(CourseApi::class.java)
            .videoPlayAction(classId, map)
            .compose(RxUtils.applySchedulers())
            .subscribe({

            }, {
                loge(it.message.toString())
            })
    }

    /**
     * 开启新的计时
     */
    fun startTime() {
        timerStatus = true
        //上报当前action
        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        totalPlayTime = 0
        timerTask = MyTimeTask()
        timer.schedule(timerTask, 1000, 1000)
    }

    /**
     * 停止计时
     */
    fun pauseTime() {
        timerStatus = false
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
    }

    /**
     * 记录播放时常
     */
    class MyTimeTask : TimerTask() {
        override fun run() {
            totalPlayTime++
//            loge("当前播放计时:${totalPlayTime}")
            //每3分钟上报一次
            if (totalPlayTime >= 30 * 60) {
//            if (totalPlayTime >= 1 * 60) {
                pauseTime()
                getTip()
            }
        }
    }

    fun release() {
        pauseTime()
        context = null
        classId = ""
        classRoomId = ""
    }

    /**
     * 获取连续观看30分钟提示文案
     * 提示之前打一个end的点
     */
    fun getTip() {
        postAction(ACTION_END)
        val map = HashMap<String, String>()
        map["t"] = System.currentTimeMillis().toString()
        //roomId不为空时，传值
        if (!TextUtils.isEmpty(classRoomId)) {
            map["room_id"] = classRoomId
        }


        ApiService.getRetrofit().create(CourseApi::class.java)
            .courseVideoTip(classId, map)
            .compose(RxUtils.applySchedulers())
            .subscribe(object : BaseObserver<HttpResponse<VideoTipBean>>(context?.get()) {
                override fun onSuccess(t: HttpResponse<VideoTipBean>?) {
                    val data = t?.data
                    showDialog(data)
                }

                override fun onFailure(code: Int, message: String?) {
                    super.onFailure(code, message)
                    showDialog(null)
                }
            })
    }

    /**
     * 展示弹窗
     */
    fun showDialog(videoTipBean: VideoTipBean?) {
        context?.get()?.let {
            XPopup.Builder(it)
                .asCustom(
                        VideoContinueDialog(it, videoTipBean, {
                        postAction(ACTION_BREAK)
                        it.finish()
                        }, {
                        postAction(ACTION_CONTINUE)
                        postAction(ACTION_PLAY)
                        }
                    )
                )
                .show()
        }
    }
}