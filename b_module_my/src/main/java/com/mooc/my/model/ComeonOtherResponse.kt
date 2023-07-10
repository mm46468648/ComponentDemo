package com.mooc.my.model

import com.mooc.commonbusiness.model.UserInfo
import com.mooc.commonbusiness.model.studyproject.MusicBean

/**
 * 为他人加油接口响应
 */
class ComeonOtherResponse {
    public val success = false
    public val msg: String = ""
    public var data: List<UserInfo>? = null
    public var audio: List<MusicBean>? = null

}