package com.mooc.studyproject.model

/**
 * 跟读资源列表模型
 */
data class FollowupResourse(
    var results:List<FollowupData>,
    var is_evaluating: Boolean,     //是否在评测中

)

data class FollowupData(
        var id: String,//"跟读内容id",
        var repeat_books_id: String,//"跟读类id",
        var context: String,//"这里是文稿内容...",
        var status: String = "", // （1读完达标，0未达标）
        var finish_num: Int = 0,// 0,  // 完整度百分制
        var fluent_num: Int = 0, // 0  // 流畅度百分制
        var is_fail: Boolean =false, //后台评测失败为True, 其他情况为False
        var is_backend:Boolean=false,        //是否是在后台评测
        var wait_time: Int = 0,      //大约需要等待的时间
        var repeat_status : LoopBean  //新模型，返回等待时间，状态，进度等

)
