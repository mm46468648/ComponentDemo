package com.mooc.setting.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**

 * @Author limeng
 * @Date 2021/2/3-3:38 PM
 */
data class SettingMsgBean(var title: String? = null,
                          var courseId:String?=null,
                          var status: Boolean? = false,// 控制上面的互动消息 显示和隐藏
                          var statusItem: String?=null,//是关于开关属于哪一类，是课程还是公告的 是2课程  1公告
                          var notice_status: String?=null,// 控制
                          override val itemType: Int)//区分是提示条还是开关的item 1 开关 2 是提示，
    : MultiItemEntity



