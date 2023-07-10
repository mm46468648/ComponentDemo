package com.mooc.audio.manager

import android.annotation.SuppressLint
import android.text.TextUtils
import com.mooc.audio.AudioApi
import com.mooc.audio.db.AudioDatabase
import com.mooc.audio.model.AudioPoint
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.GsonManager
import com.mooc.common.utils.Md5Util
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.music.audioheat.FileNameGenerator
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.random.Random

/**
 * 音频打点工具类
 */
open class AudioPointManager {

    companion object {
        const val STATUS_PLAY = "play"
        const val STATUS_PAUSE = "pause"
        const val STATUS_HEART = "heartbeat"
        const val STATUS_END = "videoend"
        const val STATUS_SEEKING = "seeking"
    }


    val hashMap = hashMapOf<String, String>()

    var pg = "" //页面编号；取值<videoid>_<随机数>
    val p = "android" //终端类型；
    val i = 5 //心跳间隔，当前默认为5s；
    var et = "heartbeat"   //播放状态
    var fp = 0      //正常情况下，进度同cp,如果是拖拽事件，记录拖拽起点
    var tp = 0      //拖拽终点
    var cp = 0    //当前进度
    var sp = 1.0f    //播放速度
    var ts = System.currentTimeMillis().toString() //当前时间戳
    var u = ""   //用户id
    val c = "s_audio";    //固定值 (s_track 喜马拉雅音频 , s_audio自建音频)，目前只统计了自建音频
    var v = ""   //audioId(还有可能拼接了学习项目id)
    var cc = ""   //audioId
    var d = 0   //总时长
    var sq = 1   //序列号，每次自增1，当切换音频的时候重置

    //    var currentTrackModel: TrackModel? = null
//    var currentTrackModel: BaseAudioModle? = null
    var currentTrackId = "";       //记录当前播放id
//    var currentMusicData: MusicData? = null       //记录当前播放进度与时常


    var lastPostTime = 1

    @SuppressLint("CheckResult")
    fun postPoint() {
        if (currentTrackId.isEmpty()) return

        val getmPlayerManager = XiMaUtile.getInstance().getmPlayerManager() ?: return
        //        cp = currentMusicData?.progress?.div(1000)?.toInt() ?: 0
//        d = currentMusicData?.duration?.div(1000)?.toInt() ?: 0

        cp = getmPlayerManager.playCurrPositon.div(1000)
        d = getmPlayerManager.duration.div(1000)


        //进度数据5秒一打 ,看着进度间隔是(i * 速度)
//        loge("状态: ${et} 时长: ${d} 进度: ${lastPostTime}")
//        if (et == STATUS_HEART && lastPostTime % i != 0) {
//            lastPostTime++
//            return
//        }

        lastPostTime = 1

        if (et != STATUS_SEEKING) {   //如果不是拖拽的点，fp = cp
            fp = cp
            tp = cp
        } else {
            cp = fp        //如果是拖拽，cp等于拖拽前的点，cp = fp
        }

        if (v != currentTrackId) {    //如果id不同，切换id，重置sq序列,重置所有参数
            v = currentTrackId
            sq = 1
            d = 0
            cp = 0
            fp = 0
            tp = 0
        }

        ts = System.currentTimeMillis().toString() //当前时间戳
        u = GlobalsUserManager.uid ?: "!${getSessionId()}"
        cc = currentTrackId
        pg = v + "_" + get6MD5WithString(Random(Int.MAX_VALUE).nextInt().toString())
        sp = XiMaUtile.getInstance().currentSpeed


        // hashMap.clear()
        hashMap.put("i", i.toString())
        hashMap.put("et", et)
        hashMap.put("p", p)
        hashMap.put("cp", cp.toString())
        hashMap.put("fp", fp.toString())
        hashMap.put("tp", tp.toString())
        hashMap.put("sp", sp.toString())
        hashMap.put("ts", ts)
        hashMap.put("u", u)
        hashMap.put("c", c)

        hashMap.put("v", v)
        hashMap.put("pg", pg)
        //如果学习项目id不为空
        if (XiMaUtile.getInstance().ownTrackToStudyProject.containsKey(v)) {
            hashMap.put("v", "p_${XiMaUtile.getInstance().ownTrackToStudyProject[v]}_${v}")
            hashMap.put(
                "pg",
                "p_${XiMaUtile.getInstance().ownTrackToStudyProject[v]}_${v}_${
                    get6MD5WithString(Random(Int.MAX_VALUE).nextInt().toString())
                }"
            )
        }
        hashMap.put("cc", cc)
        hashMap.put("d", d.toString())
        hashMap.put("sq", sq.toString())

