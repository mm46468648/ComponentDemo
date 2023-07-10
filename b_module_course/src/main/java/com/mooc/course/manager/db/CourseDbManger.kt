package com.mooc.course.manager.db

import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.model.db.CourseDB
import java.lang.Exception

class CourseDbManger {

    companion object {

        /**
         * 为了兼容一次老版本数据
         */
        fun insertCourse(courseBean: CourseDB) {
            try {
                val findCourseById = findCourseById(courseBean.courseId, courseBean.classRoomID)
                if (findCourseById == null) {
                    CourseDatabase.DATABASE?.getCourseDao()?.insert(courseBean)
                } else{
                    if(findCourseById.oldDownloadChapter.isEmpty() && courseBean.oldDownloadChapter.isNotEmpty()){
                        //后同步的老版本,将这些字段补充进去
                        findCourseById.oldDownloadChapter = courseBean.oldDownloadChapter
                        findCourseById.totalNum = courseBean.totalNum
                        findCourseById.totalSize = courseBean.totalSize
                    }

//                    if(findCourseById.chapters.isEmpty() && courseBean.chapters.isNotEmpty()){
//                        //后同步的新版本,将这些字段补充进去
//                        findCourseById.chapters = courseBean.chapters
//                        findCourseById.lastPlayChapterId = courseBean.lastPlayChapterId
//                        findCourseById.classRoomID = courseBean.classRoomID
//                        findCourseById.platform = courseBean.platform
//                    }
                    //只要新老字段不相等
                    if(findCourseById.chapters != courseBean.chapters){
                        //后同步的新版本,将这些字段补充进去
                        findCourseById.chapters = courseBean.chapters
                        findCourseById.classRoomID = courseBean.classRoomID
                        findCourseById.platform = courseBean.platform
                    }
                    CourseDatabase.DATABASE?.getCourseDao()?.update(findCourseById)
                }

            } catch (e: Exception) {
                loge(e.toString())
            }
        }


        fun deleteCourse(courseBean: CourseDB){
            CourseDatabase.DATABASE?.getCourseDao()?.delete(courseBean)
        }
        fun updateCourse(courseBean: CourseDB) {
            try {
                CourseDatabase.DATABASE?.getCourseDao()?.update(courseBean)
            } catch (e: Exception) {
                loge(e.toString())
            }
        }

        //
        fun findCourseById(courseId: String, classRoomId: String): CourseDB? {
            if (classRoomId.isNotEmpty())
                return CourseDatabase.DATABASE?.getCourseDao()
                    ?.findCourseByClassRoomId(courseId, classRoomId)
            return CourseDatabase.DATABASE?.getCourseDao()?.findCourseById(courseId)
        }

        //
        fun findAllDownloadCourse(): List<CourseDB>? {
            val findDownloadCourseAll =
                CourseDatabase.DATABASE?.getCourseDao()?.findDownloadCourseAll()
            return findDownloadCourseAll
        }

    }
}