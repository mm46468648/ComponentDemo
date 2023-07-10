package com.mooc.home.model.todaystudy

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface

/**
 * 今日订阅（专栏）,和今日专题是一个模型
 */
class TodaySuscribe: BaseResourceInterface{

    var offset : Int = 0
    var id : String = ""
    var special_status:ArrayList<TodaySuscribe>? = null
    var colum_status:ArrayList<TodaySuscribe>? = null
    var colum_name : String = ""
    var colum_id: String = ""  //专栏id，用于专栏跳转
    var subject_id: String = ""   //专题id，用于专题跳转
    var resource_list:ArrayList<TodaySuscribe>? = null

    //方便复用，将列表中的字段写在一起
    var title:String = ""
    var publish_time:String = ""

    //点击完成需要上传的字段
    var resource_id:String = ""   //这个字段，需要取id的值去上报
    var parent_id:String = ""
    var task_type:String = ""
    var resource_info:String = ""  //如果是专栏，这个字段传parent_id,如果是最热资源，这个字段传resource_type
    var type : Int = -1 //引用的资源的类型
    //
    var is_hide : Boolean = false        //是否有隐藏的数据
    var is_subscribe : Boolean = false   //是否有订阅
    var is_complete : Boolean = false    //是否全部完成（除了隐藏数据）
    var link :String = "" //如果是文章，需要加载的链接
    var basic_url :String = "" //期刊类型，专属的url字段

    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = type
    override val _other: Map<String, String>
        get() {
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to link
            )
            if(type == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()){
                //如果是期刊资源，需要传递baseurl
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL,basic_url)
            }

            return hashMapOf
        }


}
