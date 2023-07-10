package com.mooc.audio.model

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 音频打点数据模型
 */
@Entity
class AudioPoint {
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0
    //    String i, et, p, cp, fp, tp, sp, ts, u, c, v, cc, d, pg, sq;
    var i: String = "" //页面编号；取值<videoid>_<随机数>
    var et: String = "" //终端类型；
    var cp: String = "" //心跳间隔，当前默认为5s；
    var fp: String = ""//播放状态
    var tp: String = ""  //正常情况下，进度同cp,如果是拖拽事件，记录拖拽起点
    var sp: String = "" //拖拽终点
    var ts: String = "" //当前进度
    var p: String = ""   //播放速度
    var u: String = ""//当前时间戳
    var c: String = ""//用户id
    var cc: String = "" //固定值 (s_track 喜马拉雅音频 , s_audio自建音频)，目前只统计了自建音频
    var d: String = ""//audioId
    var v: String = ""   //audioId
    var pg: String = ""   //总时长
    var sq: String = ""   //序列号，每次自增1，当切换音频的时候重置
}