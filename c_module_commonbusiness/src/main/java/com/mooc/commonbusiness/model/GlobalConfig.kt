package com.mooc.commonbusiness.model

/**

 * @Author limeng
 * @Date 2021/4/13-11:27 AM
 */
data class GlobalConfig (
    var is_change_gray: String? = null,
//    var skin_name :String? = ""    //皮肤名称
    var topic_type :String? = ""    //皮肤配置 1默认,2节日,3党政
)