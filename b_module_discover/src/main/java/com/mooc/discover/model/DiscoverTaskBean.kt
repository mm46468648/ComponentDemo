package com.mooc.discover.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

class DiscoverTaskBean : BaseResourceInterface {
    var id: String = ""
    var resource_id: String = ""
    var image_url: String = ""
    var base_img: String = ""
    var title: String = ""
    var join_num: String = ""
    var start_time: String = ""

    var task_start_date: String = ""//任务开始时间
    var task_end_date: String = ""
    var finish_data: TaskFinishBean ?=null
    var end_time: String = ""
    var limit_num: Int = 0//人数限制人数

    var status: Int = 0
    var type: Int = -1// 任务类型
    var calculate_type: Int = -1 //任务类型  1普通任务 2每日任务 3累计任务 4累计任务
    var task_resource_type: Int = -1//资源类型
    var is_bind_folder: Boolean = false//是否绑定学习清单
    var is_limit_num : Boolean = false  //是否限制人数
    var time_mode: Int = 0  //任务时间是否为不限时   1,不限时,永久任务,默认0


    var resource_type = 0
    var url: String = ""           //在今日任务中，此字段代表任务详情id
    var basic_url: String = ""
    var notice: String = ""
    var source_data: SourceData? = null
    var score:Score? = null


    override val _resourceId: String
        get() = resource_id

    override val _resourceType: Int
        get() = resource_type

    override val _other: Map<String, String>?
        get() {
            if (source_data != null) {
                val s = ResourceTypeConstans.typeStringMap.get(_resourceType) ?: ""
                val hashMapOf = hashMapOf(
                        IntentParamsConstants.WEB_PARAMS_TITLE to s,
                        IntentParamsConstants.WEB_PARAMS_URL to source_data!!.source_link
                )

                if (_resourceType == ResourceTypeConstans.TYPE_PERIODICAL && source_data?.basic_url?.isNotEmpty() == true) {
                    hashMapOf[IntentParamsConstants.PERIODICAL_PARAMS_BASICURL] = source_data!!.basic_url
                }
                return hashMapOf
            }
            return null

        }


}

class SourceData {
    var source_link: String = ""      //h5链接
    var source_select: String = ""        //资源id
    var basic_url: String = ""       //期刊使用
    var other_type: Int = -1         //资源类型

}
