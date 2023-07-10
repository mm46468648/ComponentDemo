package com.mooc.commonbusiness.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "web_table")
class WebDB {
    @PrimaryKey
    var key: String = ""
    var position: Int = 0


}