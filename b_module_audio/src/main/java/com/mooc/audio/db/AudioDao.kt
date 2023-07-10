package com.mooc.audio.db

import androidx.room.*
import com.mooc.commonbusiness.model.db.TrackDB

@Dao
interface AudioDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: TrackDB)

    //批量删除
    @Delete
    fun delete(track: List<TrackDB>)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(track: TrackDB)

    /**
     * 查询音频课下的音频列表
     */
    @Query("SELECT * FROM track_table where albumId = :albumId")
    fun findDownloadTrackList(albumId: String): List<TrackDB>


    /**
     * 查询改音频专辑中的数量
     * （只有已完成后才会插入数据库）
     */
    @Query("SELECT COUNT(*) FROM track_table WHERE albumId = :albumId")
    fun getDownloadCount(albumId: String): Int

    /**
     * 查询单个音频
     */
    @Query("SELECT * FROM track_table where id = :trackId")
    fun findDownloadTackById(trackId: String): TrackDB?
}