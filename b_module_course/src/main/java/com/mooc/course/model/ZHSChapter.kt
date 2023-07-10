package com.mooc.course.model

import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.download.util.HashUtil
import com.mooc.newdowload.DownloadInfo

data class ZHSChapterData(
        var chapterList : List<ZHSChapter>,
        var token:String,
        var ts:String,
        var videoNum:String)

data class ZHSChapter(
        var chapterId : String = "",
        var name:String = "",
        var lessonInfo : List<LessonInfo>
        

)

class LessonInfo : BaseChapter{

    var level : Int = 0   //章节结构级别       ,能播放的都置为2
    var videoLength : Long = 0
    var videoUrl : String? = ""
    override var name : String = ""




    var videoId : String = ""
    var lessonId : String = ""
    var lessonVideoId : String = ""
    var chapterId : String = "" //记录下章节id，打点需要

     var lessonName : String = ""        //下载里面的结构目前还在用
     var chapterName : String = ""       //下载里面的结构目前还在用

    var lessonVideoInfo : List<LessonInfo>? = null      //三级章节
    var deleteDownloadSelect : Boolean = false //是否选中删除下载
    //下载模型
    var downloadInfo: DownloadInfo? = null
    var lastPlayPosition : Int = 0     //最近播放位置


    //有lessonVideoId取lessonVideoId,等于是第三级结构，没有取lessonId
    override val id: String
        get() {
            val chapterId =  if(lessonVideoId.isNotEmpty()){ //不等于空的是第三级结构
                lessonVideoId
            }else{
                lessonId
            }
            return chapterId
        }

    override fun generateDownloadId(courseId: String, classRoomId: String): Long {
        val longHash = HashUtil.longHash("${courseId}_${id})")
        return longHash
    }
}