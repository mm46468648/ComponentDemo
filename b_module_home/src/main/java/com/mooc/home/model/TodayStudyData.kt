package com.mooc.home.model

/**
 * 今日学习模型
 */
data class TodayStudyData(
        var checkin_score: Int = 0,
        val success: Boolean = false,
        val share_score: Int = 0,
        val every_day_score: Int = 0,
        val today_score: Int = 0,
        val report_resource_score: Int = 0,
        val resource_num :Int = 0
)