package com.mooc.commonbusiness.model.studyroom

import com.mooc.commonbusiness.model.UserInfo

/**
 * 学习计划数据模型
 * 使用kotlin解析时间格式字符串会有问题，暂时不用，用java类型的数据bean
 */
@Deprecated("")
data class StudyProject3(
        var plan_name:String,  //计划名称
        var plan_img:String,  //学习计划图片
        var plan_start_users:List<UserInfo>,   //发起人信息
        var plan_starttime:String,   //开始时间
        var plan_endtime:String,   //结束时间
        var plan_num:String,  //计划人数
        var plan_status:String,  //计划状态
        var is_success: Int = 0,  //项目是否成功 1成功，2失败，3提示进度
        var user_do_num: Int = 0,  //项目完成度,已完成
        var source_num: Int = 0,  //项目完成度,总完成度
        var plan_endtime_ts: Long = 0,  //结束时间，单位秒
        var compute_time: Long = 0  //积分结算日期，单位秒


)
