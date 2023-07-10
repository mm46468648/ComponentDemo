package com.mooc.home.model

data class ContentResponse(
        var count: Int = 0,
        val about: String = "",
        val position_one: Int = 0,
        val student_num: Int = 0,
        val title: String = "",
        val has_more: Boolean = false,
        val list_tag: Int = 0,
        val is_subscribe: Boolean = false,
        val detail: String = "",
        val parent_id: Int = 0,
        val tag: Int = 0,
        val link: String = "",
        val position_two: Int = 0,
        val recommend_reason: String = "",
        val type: Int = 0,
        val id: Int = 0,
        val small_image: String? = ""
)