package com.mooc.my.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**

 * @Author limeng
 * @Date 2020/9/1-3:18 PM
 */
data class ReadBean(
        val uid: Int,
        var date: String = "",
        var image_url: String = "",
        var share_url: String = ""
)