package com.mooc.studyroom.model

data class InteractionMsgBean(
         val id: Int = 0,
         val created_time: String? = null,
         val time: Long = 0,
         val receiver_id: Int = 0,
         val sender_type: Int = 0,
         val content: String? = null,
         val is_read: Boolean = false,
         val message_index_id: Int = 0,
         val others: InteractionOtherBean? = null,
         val sender_id: Int = 0,
         val sender_name: String? = null,
         val url: String? = null,
         val like_type: Int = 0,
         val event_name: String? = null,
         val type_id: Int = 0,
         val studyplan_id: Int = 0,
         val activity_id:String?=null,
         val is_start_user: Boolean = false
)