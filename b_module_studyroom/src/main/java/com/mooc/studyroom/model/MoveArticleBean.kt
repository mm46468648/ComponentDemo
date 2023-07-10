package com.mooc.studyroom.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**
 * 笔记中移动的 bean
 */
data class MoveArticleBean(
//        val drawable: Int = 0,
        val name: String? = null,
        val folderId: String? = null,
        val next: Boolean = false,
        override val itemType: Int
):MultiItemEntity