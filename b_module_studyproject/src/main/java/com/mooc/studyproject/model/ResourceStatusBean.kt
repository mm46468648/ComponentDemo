package com.mooc.studyproject.model

data class ResourceStatusBean(
        var success: Boolean = false,
        var msg: String="",
        var code: String="",
        var is_complate: String=""
) {

}