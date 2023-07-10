package com.mooc.course.manager

import android.annotation.SuppressLint
import android.text.TextUtils
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.course.CourseApi
import com.mooc.course.model.LessonInfo
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.*

/**
 * 视频打点
 */
object VideoPointManger {

    val hashMap = hashMapOf<String, String>()
    var currentZHSLessionInfo: LessonInfo? = null
    var totalPlayTime = 0 //播放时长


    /**
     * 定时器
     */
    private val timer: Timer = Timer("readPageTimer")

    /**
     * 定时任务
     */
    private var timerTask: MyTimeTask? = null


    /**
     * 初始化章节信息
     */
    fun initChapterInfo(courseId: String, videoNum: String, ts: String, token: String) {
        hashMap.put("c", courseId)
        hashMap.put("videoNum", videoNum)
        hashMap.put("ts", ts)
        hashMap.put("token", token)
    }

    /**
     * 切换章节
     */
    fun changeLesson(currentZHSCourseBean: LessonInfo) {
        if (this.currentZHSLessionInfo == null  //null直接切
                || this.currentZHSLessionInfo?.lessonId != currentZHSCourseBean.lessonId //先比较lessonId
                || this.currentZHSLessionInfo?.lessonVideoId != currentZHSCourseBean?.lessonVideoId) {
            //再比较lessonVideoId
            //切换章节
            pause()
            this.currentZHSLessionInfo = currentZHSCourseBean
        }


    }

    @SuppressLint("CheckResult")
    fun postPoint() {
//        loge("上报计时:${totalPlayTime}")

        if (currentZHSLessionInfo == null || totalPlayTime == 0) return

        hashMap.put("t", totalPlayTime.toString())     //上报的时候，看了多久
        hashMap.put("u", GlobalsUserManager.uid ?: "")        //用户id
        hashMap.put("s", currentZHSLessionInfo?.lessonId?:"")
        hashMap.put("cp", currentZHSLessionInfo?.chapterId?:"")
        //这个地方这样写的原因是保证没有值得时候传"0",不要动
        hashMap.put("ls", if (TextUtils.isEmpty(currentZHSLessionInfo?.lessonVideoId)) "0" else currentZHSLessionInfo?.lessonVideoId
            ?: "0")
        hashMap.put("v", currentZHSLessionInfo?.videoId?:"")
        hashMap.put("d", currentZHSLessionInfo?.videoLength.toString())


        ApiService.getRetrofit().create(CourseApi::class.java)
                .zhsVideoPlayHeartBeat(hashMap)
                ?.enqueue(object : Callback<ResponseBody?> {
                    override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>) {
                        totalPlayTime = 0
//                        if (DebugUtil.debugMode) {
//                            Toast.makeText(BaseApplication.instance, "===response===>" + response.toString(), Toast.LENGTH_LONG).show()
//                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
//                        if (DebugUtil.debugMode) {
//                            Toast.makeText(BaseApplication.instance, "===onFailure===>", Toast.LENGTH_LONG).show()
//                        }
                        totalPlayTime = 0
                    }
                })
    }


    /**
     * 开启新的计时
     */
    fun start() {
        //上报当前action
        VideoActionManager.postAction(VideoActionManager.ACTION_PLAY)
        if (timerTask != null) {
            timerTask?.cancel() //将原任务从队列中移除
        }
        timer.purge()
        totalPlayTime = 0
        timerTask = MyTimeTask()
        timer.schedule(timerTask, 1000, 1000)

    }

    /**
     * 暂停计时
     * 并上报当前时长
     */
    fun pause() {
        //上报当前action
        VideoActionManager.postAction(VideoActionManager.ACTION_END)
        //上报一下进度
        postPoint()
        if (timerTask != null) {
            timerTask?.cancel()
            timerTask = null
        }
    }

    /**
     * 退出释放
     */
    fun release() {
        pause()
        totalPlayTime = 0
        currentZHSLessionInfo = null
    }

    /**
     * 记录播放时常
     */
    class MyTimeTask : TimerTask() {
        override fun run() {
            totalPlayTime++
//            loge("当前播放计时:${totalPlayTime}")
            //每3分钟上报一次
            if (totalPlayTime >= 3 * 60) {
                postPoint()
            }
        }
    }
}