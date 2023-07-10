package com.mooc.audio.manager

import android.annotation.SuppressLint
import android.content.IntentFilter
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaControllerCompat
import android.support.v4.media.session.PlaybackStateCompat
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.mooc.audio.AudioApi
import com.mooc.audio.receiver.MusicPlayStateBroadcastReceiver
import com.mooc.common.global.AppGlobals
import com.mooc.common.ktextends.loge
import com.mooc.common.utils.rxjava.RxUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.manager.studylog.StudyLogManager
import com.mooc.commonbusiness.model.audio.BaseAudioModle
import com.mooc.commonbusiness.model.studyproject.MusicBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.commonbusiness.utils.incpoints.AddPointManager
import com.mooc.commonbusiness.utils.incpoints.FirstAddPointManager
import com.mooc.music.manager.MusicPlayerManager
import com.mooc.music.manager.MusicPlayerManager.MusicPlayCallBack
import com.mooc.music.model.MusicData
import com.mooc.music.service.contentcatalogs.MusicLibrary
import com.mooc.music.util.ConstantUtils
import org.json.JSONObject


object TrackPlayManger {

    val manager: MusicPlayerManager by lazy {
        MusicPlayerManager.getInstance()
    }

    //    var currentTrackModel: TrackModel? = null
    private var isOwnTrack = false   //是否是自建音频
    var currentTrackModel: BaseAudioModle? = null
        set(value) {
            field = value
            isOwnTrack = currentTrackModel?.resourceType == ResourceTypeConstans.TYPE_ONESELF_TRACK
        }
    var currentAlbumId: String = ""

    //记录当前自建音频id，对应的学习项目id，<key：自建音频id,value学习项目id>
    var ownTrackToStudyProject = hashMapOf<String, String>()

    //
    var metadataCompatMutableLiveData = MutableLiveData<MediaMetadataCompat>()

    var musicProgressMutableLiveData = MutableLiveData<MusicData>()

    var playIdleLiveData = MutableLiveData<Boolean>()

    var musicStateLiveData = MutableLiveData<PlaybackStateCompat>()

    //    public static MutableLiveData<List<TrackBean>> musics = new MutableLiveData<>();
    var mIsPlaying = MutableLiveData<Boolean>()

    var currentSpeed = 1.0f    //当前播放速度

    var audioPointManager = AudioPointManager()


    init {
        //注册广播
        val intentFilter = IntentFilter()
        intentFilter.addAction(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_UPDATE_TIME)
        intentFilter.addAction(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY)
        intentFilter.addAction(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE)
        intentFilter.addAction(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE_COMPLETE)
        intentFilter.addAction(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE_READY)
        intentFilter.addAction(ConstantUtils.ACTION_CLICK_NITIFICATION_CONTENT)
        val musicPlayStateBroadcastReceiver = MusicPlayStateBroadcastReceiver(audioPointManager)
        AppGlobals.getApplication()?.registerReceiver(musicPlayStateBroadcastReceiver, intentFilter)
    }

    //    public static VideoHeatUtil videoHeatUtil;
    fun setPlayCallBack(owner: LifecycleOwner?, playCallBack: MusicPlayCallBack?) {
        owner?.apply {
            metadataCompatMutableLiveData.observe(
                this,
                Observer { mediaMetadataCompat -> playCallBack?.changeMusic(mediaMetadataCompat) })
            musicProgressMutableLiveData.observe(
                this,
                Observer { musicData -> playCallBack?.onSeekTo(musicData) })
            playIdleLiveData.observe(
                this,
                Observer { aBoolean -> playCallBack?.playReady(aBoolean) })
            musicStateLiveData.observe(
                this,
                Observer { playbackStateCompat -> playCallBack?.stateChange(playbackStateCompat) })
            mIsPlaying.observe(this, Observer { aBoolean -> playCallBack?.isPlaying(aBoolean) })
        }

    }

    /**
     * 创建一个新的播放合集
     * @param albumId 音频课id
     */
    fun createMediaMetadataCompats(albumId: String, tracks: ArrayList<BaseAudioModle>) {
        if (currentAlbumId != albumId) { //如果是不同的音频课，就清空
            currentAlbumId = albumId
            MusicPlayerManager.getInstance().clearMusics()
            MusicLibrary.music.clear()
        }

        //移除，所有缓存中非该自建音频id的学习项目id
        val it: MutableIterator<Map.Entry<String, String>> =
            ownTrackToStudyProject.entries.iterator()
        while (it.hasNext()) {
            val item = it.next()
            if (item.key != albumId)
                it.remove()
        }

        appendMediaMetadataCompats(tracks)

    }
    /**
     * 向新的集合添加
     */
    fun appendMediaMetadataCompats(tracks: ArrayList<BaseAudioModle>) {

        //添加到服务
        val mediaController: MediaControllerCompat =
            MusicPlayerManager.getInstance().getContrllerCompat()
        val mediaList = MusicPlayerManager.getMediaItems()

        for (i in tracks.indices) {
            var find = false
            //判断是否已经在播放列表中
            val baseAudioModle = tracks[i]
            mediaList.forEach {
                if (it.mediaId == baseAudioModle.id) {
                    find = true
                }
            }

            if (!find) {
//                loge("当前播放列表不存在，插入${baseAudioModle.id}${baseAudioModle.trackTitle}")
                createMediaMetadata(baseAudioModle)
            } else {
//                loge("当前播放列表已存在，丢掉${baseAudioModle.id}${baseAudioModle.trackTitle}")
            }

        }


        // Queue up all media items for this simple sample.
        for (mediaItem in MusicPlayerManager.getMediaItems()) {
            mediaController.addQueueItem(mediaItem.description)
        }
    }


