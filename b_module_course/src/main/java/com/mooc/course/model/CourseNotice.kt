package com.mooc.course.model

data class CourseNotice(
        var id: String = "",     //公告id
        val date: String = "",    //日期"2017-11-20 00:00:00",
        val content: String = ""    //公告内容
)