package com.mooc.audio.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Parcelable
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import com.alibaba.android.arouter.launcher.ARouter
import com.mooc.audio.manager.AudioDbManger
import com.mooc.audio.manager.AudioPointManager
import com.mooc.audio.manager.TrackPlayManger
import com.mooc.common.bus.LiveDataBus
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LiveDataBusEventConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.route.Paths
import com.mooc.music.model.MusicData
import com.mooc.music.util.ConstantUtils

/**
 * 音乐播放状态监听广播
 * @param audioPointUtil 打点工具类
 */
class MusicPlayStateBroadcastReceiver(val audioPointUtil: AudioPointManager) : BroadcastReceiver() {


//    val audioPointUtil = AudioPointManager()

    override fun onReceive(context: Context?, intent: Intent?) {

        val action = intent?.action
        when (action) {
            ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE -> {
                onUpdateState(intent)

            }
            ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE_READY -> {
                //暂时是空实现
            }

            ConstantUtils.DATA_OBSERVER_KEY_MUSIC_UPDATE_TIME -> {
                onUpdateProgress(intent)
            }

            ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE_COMPLETE -> {
                onUpdateComplete()
            }

            ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY -> {
                changeMusic(intent)
            }
            ConstantUtils.ACTION_CLICK_NITIFICATION_CONTENT -> {

                //特殊情况下会产生没有audio参数，兼容一下进入主页
                if (intent.hasExtra(IntentParamsConstants.AUDIO_PARAMS_ID)) {
                    //打开音频播放页面
                    val trackId = intent.getStringExtra(IntentParamsConstants.AUDIO_PARAMS_ID)
                    val albumId = intent.getStringExtra(IntentParamsConstants.ALBUM_PARAMS_ID)
                    val audio_type = intent.getStringExtra("audio_type") ?: "喜马拉雅"

                    if (audio_type == "自建音频") {
                        ARouter.getInstance().build(Paths.PAGE_AUDIO_OWN_BUILD_PLAY)
                                .withString(IntentParamsConstants.AUDIO_PARAMS_ID, trackId)
                                .navigation()
                    } else {
                        ARouter.getInstance().build(Paths.PAGE_AUDIO_PLAY)
                                .withString(IntentParamsConstants.AUDIO_PARAMS_ID, trackId)
                                .withString(IntentParamsConstants.ALBUM_PARAMS_ID, albumId)
                                .navigation()
                    }
                } else {
                    ARouter.getInstance().build(Paths.PAGE_HOME).navigation()
                }


//                loge("open AudioActivity id:${stringExtra}}")
            }
        }

//        loge(action.toString(), intent?.extras ?: "")
    }

    /**
     * 更新完成
     */
    private fun onUpdateComplete() {
        TrackPlayManger.postCurrentPlayDuration()
        audioPointUtil.postEtState(AudioPointManager.STATUS_END)
    }

    /**
     * 音频状态改变事件
     * 切换音乐
     */
    private fun changeMusic(intent: Intent) {
        val mediaMetadata = intent.extras?.getParcelable(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_PLAY) as MediaMetadataCompat?
        mediaMetadata?.let {
            val trackId = it.getDescription().getMediaId() ?: ""
            val trackTitle = it.getDescription().title.toString() ?: ""
            //如果是切换音频,记录上一个播放的点
            if (audioPointUtil.currentTrackId.isNotEmpty() && trackId != audioPointUtil.currentTrackId) {
//                TrackPlayManger.postCurrentPlayDuration(audioPointUtil.currentTrackId,
//                        TrackPlayManger.audioPointManager.currentMusicData?.progress?.div(1000)
//                                ?: 0)
            }

            //新播放的音频
            if (trackId.isNotEmpty() && trackId != audioPointUtil.currentTrackId && TrackPlayManger.currentAlbumId.isNotEmpty()) {


                TrackPlayManger.metadataCompatMutableLiveData.setValue(it)
                audioPointUtil.currentTrackId = trackId

                AudioDbManger.updateLastListenAudio(TrackPlayManger.currentAlbumId, trackId)
                loge("发送需要定位的音频: ${trackId}")
                LiveDataBus.get().with(LiveDataBusEventConstants.EVENT_LISTEN_TRACK_ID).postStickyData(trackId)

                //切换音频发送上传学习记录接口
//                TrackPlayManger.postStudyLog(TrackPlayManger.currentTrackModel?.resourceType
//                        ?: ResourceTypeConstans.TYPE_TRACK, trackId, trackTitle)
                //
                TrackPlayManger.postCurrentPlayDuration(trackId, 0)

                //喜马拉雅的音频要跳转到上次的进度
                val resourceType = it.getLong(MediaMetadataCompat.METADATA_KEY_BT_FOLDER_TYPE)
                if (resourceType == ResourceTypeConstans.TYPE_TRACK.toLong()) {
                    var lastDuration = it.getString(MediaMetadataCompat.METADATA_KEY_GENRE).toLong()
                    val totalDuration = it.getLong(MediaMetadataCompat.METADATA_KEY_DURATION)

                    if (lastDuration >= totalDuration) {
                        lastDuration = 0
                    }

                    if (lastDuration <= 0) {
                        lastDuration = 0
                    }
                    TrackPlayManger.seekTo(lastDuration * 1000)
                }

            }
            ""
        }
    }

    /**
     * 更新进度
     */
    private fun onUpdateProgress(intent: Intent) {
        val musicData = intent.extras?.getParcelable<Parcelable>(ConstantUtils.DATA_OBSERVER_KEY_MUSIC_UPDATE_TIME) as MusicData?
        musicData?.let {
            TrackPlayManger.musicProgressMutableLiveData.setValue(it)
//            audioPointUtil.currentMusicData = musicData

            audioPointUtil.postEtState(AudioPointManager.STATUS_HEART)
        }
//        loge("播放进度: ${musicData?.progress}")
        //打点，更新ui

    }

    /**
     * 更新播放状态
     */
    private fun onUpdateState(intent: Intent) {
        val playbackState = intent.extras?.getParcelable(ConstantUtils.DATA_OBSERVER_KEY_MEDIA_STATE) as PlaybackStateCompat?
        playbackState?.let {
            TrackPlayManger.musicStateLiveData.setValue(it)

            if (playbackState.state == PlaybackStateCompat.STATE_PLAYING) {
                TrackPlayManger.mIsPlaying.setValue(true)
                audioPointUtil.postEtState(AudioPointManager.STATUS_PLAY)
            } else {
                TrackPlayManger.mIsPlaying.setValue(false)
                audioPointUtil.postEtState(AudioPointManager.STATUS_PAUSE)
            }
        }


//        loge("播放状态: ${playbackState?.state}")
        //状态打点，更新ui
    }
}