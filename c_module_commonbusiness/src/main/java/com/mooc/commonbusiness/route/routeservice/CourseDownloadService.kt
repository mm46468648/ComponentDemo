package com.mooc.commonbusiness.route.routeservice

import android.content.Context
import com.alibaba.android.arouter.facade.template.IProvider
import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.model.db.CourseDB

interface CourseDownloadService : IProvider {
    override fun init(context: Context?) {}

    fun findAllDownloadCourse():List<CourseDB>?

    fun findDownloadChapter(courseId:String,classRoomID:String):List<BaseChapter>?

    fun deleteCourse(courseBean: CourseDB)

    fun deleteChapters(chapters:List<BaseChapter>,coureId:String,classRoomId:String)

    fun insertCourseDb(courseBean: CourseDB)
}