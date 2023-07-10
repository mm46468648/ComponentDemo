package com.mooc.home.model

/**
 * 积分明细响应模型
 */
data class ScoreDetailResponse(
        var user_total_score: String,
        var user_score: List<ScoreDetail>
)
/**
 * 积分明细模型
 */
data class ScoreDetail(
        var user_total_score: String? = null,
        val source: String = "",
        val total_score: Int = 0,
        val add_time: Long = 0
)