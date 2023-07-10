package com.mooc.course.model

import com.mooc.download.DownloadModel

/**
 * 课程章节
 * 和课程公告
 * 公用一个外壳
 */
data class CourseChapterListResponse(
        var chapters: ArrayList<CourseChapter>,
        var updates: ArrayList<CourseNotice>
)

data class CourseChapter(
        var id: String = "",
        var sequentials: ArrayList<SequentialBean>,
        var strSequenceJson: String = "",
        var display_name: String = ""   //课程考试章节名称
)

/**
 * 章节中的小节
 */
data class SequentialBean(
        var id: String = "",   //小节id
        var display_name: String? = "",   //课程考试小节名称
        var video_length: Int = 0,
        var parentDisplayName: String,   //外层的章节名称
        var has_submitted: Boolean,   //是否提交
        var has_problem: Boolean = false, //是否有习题
        var due_time: String? = "", //习题时长 格式（带有时区格式的字符串yyy-MM-dd'T'HH:mm:ss'Z'）

        var gained_point: Int = 0,     //得分点？
        var total_point: Int = 0,    //总分

        var watched: Boolean, //是否观看过
        var watched_percent: Double = 0.0,    //播放进度百分比（数据库记录）

        //下载模型
        var downloadModel: DownloadModel?
)