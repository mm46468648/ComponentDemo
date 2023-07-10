package com.mooc.studyroom.model

import java.util.*

data class MedalBean(
        var studyplan_medal_count: String ,
        var check_medal_count: String ,
        var share_medal_count: String ,
        var special_medal_count: String ,



        var share_medal_list: ArrayList<MedalDataBean>?,
        var share_medal_finish_list: ArrayList<MedalDataBean>?,
        var share_medal_not_finish_list: ArrayList<MedalDataBean>?,


        var check_medal_list: ArrayList<MedalDataBean>?,
        var check_medal_finish_list: ArrayList<MedalDataBean>?,
        var check_medal_not_finish_list: ArrayList<MedalDataBean>?,

        var special_medal_list: ArrayList<MedalDataBean>?,
        var special_medal_finish_list: ArrayList<MedalDataBean>?,

        var study_medal_list: ArrayList<MedalDataBean>?,
        var study_medal_finish_list: ArrayList<MedalDataBean>?,
        var study_medal_not_finish_list: ArrayList<MedalDataBean>?
)