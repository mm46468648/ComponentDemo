package com.mooc.studyproject.model

import java.util.*

data class CommentList(
        /**
         * count : 2
         * next : null
         * previous : null
         */
        private val count: Int = 0,
        val next: Any? = null,
        val previous: Any? = null,
        var results: ArrayList<ItemComment>? = null
) {

}