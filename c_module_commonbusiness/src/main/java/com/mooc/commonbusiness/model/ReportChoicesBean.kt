package com.mooc.commonbusiness.model

import java.util.*

/**
 * 举报选项bean
 */
data class ReportChoicesBean(
        var report_choices: ArrayList<ReportBean>? = null,
        var results: ArrayList<ReportBean>? = null        //学习计划中动态举报使用这个字段
)

data class ReportBean(
        val report_id: Int = 0,
        val detail: String = "",
        var isSelected: Boolean = false,

        //扩展一些字段方便传值
        var resourceId:String="",
        var resourceType:Int = -1,
        var resourceTitle:String,

        //动态举报id
        var activity_id:String="",
)