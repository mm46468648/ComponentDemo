package com.mooc.discover.model

import android.text.TextUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean

/**
 * 推荐页专栏数据
 */
class RecommendColumn(
    var tag: Int = 0,    //专栏样式数字代表对应的模版（ui设计稿首页样式）
    var title: String,    //专栏标题
    var is_subscribe: Boolean, //是否订阅
    var data: List<RecommendColumn>?,     //专栏中的数据

    var id: String,
    var resource_id: String = "", //具体的资源的id
    var type: Int,    //数据类型
//    var title:String,    //列表标题
    var big_image: String,    //大图
    var small_image: String,    //小图
    var cover_url: String,    //小图
    var publish_time: String,   //发布时间
    var update_time: String,   //更新时间时间
    var source: String,    //
    var link: String = "",    //展示链接
    var audio_data: MusicBean,  //自建音频
    var org: String,    //
    var position_one: Int = 0, //位置1显示更多还是换一换 //0隐藏 //1，显示换一换 2。显示查看全部
    var position_two: Int = 0,  //位置2显示更多还是换一换
    var list_tag: Int,
    var staff: String,    //
    var video_duration: String = "",    //时长
    var page_num: String,    //电子书页数
    var album_data: AlbumBean,    //音频课数据
    var track_data: TrackBean,    //音频数据
    var about: String? = "",    //关于
    var desc: String,    //描述
    var writer: String,   //作者
    var parent_name: String,
    var platform: String,   //平台
    var student_num: Int,    //学习人数
    var is_have_exam: String,
    var verified_active: String,
    var is_free: String,
    var platform_zh: String,
    var plan_start_users: String,
    var press: String,
    var master_info: MasterOrderBean,

    var word_count: Long,
    var basic_date_time: String,
    var price: Float,
    var event_task: EventTaskBean,
    val plan_starttime: String,
    var join_num: String = "",
    var start_time: String = "",
    var success_score: String = "",
    var task_start_date: String = "",//任务开始时间
    var task_end_date: String = "",
    var end_time: String = "",
    val time_mode: String = "0",// 时间是否是永久 1是永久
    var identity_name: String = "",// 推荐模板右上角角标文案，只有合集和课程类型取这个字段

    val plan_endtime: String,

    val join_start_time: String,

    val join_end_time: String,

    var is_free_info: String,

    val verified_active_info: String,

    val is_have_exam_info: String,

    var basic_url: String = "",

    var folder_id: String = "",  //学习清单真实id
    var name: String,    //学习清单标题
    var resource_count: String,    //学习清单文件数量
    var is_admin: Boolean,    //学习清单文件数量

    var periodicals_data: PublicationDataBean,
    //微知识相关
    var pic: String = "", //图片
    var click_num: String = "",//学习人数
    var exam_pass_num: String = "",//测试通过人数
    var like_num: String = "",//点赞数

    var task_data: TaskDetailsBean,
    //任务相关字段
    var status:Int = 0,
    var resource_info:ResourceInfo?
) : BaseResourceInterface {

    override val _resourceId: String
        get() {
            var resId = ""
            if (!TextUtils.isEmpty(resource_id) && resource_id != "0") {
                resId = resource_id
            } else if (!TextUtils.isEmpty(id) && id != "0") {
                resId = id
            } else {
                resId = link
            }
            return resId
        }
    override val _resourceType: Int
        get() = type
    override val _other: Map<String, String>
        get() {
            //h5跳转地址
            val realUrl = link

//            val a = ResourceTypeConstans.typeStringMap.get(type) ?: ""

            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to realUrl
            )


            if (type == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()) {
                //如果是期刊资源，需要传递baseurl
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url)
            }


            return hashMapOf
        }


}

/**
 * 微知识资源数据
 */
class ResourceInfo{
    var updated_time:String = ""
    var title:String = ""
}