package com.mooc.splash.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface


/**
 * 启动页模型
 */
data class LaunchBean(val launch_picture: String) : BaseResourceInterface {

    val resource_type:Int = -1
    val resource_id :String = ""
    val link: String = ""
    val basic_url: String = ""
    val title: String = ""


    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>?
        get(){
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to link
            )

            //添加打点需要的from参数
            hashMapOf.put(IntentParamsConstants.ACT_FROM_TYPE, LogEventConstants2.F_LAUNCH)
            //如果是期刊资源，需要传递baseurl
            if(resource_type == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()){
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL,basic_url)
            }
            return hashMapOf
        }
}