    /**
     * 创建单个音频
     */
    private fun createMediaMetadata(track: BaseAudioModle) {
        val writer = if (track is MusicBean) "自建音频" else "喜马拉雅"

        loge("插入一个音频id: ${track.id} title: ${track.trackTitle} lastPosition: ${track.lastDuration}")
        MusicLibrary.createMediaMetadataCompat(
            track.id + "",
            track.trackTitle,
            track.coverUrl,
            track.albumId,
            track.lastDuration,
            track.totalDuration,
            track.playUrl,
            writer,
            track.id + "",
            track.resourceType.toLong(),
            track.trackTitle
        )
    }


    fun playWithMediaId(track: BaseAudioModle) {
        //如果是相同的音频，并且正在播放，则不必重新播放
        val currentMedioID =
            manager.currentPlayMusic?.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)
//        loge("current play: ${currentMedioID}")
        if (manager.currentPlayMusic != null && currentMedioID == track.id && mIsPlaying.value == true) {
            return
        }

        currentTrackModel = track


//        createMediaMetadata(track)
        MusicPlayerManager.getInstance().playMediaId(track.id)

        //设置默认倍速
        manager.setSpeed(currentSpeed)
    }

    fun seekTo(progress: Long) {
        if (manager.currentPlayMusic != null) {
            manager.seekTo(progress)
        }
    }

    fun play() {
        manager.play()
    }

    fun pause() {
        postCurrentPlayDuration()
        manager.pause()
    }

    /**
     * 设置倍放速度
     */
    fun setSpeed(speed: Float) {
        if (currentSpeed == speed) {
            return
        }
        currentSpeed = speed
        manager.setSpeed(speed)
    }

    var mTrackCloseTimer: TrackCloseTimerManager? = null       //音频定时关闭记时
    var mTrackScoreTimer: TrackAddScoreTimerManager? = null       //音频播放加分记时
    var currentCloseTimerIndex = -1  //当前倒计时选中索引,-1代码不开启，或者时间停止

    /**
     * 设置定时器定时播放
     * 定时时间
     */
    fun setTimer(time: Long, timeCallBack: ((t: Long) -> Unit)) {
        if (mTrackCloseTimer == null) {
            mTrackCloseTimer = TrackCloseTimerManager()
        }

        //根据定时的时间设置索引
        currentCloseTimerIndex = when (time) {
            10 * 60L -> 0
            20 * 60L -> 1
            30 * 60L -> 2
            60 * 60L -> 3
            90 * 60L -> 4
            else -> -1
        }
        mTrackCloseTimer?.currentTimeCallBack = timeCallBack
        mTrackCloseTimer?.startTime(time)
    }

    /**
     * 停止计时
     */
    public fun stopTimer() {
        currentCloseTimerIndex = -1
        mTrackCloseTimer?.stopTime()
        mTrackCloseTimer = null
    }


    /**
     * 释放所有资源
     * 取消通知栏
     */
    fun release() {
        postCurrentPlayDuration()

        //清空音频课列表
//        AlbumAudioListManager.clearList()
        MusicPlayerManager.getInstance().clearMusics()
        MusicLibrary.music.clear()
        ownTrackToStudyProject.clear()

        mTrackScoreTimer?.stopTime()
        currentTrackModel = null
        mIsPlaying.postValue(false)
    }


    /**
     * 在播放30s到90s之前随机一个时间，倒计时后上报积分
     */
    fun postAddScore(type: Int,id:String,title: String) {
        if (mTrackScoreTimer == null) {
            mTrackScoreTimer = TrackAddScoreTimerManager()
        }
        mTrackScoreTimer?.startTime(title, type.toString(), id)
    }


    /**
     * 上传当前播放音频id
     * 和音频播放进度
     */
    @SuppressLint("CheckResult")
    fun postCurrentPlayDuration() {
        val requestData = JSONObject()
        requestData.put("track_id", audioPointManager.currentTrackId)
//        requestData.put("duration", audioPointManager.currentMusicData?.progress?.div(1000) ?: 0)
        ApiService.getRetrofit().create(AudioApi::class.java)
            .postAudioHistory(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers())
            .subscribe({

            }, {
//                loge(
//                    "error msg: ${it.toString()}",
//                    "currentMusic progress:" + audioPointManager.currentMusicData?.progress
//                )
            })
    }

    /**
     * 上传当前播放音频id
     * 和音频播放进度
     * @param trackId 音频id
     * @param duration 时长 (单位s)
     */
    @SuppressLint("CheckResult")
    fun postCurrentPlayDuration(trackId: String, duration: Long) {
        val requestData = JSONObject()
        requestData.put("track_id", trackId)
        requestData.put("duration", duration)
        ApiService.getRetrofit().create(AudioApi::class.java)
            .postAudioHistory(RequestBodyUtil.fromJson(requestData))
            .compose(RxUtils.applySchedulers())
            .subscribe({

            }, {
//                loge(
//                    "error msg: ${it.toString()}",
//                    "currentMusic progress:" + audioPointManager.currentMusicData?.progress
//                )
            })
    }
}