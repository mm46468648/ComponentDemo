package com.mooc.studyroom.model

data class CourseMsgBean(
         val id: Int = 0,
         val receiver_id: Int = 0,
         val unread_num: Int = 0,
         val message_type: Int = 0,
         val sender_id: Int = 0,
         val updated_time: String? = null,
         val is_top: Int = 0,
         val last_content: String? = null,
         val url: String? = null,
         val title: String? = null
)