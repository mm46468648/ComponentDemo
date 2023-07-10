package com.mooc.commonbusiness.model.search

import android.text.TextUtils
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

/**
 * 刊物
 */
class PublicationBean:BaseResourceInterface, StudyResourceEditable {
    var id:String = ""
    var magname:String = ""           //标题
    var coverurl:String = ""
    var year:String = ""            //年
    var term:String = ""              //期
    var unit:String = ""            //机构
    var resource_id:String = ""            //学习室中使用的是这个字段
    val resource_status: Int = 0

    override val _resourceId: String
        get() = if(TextUtils.isEmpty(resource_id)) id else resource_id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_PUBLICATION
    override val _resourceStatus: Int
        get() = resource_status
    override val _other: Map<String, String>?
        get() = null
    override val resourceId: String
        get() = if(TextUtils.isEmpty(resource_id)) id else resource_id
    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_PUBLICATION.toString()
}