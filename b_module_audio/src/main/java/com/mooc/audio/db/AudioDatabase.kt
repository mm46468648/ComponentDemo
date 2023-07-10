package com.mooc.audio.db

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mooc.audio.model.AudioPoint
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.db.TrackDB
import com.mooc.download.global.AppGlobals


@Database(entities = [AlbumDB::class,TrackDB::class,AudioPoint::class], version = 2)
abstract class AudioDatabase : RoomDatabase(){

    companion object{

        //创建音频打点数据库SQL
        val SQL_CREATE_AUDIO_POINT_DB = "CREATE TABLE IF NOT EXISTS `AudioPoint` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `i` TEXT NOT NULL, `et` TEXT NOT NULL, `cp` TEXT NOT NULL, `fp` TEXT NOT NULL, `tp` TEXT NOT NULL, `sp` TEXT NOT NULL, `ts` TEXT NOT NULL, `p` TEXT NOT NULL, `u` TEXT NOT NULL, `c` TEXT NOT NULL, `cc` TEXT NOT NULL, `d` TEXT NOT NULL, `v` TEXT NOT NULL, `pg` TEXT NOT NULL, `sq` TEXT NOT NULL)"
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL(SQL_CREATE_AUDIO_POINT_DB);
            }
        }

        val DATABASE: AudioDatabase? = AppGlobals.getApplication()?.let {
            Room.databaseBuilder(it, AudioDatabase::class.java, "audio_db")
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
                     .addMigrations(MIGRATION_1_2)
                    .build()
        }



    }

    abstract fun getAudioDao() : AudioDao

    abstract fun getAlbumDao() : AlbumDao

    abstract fun getAudioPoint() : AudioPointDao
}
