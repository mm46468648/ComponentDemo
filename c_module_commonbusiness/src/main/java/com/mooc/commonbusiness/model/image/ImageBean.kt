package com.mooc.commonbusiness.model.image

data class ImageBean(
        var url: String? = null,//图片地址

        var word: String? = null,//图片需要复审文案

        var need_check: Int = 0//图片需要复审时，发表动态时传1
)