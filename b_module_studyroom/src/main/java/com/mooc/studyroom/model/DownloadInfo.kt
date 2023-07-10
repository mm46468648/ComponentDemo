package com.mooc.studyroom.model

/**
 * 下载合集信息
 */
class DownloadCollectionInfo {
    var id : String = ""    //资源id
    var name : String = ""     //合集名字
    var cover : String = ""  //合集封面
    var size : Long = 0  //合集大小
    var num : Int = 0   //合集数量
    var type : String=""  //下载类型
    var ebookSize : String = ""   //电子书特殊字段，直接读取文件大小
    var ebookId : String = ""//电子书进入阅读使用的id
}