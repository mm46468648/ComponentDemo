package com.mooc.download.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mooc.download.DownloadModel
import com.mooc.download.global.AppGlobals
import com.mooc.newdowload.DownloadInfo

@Database(entities = [DownloadInfo::class], version = 1)
abstract class DownloadDatabase() : RoomDatabase(){

    companion object{
         val database: DownloadDatabase? = AppGlobals.getApplication()?.let {
            Room.databaseBuilder(it, DownloadDatabase::class.java, "download_db")
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
                    //.fallbackToDestructiveMigrationFrom()
                    // .addMigrations(CacheDatabase.sMigration)
                    .build()
        }

    }

    abstract fun getDownloadDao() : DownloadDao

}