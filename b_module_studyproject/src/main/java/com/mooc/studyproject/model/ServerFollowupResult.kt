package com.mooc.studyproject.model

/**
 * 记录
 * 后台评测结果是否显示过
 */
data class ServerFollowupResult(

    var id:String= "",     //跟读资源id+内容id
    var show:Boolean = false,
    var notUploadFilePath:String = ""   //未上传的文件地址
)