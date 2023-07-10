package com.mooc.search.model

import android.text.TextUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.manager.ResourceTurnManager
import com.mooc.commonbusiness.model.UserInfo

/**
 * 统一的搜索结果模型
 * 只取需要的内容.
 */
class SearchItem : BaseResourceInterface {

    //资源相关
    var resource_type: Int = 0       //音频有,课程没有
    var ownTotalCount: Int = 0            //自定义的全部资源数量,用于头部显示,和资源本身无关

    var id: String = ""


    var albumTitle: String = ""

    var album_title: String = ""
    var question: String = ""
    var answer: String = ""
    var coverurl: String = ""
    var track_title: String = ""
    var summary: String = ""
    var unit: String = ""
    var term: String = ""
    var year: String = ""
    var platform_zh: String = ""
    var magname: String = ""
    var source: String = ""
    var writer: String = ""
    var press: String = ""
    var course_start_time: String = ""
    var picture: String = ""
    var cover_url_small: String = ""
    var basic_cover_url: String = ""
    var basic_creator: String = ""
    var basic_date_time: String = ""
    var org: String = ""
    var title: String = ""

    var is_have_exam: Int = 0
    var verified_active: Int = 0
    var is_free: String = "0"
    var word_count: Int = 0
    var play_count: Int = 0
    var include_track_count: Int = 0
    var video_duration: Long = 0
    var duration: Long = 0
    val resource_status: String = ""

    //文章相关数据
    var url: String = ""

    //期刊相关数据
    var basic_url: String = ""

    //学习清单相关数据
    var name: String = ""
    var username: String = ""
    var user_id: String = ""
    var resource_count: String = ""


    //学习项目相关
    var plan_img: String = ""
    var plan_name: String = ""
    var plan_num: String = ""
    var plan_starttime: String = ""
    var join_start_time: String = ""
    var join_end_time: String = ""
    var plan_endtime: String = ""
    var plan_start_users: List<UserInfo>? = null
    var time_mode: Int = 0

    //微知识相关
    var pic: String = ""//图片
    var click_num: String = ""//学习人数
    var exam_pass_num: String = ""//测试通过人数
    var like_num: String = ""//点赞数

    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = resource_type
    override val _resourceStatus: Int
        get() {
            if (TextUtils.isEmpty(resource_status)) {
                return 0
            } else {
                return resource_status.toInt()
            }
        }
    override val _other: Map<String, String>
        get() {
            //文章需要添加文章标题和链接
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to url
            )

            //不为空的时候再传递
            if (basic_url.isNotEmpty()) {
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url)
            }
            return hashMapOf
        }

}