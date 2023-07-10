package com.mooc.studyroom.viewmodel

import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import com.mooc.common.global.AppGlobals
import com.mooc.commonbusiness.base.BaseListViewModel2
import com.mooc.commonbusiness.manager.ebook.ReadHistoryManager
import com.mooc.commonbusiness.model.search.EBookBean
import com.mooc.studyroom.db.OldDbSearchManager
import com.mooc.studyroom.db.OldDownloadBookDatabaseHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class EbookDownloadViewModel : BaseListViewModel2<EBookBean>() {
    override suspend fun getData(): Flow<List<EBookBean>> {
        return flow<List<EBookBean>> {
            queryAllDwonloadBookData()
            emit(ReadHistoryManager.searchReadHistoryList()?: arrayListOf())
        }
    }

    /**
     * 查询老版本数据库，
     * 如果没有会走到cache中
     */
    fun queryAllDwonloadBookData() {
        val bookHelper = OldDownloadBookDatabaseHelper(AppGlobals.getApplication())
        var cursor: Cursor? = null
        try {
            val db: SQLiteDatabase = bookHelper.getReadableDatabase()
            // 查询所有数据
            cursor = db.query(
                OldDownloadBookDatabaseHelper.TABLE_EBOOK_DOWNLOAD_NAME,
                null,
                null,
                null,
                null,
                null,
                null
            )
            if (cursor.count > 0) {
                while (cursor.moveToNext()) {
                    val order: EBookBean= parseBookDownloadBean(cursor)
                    ReadHistoryManager.save(order)
                }

                //同步完毕，删除原来的数据库
                val sql = "DROP TABLE IF EXISTS \"" + OldDownloadBookDatabaseHelper.TABLE_EBOOK_DOWNLOAD_NAME +"\""
                db.execSQL(sql)
            }
        } catch (e: Exception) {
            Log.e(OldDbSearchManager.TAG, e.toString())
        } finally {
            cursor?.close()
        }
    }


    private fun parseBookDownloadBean(cursor: Cursor): EBookBean {
        val eBookBean = EBookBean()

        val id = cursor.getString(cursor.getColumnIndex("_id"))
        val picture = cursor.getString(cursor.getColumnIndex("PICTURE"))
        val book_id = cursor.getString(cursor.getColumnIndex("BOOK_ID"))
        val title = cursor.getString(cursor.getColumnIndex("TITLE"))
        val filesize = cursor.getString(cursor.getColumnIndex("FILESIZE"))
        Log.e(
            OldDbSearchManager.TAG,
            "id: " + id + "picture: " + picture + "book_id: " + book_id + "title: " + title + "picture: " + filesize
        )

        eBookBean.id = id
        eBookBean.picture = picture
        eBookBean.ebook_id = book_id
        eBookBean.title = title
        eBookBean.filesize = filesize


        return eBookBean
    }
}