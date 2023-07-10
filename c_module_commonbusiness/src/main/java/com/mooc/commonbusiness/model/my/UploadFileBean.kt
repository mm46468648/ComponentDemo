package com.mooc.commonbusiness.model.my

/**
 * 上传文件
 */
data class UploadFileBean(
        var success: Boolean = false,
        var url: String? = null,
        var word: String? = null
)