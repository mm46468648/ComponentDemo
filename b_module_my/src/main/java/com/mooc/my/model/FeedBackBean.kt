package com.mooc.my.model

import java.util.*

data class FeedBackBean(
        var phone_num: String? = null,
        var results: ArrayList<FeedTypeBean>? = null

)