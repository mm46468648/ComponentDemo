package com.mooc.course.manager.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mooc.commonbusiness.model.db.CourseDB
import com.mooc.download.global.AppGlobals

@Database(entities = [CourseDB::class], version = 1)
abstract class CourseDatabase() : RoomDatabase(){

    companion object{
         val DATABASE: CourseDatabase? = AppGlobals.getApplication()?.let {
            Room.databaseBuilder(it, CourseDatabase::class.java, "course_db")
                    .allowMainThreadQueries() //是否允许在主线程进行查询
                    //数据库创建和打开后的回调
                    //.addCallback()
                    //设置查询的线程池
                    //.setQueryExecutor()
                    //.openHelperFactory()
                    //room的日志模式
                    //.setJournalMode()
                    //数据库升级异常之后的回滚
//                    .fallbackToDestructiveMigration()
                    //数据库升级异常后根据指定版本进行回滚
//                    .fallbackToDestructiveMigrationFrom()
                    // .addMigrations(CacheDatabase.sMigration)
                    .build()
        }

    }

//    abstract fun getChapterDao() : ChapterDao

    abstract fun getCourseDao():CourseDao

}