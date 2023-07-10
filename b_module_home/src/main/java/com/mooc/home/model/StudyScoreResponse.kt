package com.mooc.home.model

/**
 * 学习室积分
 * 学习室头部
 */
data class StudyScoreResponse(
        var today_score: Int = 0,     //今日积分
        val total_score: Int = 0,     //所有积分
        val user_total: Int = 0,     //总共注册任务
        val unread_num: Int = 0,     //未读消息数
        var ranking: String? = null,
        val learn_count: Int = 0,
        val learn_score: Int = 0,
        val learn_read: Boolean = false    //是否显示获得学习积分pop
)