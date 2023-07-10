package com.mooc.commonbusiness.model.db

import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey

@Entity(tableName = "album_table")
class AlbumDB {
    @PrimaryKey
    var id: Long = 0
    var data: String = "" //详情全部以json方式存储
    var lastPlayAudioId = ""
    var haveDownload: Boolean = false

    @Ignore
    var fileNum: Int = 0      //专辑中已下载音频数量
    @Ignore
    var fileSize: Long = 0L //专辑中已下载音频文件大小
}