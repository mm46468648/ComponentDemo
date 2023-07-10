package com.mooc.webview.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mooc.common.global.AppGlobals
import com.mooc.commonbusiness.model.db.WebDB


@Database(entities = [WebDB::class], version = 1)
abstract class WebDatabase : RoomDatabase() {

    companion object {
//        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
//            override fun migrate(database: SupportSQLiteDatabase) {
//                database.execSQL("ALTER TABLE track_table "
//                        + " ADD COLUMN albumId TEXT")
//            }
//        }

        val DATABASE: WebDatabase? = AppGlobals.getApplication()?.let {
            Room.databaseBuilder(it, WebDatabase::class.java, "web_db")
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
//                     .addMigrations()
                    .build()
        }


    }

    abstract fun getWebDao(): WebDao
}
