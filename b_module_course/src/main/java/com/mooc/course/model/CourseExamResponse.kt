package com.mooc.course.model

data class CourseExamResponse(
        var exam_gained_point: Int = 0,
        val homework_gained_point: Int = 0,
        val is_passed: Boolean = false,
        val exam_total_point: Int = 0,
        val homework_total_point: Int = 0,
        val course_point: Int = 0,
        val passed_point: Int  = 0,
        val chapters: List<CourseChapter>

)