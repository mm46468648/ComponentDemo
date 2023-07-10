package com.mooc.studyroom.model

data class StudyPlanAddBean(
        var id: Int = 0,
        var created_time: String? = null,
        var updated_time: String? = null,
        var join_status: Int = 0,
        var user: Int = 0,
        var study_plan: Int = 0,
        var code: Int = 0,
        var message: String? = null,
        var join_score: String? = null,
        val error_code: Int = 0,
        var chengxin_score: String? = null
)