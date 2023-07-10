package com.mooc.studyroom.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import androidx.annotation.Nullable;

/**
 *老下载数据库帮助类
 */
public class OldDownloadDatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 2;
    private static final String DB_NAME = "download_info.db";
    public static final String TABLE_COUSE_DOWNLOAD_NAME = "download_info";

    private static final String SQL_CREATE_DOWNLOAD_TABLE = String.format(
            "CREATE TABLE %s (_id varchar(255) PRIMARY KEY NOT NULL,supportRanges integer NOT NULL,createAt long NOT NULL,uri varchar(255) NOT NULL,path varchar(255) NOT NULL,size long NOT NULL, progress long NOT NULL,status integer NOT NULL);",
            TABLE_COUSE_DOWNLOAD_NAME);


    public OldDownloadDatabaseHelper(@Nullable Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // 创建数据库1张表
        // 通过execSQL（）执行SQL语句（此处创建了1个名为person的表）
//        String sql = "create table person(id integer primary key autoincrement,name varchar(64),address varchar(64))";
//        db.execSQL(sql);
        db.execSQL(SQL_CREATE_DOWNLOAD_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
