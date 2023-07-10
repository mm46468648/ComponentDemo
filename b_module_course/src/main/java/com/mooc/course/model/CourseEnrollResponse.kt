package com.mooc.course.model

/**
 * 课程注册（订阅）
 */
data class CourseEnrollResponse(
        var isVerified: Boolean = false,
        val isEnrolled: Boolean = false,
        val strEnrollMode: String = "",
        val isCredentialApplySuccess :Boolean = false //是否成功申请过证书

)