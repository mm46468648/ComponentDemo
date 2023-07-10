package com.mooc.webview.db

import androidx.room.*
import com.mooc.commonbusiness.model.db.WebDB

@Dao
interface WebDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(webDb: WebDB);

    /**
     * 查询单个音频
     */
    @Query("SELECT * FROM web_table where `key` = :key")
    fun findDownloadTackById(key: String): WebDB?
}