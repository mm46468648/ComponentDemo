package com.mooc.commonbusiness.model.studyroom

import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.utils.HashUtil

/**
 * 老版本课程章节数据库模型
 */
data class OldChapterBean(
    override var name:String,
    var path:String
) : BaseChapter {
    override val id: String
        get() = ""

    var courseId : String = ""
    var classRoomId:String = ""

    override fun generateDownloadId(courseId: String, classRoomId: String): Long {
        return if(classRoomId.isEmpty()){
             HashUtil.longHash("${courseId}_${name})")
        }else{
            com.mooc.commonbusiness.utils.HashUtil.longHash("${courseId}_${classRoomId}_${name}")
        }
    }
}