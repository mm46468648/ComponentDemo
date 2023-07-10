package com.mooc.setting.model

/**
 *设置中的
 * @author limeng
 * @date 2021/2/3
 */
data class SettingCourseMsgBean(var id: String? = null,
                                var user_id: String? = null,
                                var course_id: String? = null,
                                var status: String? = null,
                                var created_time: String? = null,
                                var updated_time: String? = null,
                                var course_title: String? = null,
                                var notice_status: String? = null
)

