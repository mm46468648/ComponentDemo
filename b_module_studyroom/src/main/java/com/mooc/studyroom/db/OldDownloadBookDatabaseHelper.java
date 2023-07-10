package com.mooc.studyroom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 *老下载数据库帮助类
 */
public class OldDownloadBookDatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "user.db";
    public static final String TABLE_EBOOK_DOWNLOAD_NAME = "ZYREADER";


    private static final String SQL_CREATE_TRACK_TABLE = String.format(
            "CREATE TABLE %s (_id varchar(255) PRIMARY KEY AUTOINCREMENT NOT NULL);",
            TABLE_EBOOK_DOWNLOAD_NAME);

    public OldDownloadBookDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 创建数据库1张表
        // 通过execSQL（）执行SQL语句（此处创建了1个名为person的表）
//        try {
//            db.execSQL(SQL_CREATE_TRACK_TABLE);
//        }catch (Exception e){
//            Log.e("OldDbSearchManager",e.toString());
//        }

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
