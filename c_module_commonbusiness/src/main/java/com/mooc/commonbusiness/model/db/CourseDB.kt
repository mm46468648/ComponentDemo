package com.mooc.commonbusiness.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "course_table")
class CourseDB {
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0           //主键自增
    var courseId:String=""
    var classRoomID:String=""
    var platform : String = ""
    var name:String=""
    var cover = ""
//    var data:String = "" //详情全部以json方式存储({courseId,chourseName,classroomId,classPlatform,classCover})
    var chapters:String = "" //课程章节列表，统一以json方式存储（不同平台返回的结构不一样）
    var lastPlayChapterId = ""
    var haveDownload : Boolean = false

    var totalNum : Int = 0 //兼容老版本，表示目前下载的视频个数
    var totalSize : Long = 0 //兼容老版本，表示目前下载的文件大小
    var oldDownloadChapter:String=""    //兼容老版本，表示老版本所缓存的章节列表
}