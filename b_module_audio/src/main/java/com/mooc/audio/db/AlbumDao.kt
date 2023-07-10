package com.mooc.audio.db

import androidx.room.*
import com.mooc.commonbusiness.model.db.AlbumDB


@Dao
interface AlbumDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(track: AlbumDB)


    @Delete
    fun delete(albumBean: AlbumDB)

    @Update(onConflict = OnConflictStrategy.REPLACE)
    fun update(track: AlbumDB)

    @Query("SELECT * FROM album_table where haveDownload = 1")
    fun findDownloadAlbumList():List<AlbumDB>

    @Query("SELECT * FROM album_table where id=:albumId")
    fun findAlbumById(albumId:String) : AlbumDB?
}