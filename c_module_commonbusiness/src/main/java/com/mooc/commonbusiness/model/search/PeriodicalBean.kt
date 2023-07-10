package com.mooc.commonbusiness.model.search

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

/**

 * @Author limeng
 * @Date 2020/8/20-2:27 PM
 */
data class PeriodicalBean(
        
        var id: String = "",
        var title: String = "",
        var basic_title: String = "",
        var basic_creator: String = "",
        var basic_date_time: String = "",
        var basic_description: String = "",
        var basic_cover_url: String = "",
        var basic_title_url: String = "",
        var basic_url: String = "",
        var url: String = "",
        var other_resource_id :String = "",   //期刊对应的资源id
        //期刊入库及相应字段
        var others:PeriodicalBean,
        val resource_type :Int = ResourceTypeConstans.TYPE_PERIODICAL,
        val basic_source_name : String = "期刊",
        val source : String = "1",
        val resource_status: Int = 0

): StudyResourceEditable,BaseResourceInterface {
    override var _resourceId: String
        get() = id
        set(value) {}
    override var _resourceType: Int
        get() = ResourceTypeConstans.TYPE_PERIODICAL
        set(value) {}
    override val _resourceStatus: Int
        get() = resource_status

    override var _other: Map<String, String>?
        get(){
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_URL to url,
                IntentParamsConstants.WEB_PARAMS_TITLE to title
            )
            //不为空的时候再传递
            if(basic_url.isNotEmpty()){
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL , basic_url)
            }
            return hashMapOf
        }
        set(value) {}

    override var resourceId: String
        get() = id
        set(value) {}
    override var sourceType: String
        get() = ResourceTypeConstans.TYPE_PERIODICAL.toString()
        set(value) {}
}