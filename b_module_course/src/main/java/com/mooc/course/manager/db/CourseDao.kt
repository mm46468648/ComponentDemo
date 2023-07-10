package com.mooc.course.manager.db

import androidx.room.*
import com.mooc.commonbusiness.model.db.CourseDB

@Dao
interface CourseDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(courseBean: CourseDB)

    @Delete
    fun delete(courseBean: CourseDB)
//
//
    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(courseBean: CourseDB)
//
    /**
     * @param courseId 真正的第三方的课程id
     */
    @Query("SELECT * FROM course_table where courseId =:courseId")
    fun findCourseById(courseId:String) : CourseDB?

    /**
     * @param classRoomId 班级id
     */
    @Query("SELECT * FROM course_table where courseId =:courseId and classRoomID = :classRoomId")
    fun findCourseByClassRoomId(courseId:String,classRoomId:String) : CourseDB?
//
    @Query("SELECT * FROM course_table where haveDownload = 1")
    fun findDownloadCourseAll() : List<CourseDB>
}