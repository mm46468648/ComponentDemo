package com.mooc.discover.model

import android.annotation.SuppressLint
import android.os.Parcel
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class MusicBean : Parcelable {
    var id: String = ""
    var audio_name: String = ""
    var audio_content: String = ""
    var audio_bg_img: String = ""
    var audio_size: String = ""
    var audio_time: Long = 0
    var resource_select: String = ""
    var resource_link: String = ""
    var audio_play_num: Int = 0
    var status: String = ""
    var weixin_link: String = ""
    var set_time: String = ""
    var isEnrolled = false
    var resource_type: String = ""
    var isCanDel = false
    var is_testpaper: String = ""
    var testpaper_url: String = ""
    var testpaper_name: String = ""
    var set_resource_show_time: String = ""
    var study_plan_id: String = ""
    var study_plan_name: String = ""
    var study_plan_end_time: String = ""
}