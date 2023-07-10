package com.mooc.studyroom.model

/**
 * 老的课程下载数据bean
 */
class OldTrackDownloadBean {
    var id : String = ""     //使用逗号分割，封面，
    //http://image.zhihuishu.com/testzhs/createcourse/COURSE/201603/0558b16f95724194b041d2666d036308.jpg,课程背景,2038794,食品保藏探秘
    var tracktitle : String = ""           //音频标题
    var albumtitle : String = ""           //专辑标题
    var coverurllarge : String = ""           //专辑封面
    var albumid : String = ""           //音频id

    var downloadurl : String = ""           //网络地址
    var downloadedsavefilepath : String = ""           //下载路径
    var dataid : String = ""           //音频id

}