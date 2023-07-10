package com.mooc.home.model.todaystudy

class TargetAdjustBean {
    var checkin_status: List<TargetDetial>? = null
    var colum_status: List<TargetDetial>? = null
    var studyplan_status: List<TargetDetial>? = null
    var special_status: List<TargetDetial>? = null
    var most_hot_status: List<TargetDetial>? = null
    var course_status: List<TargetDetial>? = null
    var album_status //音频
            : List<TargetDetial>? = null
    var ebook_status //电子书
            : List<TargetDetial>? = null
    var kanwu_status //刊物
            : List<TargetDetial>? = null
    var task_system_status //任务
            : List<TargetDetial>? = null
}

data class TargetDetial(
    var colum_name: String = "",
    var resource_num:Int = 0,
    var is_open:Int = 0,     //0是关，1打开
    var resource_type : Int= 0,
    var resource_id : Int= 0,
    var header: String = "",
    var plan_name: String = "",
    var limit_num: Int = -1         //接口返回的最大的显示条数
)