package com.mooc.microknowledge.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.model.search.TrackBean

/**
 * 微知识资源
 */
class KnowledgeResource : BaseResourceInterface {

    var id: String = ""
    var knowledge_resource_id: String = ""  //资源外壳id

    var title: String = ""
    var name: String = ""
    var resource_id: String = ""
    var resource_type: Int = 0
    var big_image: String = ""
    var small_image: String = ""
    var recommend_reason: String = ""
    var desc: String = ""

    var classType = -1

    //文章相关字段
    var platform_zh: String = ""
    var source: String = ""
    var source_zh: String = ""//来源
    var picture: String = ""
    var url: String = ""
    var staff: String = ""

    //音频相关
    var track_title: String = ""
    var albumTitle: String = ""
    var play_count: Int = 0
    var cover_url_small: String = ""
    var cover_url_middle: String = ""
    var cover_url_large: String = ""
    var duration: Long = 0
    var track_data: TrackBean? = null

    //自建音频
    var audio_play_num: Int = 0
    var audio_time: Long = 0
    var audio_bg_img: String = ""

    //音频专辑
    var album_title: String = ""
    var include_track_count: Int = 0

    //任务相关字段
    var image_url: String = ""
    var base_img: String = ""
    var join_num: String = ""
    var start_time: String = ""
    var success_score: String = ""

    var task_start_date: String = ""//任务开始时间
    var task_end_date: String = ""
    var end_time: String = ""
    var limit_num: Int = 0//人数限制人数

    var status: Int = 0
    var is_limit_num: Boolean = false  //是否限制人数
    var time_mode: Int = 0  //任务时间是否为不限时   1,不限时,永久任务,默认0
    var score: Score? = null

    //电子书相关
    var writer: String = ""
    var press: String = ""
    var word_count: Long = 0

    //课程相关字段
    var org: String = ""
    var course_start_time: String = ""

    var is_have_exam: Int = 0
    var verified_active: Boolean = false
    var is_free: Boolean = false

    //期刊相关
    var basic_url: String = ""


    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>?
        get() {
            //文章需要添加文章标题和链接
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to url,
                IntentParamsConstants.PARAMS_PARENT_ID to knowledge_resource_id,
                IntentParamsConstants.PARAMS_PARENT_TYPE to ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE.toString()
            )

            //不为空的时候再传递
            if (basic_url.isNotEmpty()) {
                hashMapOf[IntentParamsConstants.PERIODICAL_PARAMS_BASICURL] = basic_url
            }
            return hashMapOf
        }
}

data class Score(
    var success_score: String = "",//有可能是直接的奖励积分,有可能是积分的范围如:35~63
    var fail_score: String = ""         //同奖励积分
) {
}