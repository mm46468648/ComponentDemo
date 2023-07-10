package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.NormalConstants
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.global.GlobalsUserManager
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.StudyStatusBean
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.UnsupportedEncodingException
import java.net.URLEncoder

/**

 * @Author limeng
 * @Date 2021/2/24-10:39 AM
 */
class DataBoardViewModel : BaseViewModel() {
    var result = MutableLiveData<StudyStatusBean?>()
    var mUrl = MutableLiveData<String>()

    /**
     * 获取数据面板数据
     */
    fun getDataBordData() {
        launchUI ({
            val bean = ApiService.getRetrofit().create(StudyRoomApi::class.java).getDataBoardData().await()
            result.postValue(bean)
        },{
            result.postValue(null)
        })

    }

    fun getShareData(mStatusBean: StudyStatusBean?) {
        launchUI {
            val jsonObject = JSONObject()
            try {
                jsonObject.put("source_type", NormalConstants.SHARE_SOURCE_TYPE_APP_DATABOARD.toString())
                jsonObject.put("source_id", "0")
            } catch (e: JSONException) {
                e.printStackTrace()
            }
            val toRequestBody = jsonObject.toString().toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
            val cmsData = HttpService.commonApi.getCMSShareData(toRequestBody)
            val targetUrl = cmsData.data?.url + getShareKeyValue(cmsData.data?.url, mStatusBean)
            mUrl.postValue(targetUrl)
        }
    }

    private fun getShareKeyValue(url: String?, statusBean: StudyStatusBean?): String? {
        val sb = StringBuffer()
        val userBean = GlobalsUserManager.userInfo
        sb.append("&course_cnt=" + statusBean?.course_cnt)
        try {
            sb.append("&name=" + URLEncoder.encode(statusBean?.name, "UTF-8"))
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }
        if (  userBean != null&& !url?.contains("share_user_id")!!) {
            sb.append("&share_user_id=" + userBean.id)
        }
        sb.append("&today=" + statusBean?.today)
        sb.append("&today_duration=" + statusBean?.today_duration)
        sb.append("&micro_course=" + statusBean?.micro_course)
        sb.append("&article=" + statusBean?.article)
        sb.append("&baike=" + statusBean?.baike)
        sb.append("&course_duration=" + statusBean?.course_duration)
        sb.append("&knowledge=" + statusBean?.knowledge)
        sb.append("&periodical=" + statusBean?.periodical)
        sb.append("&track_count=" + statusBean?.track_count)
        sb.append("&ebook=" + statusBean?.ebook)
        sb.append("&user_check_count=" + statusBean?.user_check_count)
        sb.append("&study_total_count=" + statusBean?.study_total_count)
        sb.append("&study_continue_count=" + statusBean?.study_continue_count)
        sb.append("&learn_today_time=" + statusBean?.learn_today_time)
        sb.append("&learn_all_time=" + statusBean?.learn_all_time)

        sb.append("&today_score=" + statusBean?.today_score)
        sb.append("&total_score=" + statusBean?.total_score)
//        if (statusBean?.share_medal_count?.toInt() != null&&statusBean?.check_medal_count?.toInt()!=null
//                &&statusBean?.specail_medal_count?.toInt()!=null) {
//            val count: Int = statusBean?.check_medal_count?.toInt()!!
//            +statusBean?.share_medal_count?.toInt()!!
//            + statusBean?.specail_medal_count?.toInt()!!
//            sb.append("&medal_count=$count")
//        }
        var count: Int = statusBean?.check_medal_count?.toInt()?:0
        count += statusBean?.share_medal_count?.toInt()?:0
        count += statusBean?.specail_medal_count?.toInt()?:0
        sb.append("&medal_count=$count")

        sb.append("&is_share=" + 1)
        sb.append("&is_master=" + 0)
        sb.append("&resource_type=" + ShareTypeConstants.TYPE_SCORE_BOARD)
        return sb.toString()
    }
}