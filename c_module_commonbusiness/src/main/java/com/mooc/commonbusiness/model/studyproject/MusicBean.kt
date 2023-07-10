package com.mooc.commonbusiness.model.studyproject

import android.os.Parcelable
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.model.audio.BaseAudioModle
import kotlinx.android.parcel.Parcelize

/**
 * 自建音频
 */
@Parcelize
class MusicBean : Parcelable,BaseAudioModle {
    override var id: String=""

    var audio_name: String=""
    var audio_content: String=""
    var audio_bg_img: String=""
    var audio_size: String=""
    var audio_time: Long=0
    var resource_select: String=""
    var resource_link: String=""
    var audio_play_num: Int = 0
    var status: String=""
    var weixin_link: String=""
    var set_time: String=""
    var isEnrolled = false
    var resource_type: String=""
    var isCanDel = false
    var is_testpaper: String=""        //是否有测试卷    1。有，其他没有
    var testpaper_url: String=""       //测试卷地址
    var testpaper_name: String=""      //测试卷名字
    var set_resource_show_time: String=""
    var study_plan_id: String=""
    var study_plan_name: String=""
    var study_plan_end_time: String=""


    override val trackTitle: String
        get() = audio_name
    override val albumTitle: String      //专辑名称，暂时使用学习项目名称
        get() = study_plan_name
    override val albumId: String    //专辑id，暂时使用学习项目id
        get() = study_plan_id
    override val playUrl: String
        get() = resource_link
    override val coverUrl: String
        get() = audio_bg_img
    override val lastDuration: Long
        get() = 0
    override val totalDuration: Long
        get() = audio_time
    override val resourceType: Int
        get() = ResourceTypeConstans.TYPE_ONESELF_TRACK

}