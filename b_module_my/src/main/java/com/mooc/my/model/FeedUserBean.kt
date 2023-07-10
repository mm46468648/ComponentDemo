package com.mooc.my.model

import java.util.*

/**

 * @Author limeng
 * @Date 2020/10/19-11:11 AM
 */
data class FeedUserBean(
     val id :String?=null,
     val user_name: String? = null,
     val user_avatar: String? = null,
     val user_id :String?=null,
     val title: String? = null,
     val question_type :String?=null,
     val feedback_type_content: String? = null,
     val descriptionFeedUserBean: String? = null,
     val attachment: ArrayList<String>? = null,
     val back_origin :String?=null,
     val status :String?=null,
     val contact: String? = null,
     val local_time: Long=0,
     val feedback_time: Long=0,
     val custom_service_name: String? = null,
     val is_solved :String?=null,
     val is_delete :Boolean=false,
     var is_read_message :String?=null

)