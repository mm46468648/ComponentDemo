package com.mooc.commonbusiness.model.xuetang

/**
 * 小节播放列表信息
 * 有可能是多条视频资源
 */
data class SequentialPlayList(var verticals : List<SequentialDetail>)

data class SequentialDetail(
        var display_name : String,    // (播放类型)
        var children : List<SequentialChildren>        //里面有可能是多段视频
)

data class SequentialChildren(
        var id : String,
        var source : String       //通过这个sourse，获取视频播放地址
)