package com.mooc.commonbusiness.interfaces

/**
 * 资源类型基类接口
 */
interface BaseResourceInterface {

    val _resourceId : String
    val _resourceType : Int
    val _resourceStatus : Int         //资源是否下线 ，0正常在线状态，-1下线或隐藏
        get() = 0
    val _other : Map<String, String>?

}