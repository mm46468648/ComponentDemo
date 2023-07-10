package com.mooc.commonbusiness.model.studyproject

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 学习动态
 */
@Parcelize
data class StudyDynamic(
        var id: String = "",
        var study_plan: Int = 0,
        var user: DynamicUser? = null,
        var publish_time: String? = null,
        var publish_content: String? = null,
        var publish_img: String? = null,
        var like_num: Int = 0, //点赞数
        var comment_num: Int = 0,//动态评论数


        var publish_state: Int = 0,//动态的状态 0是屏蔽 1是显示

        var is_like: Boolean = false,//是否点赞

        var activity_type: Int = 0,//0为文本类型，1为语音类型

        var activity_content_long: String? = null,
        var activity_checkin_type: Int = 0,
        var review_status: Int = 0,//0审核中，1未通过，2审核通过

        var source_title: String? = null,
        var is_activity_user: Int = 0,
        var is_time_out: Int = 0,
        var is_top: Int = 0,
        var publish_img_list: ArrayList<String>? = null,
        var is_studyplan_start_user: Boolean = false,
        var studyplan_display_tag: String = "", //整个项目设置标签内容 （精华内容）
        var display_tag: String = "",//单个动态标签内容 （精华内容）
        var display_time: Long = 0,//打开动态显示时间，默认为0   时间戳格式
        var is_timing_show: String = "0",//是否设置定时 1是，0不是，当为0的时候设置时间，也不定时
        var activity_num: Int = 0,
        var studyplan_is_open_activity: String? = null, //是否显示（新的动态区逻辑）1用旧的逻辑，0新的 （前段逻辑已经写完，后端还没有确定什么时候用，所以加了这个开关控制）

        var activity_resource_time: String? = null, //继续使用这个字段，后段通过开关控制

        var source_type_id: Int = 0,//与动态相关的资源类型的id

        var extra_info: ExtraInfoBean? = null,
        var activity_nomination: NominationBean? = null


) : Parcelable {


    /**
     * 跟读语音动态，额外信息
     */
    @Parcelize
    class ExtraInfoBean : Parcelable {
        var repeat_record //跟读音频记录
                : ArrayList<RepeatRecordBean>? = null
    }

    @Parcelize
    class RepeatRecordBean : Parcelable {
        var audio_time: String? = null
        var audio_url: String? = null
    }

    @Parcelize
    class NominationBean : Parcelable {
        var status: String? = null //1,我要自荐   2,自荐审核中  3,自荐未通过  4,自荐通过  0,""
        var text: String? = null  //我要自荐按钮的文案
    }
}