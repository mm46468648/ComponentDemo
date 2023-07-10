package com.mooc.discover.model

import java.util.*

/**

 * @Author limeng
 * @Date 2020/11/13-11:35 AM
 */
data  class RecommendHotBean(
    var msg: String? = null,
    var total :Int= 0,

    var result: ArrayList<Any>? = null
)