package com.mooc.commonbusiness.model

data class UserInfo(
    var nickname: String? = null,
    var avatar_identity: String? = null,
    var name: String? = "",   //真正的用户名字
    var token: String? = null,
    var id: String = "",      //代表用户真正的id
    var avatar: String? = null,    //头像
    var introduction: String? = null,
    var isHome: Boolean = false,
    var user_follow_count: Int = 0,
    var user_be_followed_count: Int = 0,
    var is_checkin: Boolean = false,
    var check_name_result: String? = null,
    var uuid: String? = null,
    var user_id: String = "",     //一般和其他用户相关操作，使用这个字段
    var check_user_info: CheckUserInfo?  //用户信息是否包括敏感信息
)