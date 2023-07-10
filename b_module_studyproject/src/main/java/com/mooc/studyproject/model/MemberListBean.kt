package com.mooc.studyproject.model

import java.io.Serializable

data class MemberListBean(
    var name_pinyin: String? = null,
    var activity_id: Int = 0,
    var user_id: String? = null,
    var avatar: String? = null,
    var name: String? = null,
    var isLike: Boolean = false

) : Serializable