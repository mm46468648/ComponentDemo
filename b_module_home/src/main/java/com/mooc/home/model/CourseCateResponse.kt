package com.mooc.home.model

data class CourseCateResponse(
        var platform: PlatformBean? = null
)

data class PlatformBean(
        var url: String? = "",
        val code: String = "",
        val name: String = "",
        val localLevel :Int= 0,
        var other_platforms: List<PlatformBean> = arrayListOf(),
        var union_platforms: List<PlatformBean> = arrayListOf()
)