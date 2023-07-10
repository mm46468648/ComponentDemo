package com.mooc.commonbusiness.model.studyroom

/**
 * 学习清单中删除的资源
 */
data class FolderResourceDelBean(
        val resource_id: String = "",
        val resource_type: String = "",
        val title: String = ""
)