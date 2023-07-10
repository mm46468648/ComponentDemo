package com.mooc.my.model

import java.util.*

/**

 * @Author limeng
 * @Date 2020/10/19-3:50 PM
 */
data class FeedListBean(
    var count :String?=null,
    var results: ArrayList<FeedUserBean>? = null
)