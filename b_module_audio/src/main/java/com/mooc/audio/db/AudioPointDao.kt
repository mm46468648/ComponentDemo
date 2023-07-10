package com.mooc.audio.db

import androidx.room.*
import com.mooc.audio.model.AudioPoint
import com.mooc.commonbusiness.model.db.AlbumDB
import com.mooc.commonbusiness.model.db.TrackDB

@Dao
interface AudioPointDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(point:AudioPoint)

    @Delete
    fun delete(point: AudioPoint)

    @Query("SELECT * FROM audioPoint")
    fun findAllPoint():List<AudioPoint>
}