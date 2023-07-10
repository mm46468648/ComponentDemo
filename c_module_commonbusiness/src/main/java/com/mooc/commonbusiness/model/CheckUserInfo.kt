package com.mooc.commonbusiness.model

/**
 * 用户信息是否包含敏感词
 */
class CheckUserInfo(
    var check_avatar_info: String? = "",//头像提示文案
    var check_name_info: String? = "",//昵称提示文案
    var check_info: String? = "",//提示文案
    var check_avatar_status: Boolean = true,//头像是否包含敏感词 true正常，false有问题
    var check_name_status: Boolean = true,//昵称是否包含敏感词 true正常，false有问题
)