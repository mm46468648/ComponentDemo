package com.mooc.commonbusiness.model.eventbus

import com.mooc.commonbusiness.model.UserInfo

/**
 * 此事件只能判断登录状态
 * 退出或者登录
 * @param userInfo == null 的时候为退出登录状态
 */
data class UserLoginStateEvent(var userInfo: UserInfo?)