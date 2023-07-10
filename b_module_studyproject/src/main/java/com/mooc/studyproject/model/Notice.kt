package com.mooc.studyproject.model

import com.chad.library.adapter.base.entity.MultiItemEntity

data class Notice(
        /**
         * id : 1
         * studyplan : 68
         * title : 公告标题
         * content : 公告内容
         * user_id : 101
         * status : 1
         * order : 2
         * created_time : 2019-02-12 16:15:03
         * updated_time : 2019-02-12 16:15:03
         */
        var type: Int = 2,
        var id:String? = null,
        var studyplan: Int = 0,
        var title: String? = null,
        var content: String? = null,
        var user_id: Int = 0,
        var status: Int = 0,
        var order: Int = 0,
        var created_time: String? = null,
        var updated_time: String? = null
        ) : MultiItemEntity {
   override  val itemType: Int
        get() = type
}