package com.mooc.commonbusiness.model.microknowledge

import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

data class MicroKnowBean(
    var id: String = "",//id
    var title: String = "",//标题
    var pic: String = "", //图片
    var click_num: String = "",//学习人数
    var exam_pass_num: String = "",//测试通过人数
    var exam_num: String = "",//测试通过人数
    var like_num: String = "",//点赞数
    val resource_status: String? = null,
    var resource_type: Int = 0 //资源类型

) : StudyResourceEditable, BaseResourceInterface {
    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE
    override val _resourceStatus: Int
        get() {
            if (resource_status.isNullOrEmpty()) {
                return 0
            } else {
                return resource_status.toInt()
            }
        }
    override val _other: Map<String, String>
        get() = TODO("Not yet implemented")
//    override val _other: Map<String, String>
//        get() {
//            val hashMapOf = hashMapOf(
//                IntentParamsConstants.WEB_PARAMS_TITLE to title,
//                IntentParamsConstants.WEB_PARAMS_URL to id
//            )
//            return hashMapOf
//        }

    override val resourceId: String
        get() = id
    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE.toString()
}