package com.mooc.commonbusiness.model.home

import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

/**
 * 自建音频数据模型
 */
data class OwnTrackBean(
        var id: String = "",
        var audio_name: String? = "",
        val audio_content: String = "",
        val audio_play_num: String = "0",
        val audio_bg_img: String = "",
        val audio_size: String = "",
        val audio_time: String = "",
        val resource_status: Int = 0

) : StudyResourceEditable, BaseResourceInterface{
    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_ONESELF_TRACK

    override val _resourceStatus: Int
        get() = resource_status

    override val _other: Map<String, String>?
        get() = null

    override val resourceId: String
        get() = id
    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_ONESELF_TRACK.toString()
}