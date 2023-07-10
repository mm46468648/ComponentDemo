package com.mooc.commonbusiness.model.search

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

data class FolderBean(var username: String,
                      var folder_id: String,
                      var user_id: Int,
                      var name: String,
                      var resource_count: Int, var id: Int = 0) : BaseResourceInterface {


    override val _resourceId: String
        get() = id.toString()
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_SOURCE_FOLDER
    override val _other: Map<String, String>
        get() {
            //学习清单详情页需要原始folderId，请求已删除的资源
            val hashMapOf = hashMapOf(IntentParamsConstants.STUDYROOM_FROM_FOLDER_ID to folder_id)
            hashMapOf.put(IntentParamsConstants.STUDYROOM_FOLDER_NAME, name)
            hashMapOf.put(IntentParamsConstants.MY_USER_ID, user_id.toString())
            return hashMapOf
        }

}