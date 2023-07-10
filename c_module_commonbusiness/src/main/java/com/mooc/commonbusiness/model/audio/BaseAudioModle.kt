package com.mooc.commonbusiness.model.audio

/**
 * BaseAudioModle
 */
interface BaseAudioModle {
    val id : String
    val trackTitle : String
    val albumTitle : String
    val albumId : String
    val playUrl : String
    val coverUrl : String
    val lastDuration : Long      //上次播放进度
    val totalDuration : Long      //总进度
    val resourceType : Int  //区分是自建音频还是喜马拉雅音频
}