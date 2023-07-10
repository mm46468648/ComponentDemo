package com.mooc.studyroom.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

class SystemResourceBean : BaseResourceInterface {
    var link: String? = null
    var resource_title: String? = null
    var type: String? = null
    var resource_way = 0
    var resource_id: String? = null

    //     Track track_data;
    //     Album album_data;
    //     EventTaskBean event_task;
    var basic_title_url: String? = null
    var basic_url: String? = null
    var other_type = 0
    override var _resourceId: String
        get() = if (resource_id.isNullOrEmpty()) "" else resource_id!!
        set(value) {}
    override var _resourceType: Int
        get() =if (type.isNullOrEmpty()) 0 else type?.toInt()!!
        set(value) {}
    override var _other: Map<String, String>?
        get(){
            val map = hashMapOf<String,String>()
            if (link?.isNotEmpty() == true) {
                map[IntentParamsConstants.WEB_PARAMS_URL] =link!!
            }
            return map
        }
        set(value) {}
}