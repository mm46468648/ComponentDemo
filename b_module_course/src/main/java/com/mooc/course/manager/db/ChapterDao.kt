package com.mooc.course.manager.db

import androidx.room.*
import com.mooc.course.model.ChaptersBean

//import com.mooc.commonbusiness.model.search.ChaptersBean


@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(chaptersBean: ChaptersBean)

    @Delete
    fun delete(chaptersBean: List<ChaptersBean>)

}