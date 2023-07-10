package com.mooc.setting.model

data class SettingInteractionMsgBean(
        var msg: String? = null,
        var success: Boolean = false,
        var  allow_interaction: Boolean = false

)