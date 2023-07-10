package com.mooc.commonbusiness.model.my

/**

 * @Author limeng
 * @Date 2020/11/11-2:18 PM
 */
data class NodeBean(
        var id: Int = 0,
        var user_id: Int = 0,
        var other_resource_type: Int = 0,
        var other_resource_id: Int = 0,
        var other_resource_url: String? = null,
        var status: Int = 0,
        var create_time: String? = null,
        var update_time: String? = null,
        var other_resource_title: String? = null,
        var recommend_id: String? = null,
        var recommend_title: String? = null,
        var recommend_type: String? = null,
        var content: String? = null,
        var is_active: Boolean = false,
        var in_guidang: Boolean = false,
        var is_enrolled: Boolean = false,
        var canDel: Boolean = false,
        var basic_title_url: String? = null,
        var basic_url: String? = null
)