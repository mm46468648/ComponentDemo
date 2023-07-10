package com.mooc.home.model.todaystudy

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

data class TodayTask(
    var count: Int = 0,
    var offset: Int = 0,
    var name: String = "",//任务标题
    var nameShow: Boolean = false,//专栏是否显示
    var title: String = "",//资源名字
    var link: String = "",//h5资源跳转链接
    var desc: String = "",//任务描述
    var state: String = "",//任务状态
    var obj: Any? = null, //任务的具体数据模型
    var checkin_status: List<CheckinStatusBean>? = null,
    var colum_status: List<ColumnStatusBean>? = null,
    var studyplan_status: List<ColumnStatusBean>? = null,
    var task_system_status: List<TaskStatus>? = null,
    var special_status: List<ColumnStatusBean>? = null,
    var most_hot_status: List<ColumnStatusBean>? = null,
    var resource_id: String = "",
    var resource_type: Int = 0,

//    var display_status: String = "",       //我的任务跳转类型
    var url: String = "",       //我的任务跳转id
    var basic_url: String = ""       //期刊使用地址
) : BaseResourceInterface {
    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>
        get() {
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to link
            )
            if (_resourceType == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()) {
                hashMapOf[IntentParamsConstants.PERIODICAL_PARAMS_BASICURL] = basic_url
            }
            return hashMapOf
        }

}

data class CheckinStatusBean(
    var continues_days: Int = 0,
    var has_checkin: Boolean = false
)

//专栏，专题，学习计划，公共数据模型
data class ColumnStatusBean(
    var colum_name: String = "", //专栏,专题名称
    var plan_name: String = "", //学习计划名称
    var title: String = "",   //专栏标题
    var link: String = "",//h5资源跳转链接
    var source_link: String = "",//家最近在看的资源跳转链接
    var source_title: String = "",   //学习计划资源标题
    var publish_time: String = "",//更新时间
    var source_select_id: String = "",//学习计划中引用资源id
    var source_type: Int = 0,//学习计划引用学习资源类型
    var resource_type: Int = -1,//大家最近在看的资源引用学习资源类型
    var is_open: Int = 0,
    var resource_id: String = "",
    var resource_num: Int = 0,
    var type: Int = 0,
    var resource_list: List<ColumnStatusBean>
)



