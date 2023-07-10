package com.mooc.audio.manager

/**
 * 音频打点bean
 */
class AudioPointBean{
    var pg = "" //页面编号；取值<videoid>_<随机数>
    val p = "android" //终端类型；
    val i = 5 //心跳间隔，当前默认为5s；
    var et = "heartbeat"   //播放状态
    val fp = "0"      //进度同cp
    val tp = "0"      //一直是0没变过
    var cp = 0    //进度
    var sp = "1.0"    //播放速度
    var ts = System.currentTimeMillis().toString() //当前时间戳
    var u = ""   //用户id
    val c = "s_audio";    //固定值
    var v = ""   //audioId
    var cc = ""   //audioId
    var d = 0   //总时长
    var sq = 1   //心跳值
}