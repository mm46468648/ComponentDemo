package com.mooc.commonbusiness.scheme


import com.mooc.commonbusiness.interfaces.BaseResourceInterface

class SchemaBean : BaseResourceInterface {


    var resource_id: String = ""
    var resource_type: Int = -1
    var resourceStatus: Int = 0
    var resource_link:String=""
    var resource_title:String=""

    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>?
        get() {
            val hashMapOf = hashMapOf<String, String>()

            return hashMapOf
        }


}