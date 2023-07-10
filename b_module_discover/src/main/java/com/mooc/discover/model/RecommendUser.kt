package com.mooc.discover.model

import java.util.*

/**
 * 推荐的相似用户
 */
class RecommendUser {

    var avatar: String= ""
    var nickname: String= ""
    var avatar_identity: String= ""
    var title: String= ""
    var user_id: String= ""
    var is_attention = false
    var label_list: ArrayList<String>? = null
}