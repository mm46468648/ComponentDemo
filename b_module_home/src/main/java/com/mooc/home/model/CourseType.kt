package com.mooc.home.model


/**
 * 课程类型
 * （
 *  有考试，无考试
 *  有证书，无证书
 *  付费，免费
 * ）
 */
data class CourseType(
        var title: String,
        var choose1: String,
        var choose2: String,
        var chooseNum: Int = -1  //1choose1，2choose2，-1什么都不选
)