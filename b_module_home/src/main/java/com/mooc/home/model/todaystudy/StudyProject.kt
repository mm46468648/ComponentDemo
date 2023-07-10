package com.mooc.home.model.todaystudy

import com.mooc.commonbusiness.model.UserInfo

/**
 * 学习计划数据模型
 */
data class StudyProject(
        var plan_name:String,  //计划名称
        var plan_img:String,  //学习计划图片
        var plan_start_users:List<UserInfo>,   //发起人信息
        var plan_starttime:String,   //开始时间
        var plan_endtime:String,   //结束时间
        var plan_num:String,  //计划人数
        var plan_status:String  //计划状态

)
