package com.mooc.commonbusiness.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "track_table")
class TrackDB {
    @PrimaryKey
    var id:Long = 0
    var data:String = "" //详情全部以json方式存储
    var albumId:String = ""
}