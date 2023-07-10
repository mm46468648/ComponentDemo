package com.mooc.studyproject.model

import android.annotation.SuppressLint
import android.os.Parcelable
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean
import com.mooc.commonbusiness.model.studyproject.MusicBean
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

/**
 * 学习清单
 */
@Parcelize
data class StudyPlanSource(
        val id: Int = 0,
        val study_plan: Int = 0,
        val source_type: Int = 0,
        val show_type: String = "",
        val source_title: String = "",
        val source_textsummary: String = "",
        val source_select_id: String = "",
        val source_other_id: String = "",
        val enrolled: Boolean = false,
        val checkin_start_time: Long = 0,
        val checkin_end_time: Long = 0,
        var has_checkin_activity: Int = 0,
        var checkin_activity_status: Int = 0,
        var is_re_chick: Boolean = false,
        val album_data: AlbumBean? = null,
        val track_data: TrackBean? = null,
        val audio_data: MusicBean? = null,
        val need_score_status: Int = 0,
        val need_score: Int = 0,
        var is_lock_up: Int = 0,//0不加锁，1加锁
        var is_click: Boolean = false,
        val set_resource_show_time: Long = 0,
        val set_is_complate: String = "",
        var audio_is_complate: String = "",
        val set_is_listen_test: String = "",
        var review_checkin_status: String = "",//0不需要审核 1 人工审核  2 自动审核
        var review_checkin_page: Array<String>? = null,//  音频 1 图片 2 文字 3

        var before_resource_check_status: String = "",//是否需要验证前置资源  0 无  1  打卡成功  2 有打卡记录

        var before_resource_info: BeforeResourceInfo? = null,
        val other_type: Int = 0,
        val checkin_bool: Int = 0,//是否打卡

        val checkin_button_text: String = "",//打卡按钮文案

        val review_button_text: String = "",//审核中按钮文案

        val checkin_fail_text: String = "",//打卡失败按钮文案

        val repeat_checkin_text: String = "",//重新打卡按钮文案
        val checkin_success_text: String = "", //打卡成功按钮文案
        val basic_url: String = "", //期刊basic_url
        var button: StudyClockState? = null//打卡按钮状态

) : Parcelable, BaseResourceInterface {


    override val _resourceId: String
        get() = if (source_type == ResourceTypeConstans.TYPE_COURSE) source_select_id else source_other_id
    override val _resourceType: Int
        get() = source_type
    override val _other: Map<String, String>?
        get() {
            val map = hashMapOf<String, String>()
            if (source_type == ResourceTypeConstans.TYPE_ARTICLE) {
                map[IntentParamsConstants.WEB_PARAMS_URL] = source_select_id + "?" + "planId=" + study_plan + "&resourceId=" + id

            } else if (source_type == ResourceTypeConstans.TYPE_PERIODICAL) {
                map[IntentParamsConstants.WEB_PARAMS_URL] = source_select_id
                map[IntentParamsConstants.WEB_PARAMS_TITLE] = source_title
                map[IntentParamsConstants.PERIODICAL_PARAMS_BASICURL] = basic_url
            } else if (source_type == ResourceTypeConstans.TYPE_TEST_VOLUME) {
                map[IntentParamsConstants.WEB_PARAMS_URL] = source_select_id + "?" + "planId=" + study_plan + "&resourceId=" + id
                map[IntentParamsConstants.WEB_PARAMS_TITLE ] = source_title
            } else {
                map[IntentParamsConstants.WEB_PARAMS_URL] = source_select_id
            }
            return map
        }


}

@Parcelize
class BeforeResourceInfo : Parcelable {
    var msg: String = ""
    var code: String = ""

}