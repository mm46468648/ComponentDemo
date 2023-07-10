package com.mooc.commonbusiness.interfaces

/**
 * 资源类型基类接口
 */
interface BaseResource<T> {

    var resourseType : Int
    var resourseId : String
    var resourseObj : T
}