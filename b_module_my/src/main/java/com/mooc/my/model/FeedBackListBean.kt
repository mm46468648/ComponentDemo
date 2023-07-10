package com.mooc.my.model

import kotlin.collections.ArrayList

/**

 * @Author limeng
 * @Date 2020/10/27-10:11 AM
 */
data class FeedBackListBean(
        val reply: ArrayList<ReplyBean>? = null,
        val feedback: ArrayList<FeedbackBean>? = null
) {
    class ReplyBean {
        val order: String? = null
        val content: String? = null
        val local_time: Long = 0
        val owner_name: String? = null
        val user_avatar: String? = null
        val user_id: String? = null
        val reply_user_id: Any? = null
        val is_read: String? = null
        val img_attachment: ArrayList<String>? = null
    }

    class FeedbackBean {
        val id: String? = null
        val user_name: String? = null
        val user_avatar: String? = null
        val user_id: String? = null
        val title: String? = null
        val question_type: String? = null
        val description: String? = null
        val back_origin: String? = null
        val status: String? = null
        val contact: String? = null
        val local_time: Long = 0
        val feedback_time: Long = 0
        val custom_service_name: String? = null
        val is_solved: String? = null
        val is_delete = false
        val is_read_message: String? = null
        val feedback_type_content: String? = null
        val attachment: ArrayList<String>? = null
    }
}