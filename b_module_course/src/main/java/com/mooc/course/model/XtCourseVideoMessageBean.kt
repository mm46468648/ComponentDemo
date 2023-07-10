package com.mooc.course.model

import java.util.*

/**
 * 学堂课程视频信息
 */
class XtCourseVideoMessageBean {
    var sources: SourcesBean? = null

    class SourcesBean {
        var quality10: ArrayList<String>? = null
    }
}