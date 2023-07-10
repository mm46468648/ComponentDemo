package com.mooc.my.model

/**

 * @Author limeng
 * @Date 2020/11/5-10:54 AM
 */
data class SchoolUserBean(
        val user_score: Int = 0,
        val user_introduction: String? = null,
        var user_is_follow: Boolean = false,
        val user_id: Int = 0,
        var user_be_follow_count: Int = 0,
        val user_avatar: String? = null,
        val avatar_identity: String? = null,
        val user_follow_count: Int = 0,
        val user_name: String? = null,
        var state: Int = 0,
        var user_like_count: Int = 0,
        var is_like: Boolean = false,
        var is_diss: Boolean = false,
        var user_dislike_count: Int = 0
)