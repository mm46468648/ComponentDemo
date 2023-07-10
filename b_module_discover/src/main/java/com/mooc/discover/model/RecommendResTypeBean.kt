package com.mooc.discover.model

/**
 * Created by huangzuoliang on 2018/2/28.
 */
class RecommendResTypeBean {
    var recommend_types: ArrayList<RecommendTypesBean>? = null

    class RecommendTypesBean {
        /**
         * type_name : 课程
         * type_id : 2
         */
        var type_name: String? = null
        var type_id = 0
    }
}