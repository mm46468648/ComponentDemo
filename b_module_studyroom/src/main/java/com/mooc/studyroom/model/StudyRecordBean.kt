package com.mooc.studyroom.model

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.model.search.TrackBean

/**
 * 学习记录Bean
 */
data class StudyRecordBean(
    var id: String,
    var title: String,
    var type: Int,    //资源类型
    var learn_date: Long,    //学习时间，单位，秒
    var link: String = "",
    var basic_url: String = "",
    val resource_id: Int = 0,
    var track_data: TrackBean?,
    val resource_status: Int = 0//

) : BaseResourceInterface {
    override val _resourceId: String
        get() = resource_id.toString()
    override val _resourceType: Int
        get() = type
    override val _resourceStatus: Int
        get() = resource_status
    override val _other: Map<String, String>
        get() {
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to link
            )

            //如果是单条音频需要传递专辑id
//            if (_resourceType == ResourceTypeConstans.TYPE_TRACK){
//                if(track_data?.subordinated_album!=null) {
//                    val s = track_data?.subordinated_album?.id ?: ""
//                    hashMapOf.put(IntentParamsConstants.ALBUM_PARAMS_ID,s)
//                }
//            }

            if (type == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()) {
                //如果是期刊资源，需要传递baseurl
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url)
            }
            return hashMapOf
        }
}