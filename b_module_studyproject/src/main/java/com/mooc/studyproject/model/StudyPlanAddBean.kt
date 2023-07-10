package com.mooc.studyproject.model

data class StudyPlanAddBean (
        var id: Int = 0,
        var created_time: String? = null,
        var updated_time: String? = null,
        var join_status: Int = 0,
        var user: Int = 0,
        var study_plan: Int = 0,
        var code: Int = 0,
        var message: String? = null,
        var join_score: String? = null,
        var integrity_score: String? = null,
        private val error_code: Int = 0,
        var chengxin_score: String? = null
)