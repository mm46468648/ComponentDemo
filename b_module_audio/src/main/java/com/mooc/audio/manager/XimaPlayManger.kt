package com.mooc.audio.manager

import com.mooc.commonbusiness.model.audio.BaseAudioModle
import com.ximalaya.ting.android.opensdk.model.PlayableModel
import com.ximalaya.ting.android.opensdk.model.track.Track



object XimaPlayManger {

     fun convertoXimaTrack(ownList: List<BaseAudioModle>): List<Track> {
        return ownList.map {
            Track().apply {
                this.kind = PlayableModel.KIND_TRACK
                this.trackTitle = it.trackTitle
                this.playUrl32 = it.playUrl
                this.coverUrlMiddle = it.coverUrl
                this.liveRoomId = it.lastDuration
                this.duration = it.totalDuration.toInt()
                this.dataId = it.id.toLong()
            }
        }
    }
}