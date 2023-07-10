package com.mooc.studyroom.model

data class CourseMsgDetailBean(
        var id: Int = 0,
        var course_id: Int = 0,
        var content: String? = null,
        var receiver_id: Int = 0,
        var isIs_read: Boolean = false,
        var created_time: String? = null,
        var course_title: String? = null


)