package com.mooc.commonbusiness.manager.ebook

import androidx.room.*
import com.mooc.commonbusiness.model.db.EbookDB

@Dao
interface EbookDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(eBookBean: EbookDB)

    //只能传递对象昂,删除时根据Cache中的主键 来比对的
    @Delete
    fun delete(downloadModel: EbookDB)

    //2.根据id删除表中数据（单个）
    @Query("DELETE  FROM ebook_read_history where id=:id")
    fun delete(id:String)
    /**
     * 根据类型，查询同类的资源
     * 如果是一对多,这里可以写List<Cache>
     */
    @Query("select *from ebook_read_history")
    fun getReadHistoryList(): List<EbookDB>
}