package com.mooc.studyroom.model

import com.chad.library.adapter.base.entity.MultiItemEntity

/**

 * @Author limeng
 * @Date 2020/9/23-7:23 PM
 */
data class MedalTypeBean(
    override val itemType: Int,
    val title: String,
    val count: String,
    var isAll: Boolean = false//是否显示所有的 点亮或者未点亮的勋章
    ,
    var medalList: ArrayList<MedalDataBean>
) : MultiItemEntity {
}

data class MedalDataBean(
    var title: String? = null,
    var before_img: String? = null,
    var after_img: String? = null,
    var medal_time: Long? = null,
    var type: String? = null,
    var is_obtain: String? = null,
    var resource_type: Int,//资源类型
    var resource_id: String? = null//资源id

)