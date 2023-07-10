package com.mooc.studyroom.model

/**
 * 老的课程下载数据bean
 */
class OldCourseDownloadBean {
    var _id : String = ""     //使用逗号分割，封面，
    //http://image.zhihuishu.com/testzhs/createcourse/COURSE/201603/0558b16f95724194b041d2666d036308.jpg,课程背景,2038794,食品保藏探秘
    var uri : String = ""           //网络地址
    var path : String = ""           //下载路径
    var status : Int = 0           //下载状态
    var size : Int = 0           //下载状态

}