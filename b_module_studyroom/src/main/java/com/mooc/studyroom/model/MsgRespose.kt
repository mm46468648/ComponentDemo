package com.mooc.studyroom.model

/**
 *masg 处理统一返回的bean
 * @author limeng
 * @date 2021/3/4
 */
data class MsgRespose<T>(

        val results: ArrayList<T>? = null, //泛型T来表示object，可能是数组，也可能是对象,有的接口是results字段
        val count: Int = 0
)