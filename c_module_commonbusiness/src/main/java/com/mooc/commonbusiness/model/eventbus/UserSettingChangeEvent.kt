package com.mooc.commonbusiness.model.eventbus

import com.mooc.commonbusiness.model.setting.UserSettingBean

/**
 * 隐私设置后监听
 */
data class UserSettingChangeEvent(var userSettingBean: UserSettingBean?, var flag: Int)