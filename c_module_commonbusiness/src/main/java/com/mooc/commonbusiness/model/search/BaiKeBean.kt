package com.mooc.commonbusiness.model.search

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

/**
 * @Author limeng
 * @Date 2020/8/18-8:21 PM
 */
data class BaiKeBean (
    var id: String = "",
    var other_resource_id: String = "",  //学习室收藏使用的是这个字段代表id
    var title: String = "",
    var content: String = "", //学习室收藏使用的是这个字段代表简介，学友圈也是用这个字段
    var summary: String = "",      //搜索接口使用的是这个字段代表简介
    var link: String = "",  //搜索接口使用的是这个字段
    var url:String = "",   //学习室收藏中的接口使用的是这个字段
    val resource_status: Int = 0

) :  StudyResourceEditable,BaseResourceInterface {
    override val _resourceId: String
        get() = other_resource_id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_BAIKE
    override val _resourceStatus: Int
        get() = resource_status

    override val _other: Map<String, String>?
        get(){
            val realUrl = if(link.isNotEmpty()) link else url
            val realContent = if(summary.isNotEmpty()) summary else content

            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to realUrl,
                IntentParamsConstants.BAIKE_PARAMS_SUMMARY to realContent
            )
            return hashMapOf
        }


    override val resourceId: String
        get() = other_resource_id
    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_BAIKE.toString()
}