        sq++

//        val host = if (ApiService.getUserNewUrl()) ApiService.NORMAL_HEARTBEAT else ApiService.OLD_HEARTBEAT
//        loge(hashMap)
        val toJson = GsonManager.getInstance().toJson(hashMap)
        val fromJson =
            GsonManager.getInstance().fromJson<AudioPoint>(toJson, AudioPoint::class.java)
//        useProxyOldVideoHeat(hashMap)
        useProxyOldVideoHeat(fromJson)


    }

    /**
     * 新域名打点方法，如果失败则切换到旧域名打点方法
     */
    @SuppressLint("CheckResult")
    private fun useProxyVideoHeat(audioPoint: AudioPoint) {
        val toJson = GsonManager.getInstance().toJson(audioPoint)
        val hashMap =
            GsonManager.getInstance().fromJson<HashMap<String, String>>(toJson, HashMap::class.java)

        ApiService.getRetrofit().create(AudioApi::class.java)
            .audioPlayHeat(hashMap)
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    AudioDatabase.DATABASE?.getAudioPoint()?.delete(audioPoint)
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    onPostError(hashMap)

                }
            })
    }

    /**
     * 旧域名打点方法
     */
    @SuppressLint("CheckResult")
    private fun useProxyOldVideoHeat(audioPoint: AudioPoint) {

        val toJson = GsonManager.getInstance().toJson(audioPoint)
        val hashMap =
            GsonManager.getInstance().fromJson<HashMap<String, String>>(toJson, HashMap::class.java)

        ApiService.getRetrofit().create(AudioApi::class.java)
            .audioPlayHeatOld(hashMap)
            .enqueue(object : Callback<ResponseBody?> {
                override fun onResponse(
                    call: Call<ResponseBody?>,
                    response: Response<ResponseBody?>
                ) {
                    AudioDatabase.DATABASE?.getAudioPoint()?.delete(audioPoint)
                }

                override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                    useProxyVideoHeat(audioPoint)

                }
            })
    }

    /**
     * 将失败打点记录到本地
     */
    fun onPostError(hashMap: HashMap<String, String>) {

        val toJson = GsonManager.getInstance().toJson(hashMap)

//        loge("上传失败音频打点保存本地： ${toJson}")

        val fromJson =
            GsonManager.getInstance().fromJson<AudioPoint>(toJson, AudioPoint::class.java)
        AudioDatabase.DATABASE?.getAudioPoint()?.insert(fromJson)
    }

    /**
     * 将本地的失败打点获取重新上传
     */
    fun postNativiePoint() {
        GlobalScope.launch {
            val findAllPoint = AudioDatabase.DATABASE?.getAudioPoint()?.findAllPoint()
            if (findAllPoint?.isEmpty() == true) return@launch

            flow<AudioPoint> {
                findAllPoint?.forEach {
//            val toJson = GsonManager.getInstance().toJson(it)
//            val fromJson = GsonManager.getInstance().fromJson<HashMap<String, String>>(toJson, HashMap::class.java)
                    loge("上传本地音频打点： ${it}")
                    emit(it)
                }
            }.flowOn(Dispatchers.IO)
                .catch {

                }
                .collect {
                    useProxyOldVideoHeat(it)

                }
        }

    }

    fun postEtState(et: String) {
        this.et = et
        postPoint()
    }

    /**
     * 发送拖拽事件
     */
    open fun postSeek(fp: Int, tp: Int) {
        this.fp = fp
        this.tp = tp
        postEtState(STATUS_SEEKING)
    }


    /**
     * 如果没有获取到userid,使用这个值
     */
    private fun getSessionId(): String {
        return try {
            val randomNum = (Math.random() * 10000).toInt()
            FileNameGenerator.generator(System.currentTimeMillis().toString() + "" + randomNum)
        } catch (e: Exception) {
            System.currentTimeMillis().toString() + ""
        }
    }

    /**
     * 从32位MD5里截取18-24 中的6位
     * @param str
     * @return
     */
    fun get6MD5WithString(str: String?): String {

        return if (TextUtils.isEmpty(str)) {
            ""
        } else Md5Util.getMD5Str(str).substring(18, 24)
    }
}