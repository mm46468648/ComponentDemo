package com.mooc.studyroom.model

data class SystemMsgBean(
        var id: Int = 0,
     var content: String? = null,
     var receiver_id: Int = 0,
     var isIs_read: Boolean = false,
     var created_time: String? = null,
     var others: SystemResourceBean? = null
 )