package com.mooc.discover.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

/**
 * 首页加入活动浮窗
 */
data class ActivityJoinBean(
        var picture_float: String? = null,
        var basic_url: String = "",  //期刊
        var resource_id: String? = null,
        var resource_type: String? = null,
        var link_id: String? = null,
        var picture_close: String? = null,
        var picture_frame: String? = null,
        var url: String? = null,
        var countdown: Int = 0,
        var is_popup: Boolean? = null
) : BaseResourceInterface {
    override val _resourceId: String
        get() = resource_id.toString()
    override val _resourceType: Int
        get() = resource_type?.toInt()!!
    override val _other: Map<String, String>?
        get(){
            val map = hashMapOf<String,String>()
            if (basic_url.isNotEmpty()) {
                map[IntentParamsConstants.PERIODICAL_PARAMS_BASICURL] = basic_url
            } else if(link_id != null){
                map[IntentParamsConstants.WEB_PARAMS_URL] = link_id?:""
            }else {
                return null
            }
            return map
        }
//            hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to link_id)

}