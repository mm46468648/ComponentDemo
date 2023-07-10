package com.mooc.commonbusiness.model.privacy

/**
 * 隐私政策弹窗查询
 */
data class PrivacyPolicyCheckBean(
        var id: Number? = 0,
        var create_user_name: String = "",
        var update_user_name: String? = null,
        var created_time: String? = null,
        var updated_time: String? = null,
        var create_user_id: Number? = 0,
        var update_user_id: Number? = 0,
        var title: String? = null,
        var version: String? = null,//政策版本
        var describe: String? = null,
        var used_time: String? = null,//生效日期
        var status: Number? = 0,

        )