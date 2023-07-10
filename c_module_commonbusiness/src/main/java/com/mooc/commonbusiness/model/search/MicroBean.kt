package com.mooc.commonbusiness.model.search

import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import java.io.Serializable

data class MicroBean(
        var id: String = "",
        var picture: String? = "",
        var course_id: String? = "",
        var created_time: String? = "",
        var video_duration: Long = 0,
        var title: String = "",
        var enrolled: String? = "",
        var platform_zh: String = "",
        var learned_process: String = "", //完成进度

        //微课详情中需要的字段
        var url: String = "",//h5页面加载地址
        var share_url: String = "",//分享的url
        var share_picture: String = "",//分享的图片
        var share_desc: String = "",//分享描述
        val resource_status: Int = 0


) : Serializable, StudyResourceEditable,BaseResourceInterface {
    override var _resourceId: String
        get() = id
        set(value) {}
    override var _resourceType: Int
        get() = ResourceTypeConstans.TYPE_MICRO_LESSON
        set(value) {}
    override val _resourceStatus: Int
        get() = resource_status

    override var _other: Map<String, String>?
        get() = null
        set(value) {}

    override var resourceId: String
        get() = id
        set(value) {}
    override var sourceType: String
        get() = ResourceTypeConstans.TYPE_MICRO_LESSON.toString()
        set(value) {}
}