package com.mooc.discover.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.LogEventConstants2
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

class RecommendBannerBean : BaseResourceInterface {
    var name: String = ""
    var background: String = ""
    var picture: String = ""
    var width = 0
    var link_type = 0
    var link_id: String = ""
    var link_url: String = ""
    var out_type: String = ""
    var out_type_id: String = ""
    var source_other_id: String = ""

    var basic_url: String = ""

    //跳转一级分类所需要参数
    val relation_type = 0  //1代表跳转资源, 2代表跳转一级分类, 3代表无配置
    val resource_type = 0   //，想要跳转的一级分类类型
    val sort_id = 0   //一级分类id

    var is_admin = true //是否是运营推荐的学习清单

    override val _resourceType: Int
        get() {
            return link_type
        }

    override val _resourceId: String
        get() {
            return link_id
        }

    override val _other: Map<String, String>
        get() {
            //h5跳转地址
            val realUrl = link_url
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to name,
                IntentParamsConstants.WEB_PARAMS_URL to realUrl
            )

            //添加打点需要的from参数
            hashMapOf.put(IntentParamsConstants.ACT_FROM_TYPE, LogEventConstants2.F_BANNER)
            //期刊特殊参数
            if (link_type == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()) {
                //如果是期刊资源，需要传递baseurl
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url)
            }

            //学习清单特殊参数
            if (_resourceType == ResourceTypeConstans.TYPE_SOURCE_FOLDER) {
                hashMapOf.put(
                    IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND,
                    is_admin.toString()
                )
                hashMapOf.put(IntentParamsConstants.STUDYROOM_FOLDER_NAME, name.toString())
            }
            return hashMapOf
        }

}