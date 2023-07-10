package com.mooc.commonbusiness.model.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ebook_read_history")
class EbookDB {
    @PrimaryKey
    var id:String = ""
    var data : String = "" //ebookbean json
}