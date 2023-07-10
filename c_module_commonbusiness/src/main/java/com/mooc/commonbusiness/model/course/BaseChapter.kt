package com.mooc.commonbusiness.model.course

interface BaseChapter {

    val name:String
    val id:String         //想要的是视频id

    fun generateDownloadId(courseId:String,classRoomId:String="") : Long
}