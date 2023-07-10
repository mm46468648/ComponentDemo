package com.mooc.studyroom.db;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.mooc.commonbusiness.model.search.EBookBean;
import com.mooc.studyroom.model.OldCourseDownloadBean;

import java.util.ArrayList;
import java.util.List;

/**
 * 老数据库查询管理
 */
public class OldDbSearchManager {

    public static final String TAG = "OldDbSearchManager";
    private Context context;
    private final OldDownloadDatabaseHelper helper;
    private final OldDownloadTrackDatabaseHelper trackHelper;
    private final OldDownloadBookDatabaseHelper bookHelper;

    public OldDbSearchManager(Context context) {

        this.context = context;
        helper = new OldDownloadDatabaseHelper(context);
        trackHelper = new OldDownloadTrackDatabaseHelper(context);
        bookHelper = new OldDownloadBookDatabaseHelper(context);
    }



    public List<OldCourseDownloadBean> queryAllDwonloadTrackData() {
        Cursor cursor = null;
        List<OldCourseDownloadBean> orderList = null;
        try {
            SQLiteDatabase db = trackHelper.getReadableDatabase();
            // 查询所有数据
            cursor = db.query(OldDownloadTrackDatabaseHelper.TABLE_TRACK_DOWNLOAD_NAME, null, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                orderList = new ArrayList<OldCourseDownloadBean>(cursor.getCount());
                while (cursor.moveToNext()) {
                    OldCourseDownloadBean order = parseTrackDownloadBean(cursor);
                    orderList.add(order);
                }
            }

        } catch (Exception e) {
            Log.e(TAG,e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return orderList;
    }

    public List<EBookBean> queryAllDwonloadBookData() {
        Cursor cursor = null;
        List<EBookBean> orderList = null;
        try {
            SQLiteDatabase db = bookHelper.getReadableDatabase();
            // 查询所有数据
            cursor = db.query(OldDownloadBookDatabaseHelper.TABLE_EBOOK_DOWNLOAD_NAME, null, null, null, null, null, null);
            if (cursor.getCount() > 0) {
                orderList = new ArrayList<EBookBean>(cursor.getCount());
                while (cursor.moveToNext()) {
                    EBookBean order = parseBookDownloadBean(cursor);
                    orderList.add(order);
                }
            }

        } catch (Exception e) {
            Log.e(TAG,e.toString());
        } finally {
            if (cursor != null)
                cursor.close();
        }

        return orderList;
    }



    private OldCourseDownloadBean parseTrackDownloadBean(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex("id"));
        String uri = cursor.getString(cursor.getColumnIndex("albumid"));
        String path = cursor.getString(cursor.getColumnIndex("tracktitle"));

        Log.e(TAG, "id: " + id + "albumid: " + uri + "tracktitle: " + path);
        return null;
    }

    private EBookBean parseBookDownloadBean(Cursor cursor) {
        String id = cursor.getString(cursor.getColumnIndex("_id"));
        String picture = cursor.getString(cursor.getColumnIndex("PICTURE"));
        String book_id = cursor.getString(cursor.getColumnIndex("BOOK_ID"));
        String title = cursor.getString(cursor.getColumnIndex("TITLE"));
        String filesize = cursor.getString(cursor.getColumnIndex("FILESIZE"));

        Log.e(TAG, "id: " + id + "picture: " + picture + "book_id: " + book_id+ "title: " + title+ "picture: " + filesize);
        return null;
    }

}
