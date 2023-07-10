package com.mooc.commonbusiness.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

class MicroProfession:BaseResourceInterface {

    var id : String = ""
    var title : String = ""
    var img : String = ""
    var learn_num : String = ""
    var user_cert_num : String = ""
    var org : String = ""
    var link : String = ""

    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_MICRO_PROFESSIONAL
    override val _other: Map<String, String>?
        get() = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to link)

}