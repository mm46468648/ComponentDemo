package com.mooc.discover.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

class QuickEntry : MultiItemEntity, BaseResourceInterface {
    var source_type = 0
    var link: String = ""
    var basic_url: String = "" //期刊使用的
    var name: String = ""
    var source_link: String = ""
    var relation_type = 0
    var source_select: String = "0" //资源id
    var other_type = 0
    var sort_id = "" //想要跳转的资源分类下的二级分类
    var show_type = 0 //  资源是否下线，0 为正常 -1 为资源下线/删除/隐藏
    var id: String = "" //快捷入口id

    override var itemType = 0

    override val _resourceId: String
        get() = source_select
    override val _resourceType: Int
        get() = source_type
    override val _other: Map<String, String>?
        get() {
            val hashMapOf = hashMapOf(
                    IntentParamsConstants.WEB_PARAMS_TITLE to name,
                    IntentParamsConstants.WEB_PARAMS_URL to source_link
            )

            //添加打点需要的from参数
            hashMapOf.put(IntentParamsConstants.ACT_FROM_TYPE, LogEventConstants2.F_QUICK)

            if (source_type == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()) {
                //如果是期刊资源，需要传递baseurl
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url)
            }
            return hashMapOf
        }
}