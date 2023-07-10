package com.mooc.commonbusiness.model.studyproject

import android.annotation.SuppressLint
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 学习项目(学习计划)
 */
@SuppressLint("ParcelCreator")// 用于处理 Lint 的错误提示
@Parcelize
class StudyPlan(
        var id: String? = null,
        var plan_name: String? = null,
        var plan_subtitle: String? = null,
        var plan_starttime: Long = 0,
        var plan_endtime: Long = 0,
        var plan_rule: String? = null,
        var plan_start_users: ArrayList<DynamicUser>? = null,
        var plan_img: String? = null,
        var plan_order: Int = 0,
        var plan_status: Int = 0,
        var join_end_time: Long = 0,
        var plan_num: Int = 0,
        var is_continus_checkin: Int = 0,
        var is_bet: Int = 0,
        var bet_rules: String? = null,
        var plan_start_users_introduction: String? = null,
        var need_score: Int = 0,
        var limit_num: Int = 0,
        var certificate_id: String? = null,
        var join_start_time: Long = 0,

        var is_review_checkin: Int = 0,
        var source_num: Int = 0,
        var user_do_num: Int = 0,
        var is_success: Int = 0,
        var end_back_score: Int = 0,
        var is_read: Int = 0,
        var user_all_score: Int = 0,
        var is_medal: Int = 0,
        var is_read_medal: Int = 0,
        var medal_link: String? = null,
        var medal_default_link: String? = null,
        var share_status: Int = 0,
        var share_title: String? = null,
        var share_desc: String? = null,
        var share_picture: String? = null,
        var comment_like_status: Int = 0,
        var set_time: Long = 0,
        var plan_mode_status: Int = 0,
        var need_score_status: Int = 0,
        var pop_window_status: Int = 0,
        var pop_desc: String? = null,
        var activity_status: Int = 0,
        var set_activity_time: Long = 0,
        var extra_studyplan_score: Int = 0,
        var is_calculate: Int = 0,
        var num_activity_limit: Int = 0,
        var bet_introduction: String? = null,
        var set_resource_end_time: Long = 0,
        var head_img: String? = null,
        var is_enroll_daterange: Int = 0,//是否显示报名时间

        var is_studyplan_daterange: Int = 0,//是否显示学习项目时间

        var plan_master_speaker: String? = null,
        var plan_master_content: String? = null,
        var plan_master_join_method: String? = null,
        var plan_master_is_code: String? = null,
        var plan_master_code_num: String? = null,
        var plan_master_code_starttime: String? = null,
        var plan_master_code_endtime: String? = null,
        var plan_master_relation: String? = null,
        var before_resource_check_status: String? = null,
        var chengxin_pop_info: String? = null,
        var compute_time: Long = 0,
        var is_set_activity_status: Int = 0,
        var activity_introduction: String? = null,
        var verified_active: Int? = -1,//-1 不可申请证书  0 可申请证书 1 已申请证书 -2 没有证书 不显示
        var is_checkin_remind: Boolean = false,//  是否显示订阅按钮
        var user_subscribe_checkin_remind: Boolean = false,//用户是否订阅了打卡提醒
        var is_open_integrity: Boolean = false,//是否配置诚信积分
        var integrity_score: Int = 0,//诚信积分设置的值
        var is_bind_testpaper: String? = null,//# 是否智能导学
        var finished_intelligent_test:  Boolean? = false,//# 是否完成智能导学测试
        var bind_info:  IntelligentBindInfo? = null,//
        var is_join: Boolean? = false,// # 是否加入学习项目
        var time_mode: Int? = 0,// # 时间是否是永久 1是永久
        var is_set_limit_num: Boolean? = true,// # 不限用户人数，true代表有限制人数
         var reputation: String? = null,//声望值
        val is_show_reputation: String? = null,//是否展示声望值

//        "is_bind_testpaper": 1,
//"bind_info": {
//    "bind_test_paper_id": 654,
//    "test_paper_target": 50,
//    "test_paper_link": "https://www.learning.mil.cn/mobile/test-paper/654?studyProgramId=715&intelligentTest=1",
//    "title": "智能组建测试"
//},
//"finished_intelligent_test": false,
//"is_join": false,

) : Parcelable