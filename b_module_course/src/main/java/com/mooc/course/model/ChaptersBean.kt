package com.mooc.course.model

import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.utils.HashUtil
import com.mooc.newdowload.DownloadInfo
import org.jetbrains.annotations.NotNull
import java.io.Serializable

class ChaptersBean : Serializable, BaseChapter {

    var title: String = ""     //老学堂章节标题

    var sequentials: List<ChaptersBean>? = null //老学堂章节次标题

    var chapterName: String = ""//章节名字
    override var name: String = ""
    var level: Int = 0

    var order: Int = 0

    var getDownloadUrlState = 0       //获取下载地址的状态1准备获取,2是获取成功,3获取失败

    @NotNull
    override var id: String = "" //视频id
    override fun generateDownloadId(courseId: String, classRoomId: String): Long {
        return HashUtil.longHash("${courseId}_${classRoomId}_${id}")
    }

//    override fun generateDownloadId(): Long {
//        return HashUtil.longHash("${courseId}_${classRoomId}_${id}")
//    }
    val section_list: ArrayList<ChaptersBean>? = null   //新学堂，复用ChaptersBean name order字段
    var leaf_list: ArrayList<ChaptersBean>? = null   //新学堂，复用ChaptersBean
    var type: Int = -1//课件类型 VIDEO = 0 AUDIO = 1 LIVE = 2 ARTICLE = 3 DISCUSSION = 4 QUIZ = 5 EXERCISE = 6

    var courseId: String = ""
    var classRoomId: String = ""

    //下载模型
    var downloadModel: DownloadInfo? = null

}