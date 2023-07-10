package com.mooc.my.model

import com.mooc.commonbusiness.model.UserInfo

/**
 * 签到get ，post 通用
 */
data class CheckInDataBean(
        var days_list: ArrayList<Long>,
        var today_checkin_count: String,
        var has_checkin: Boolean,
        var user_info: UserInfo,
        var score: Int,
        var continue_days: String, //连续签到天数
        var checkins: ArrayList<CheckPeopleBean>,

        var new_chance_days: String,
        var flag: String,
        var hold_on_days: String,
        var make_up_date: String,           //最近一个补签的日期，格式2021.09.21
        var task_status: String,   //0未开始任务，1。已开始任务2。任务成功或者失败
        var old_task_success: Boolean = false,



        var msg: String = "",
        val random_score: Int = 0,      //签到应该获取的分值
        val extra_score: Int = 0,
        val special_score :Int = 0,  //八一特别得分
        val success: Boolean = false,
        val check_count: String = "",
        val checkin_medal_img: String = ""
)

data class CheckPeopleBean(
        var random_score: Int = 0,
        val user_name: String = "",
        val extra_score: Int = 0
)