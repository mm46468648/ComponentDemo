package com.mooc.commonbusiness.model.my

import java.io.Serializable

/**
 * 获取分享数据
 */
data class GetAppShareDataBean(
    var share_title: String? = null,
    var share_desc: String? = null,
    var share_link: String? = null,
    var share_picture: String? = null,
    var join_days :String?=null

):Serializable