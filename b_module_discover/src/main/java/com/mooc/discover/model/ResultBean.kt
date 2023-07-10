package com.mooc.discover.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.model.search.AlbumBean
import com.mooc.commonbusiness.model.search.TrackBean

class ResultBean : MultiItemEntity, BaseResourceInterface {
    var id: String = ""
    var title: String = ""
    var platform = 0
    var picture: String = ""
    var is_free //是否免费  0 付费 1 免费
            = 0
    var verified_active //是否有证书 0 无证书 1 有证书
            = 0
    var is_have_exam //是否有考试 0 无考试 1 有考试
            = 0
    var is_free_info // 是否收费文案
            : String = ""
    var verified_active_info // 证书文案
            : String = ""
    var is_have_exam_info // 考试文案
            : String = ""
    var platform_zh: String = ""
    var link: String = ""
    var org: String = ""
    var course_start_time: String = ""
    var about: String = ""
    var word_count: Long = 0
    var source: String = ""
    var publish_time: String = ""
    var writer: String = ""
    var url: String = ""
    var author: String = ""
    var track_data: TrackBean? = null
    var album_data: AlbumBean? = null
    var frag_title: String = ""
    var content: String = ""
    var description: String = ""
    var press: String = ""
    var basic_date_time: String = ""
    var basic_creator: String = ""
    var basic_cover_url: String = ""
    var basic_url: String = ""
    var resource_type = 0
    var recommend_data: RecommendColumn? = null

    override val itemType: Int
        get() = resource_type
    override val _resourceId: String
        get() {
            return if (_resourceType == ResourceTypeConstans.TYPE_SOURCE_FOLDER) {
                recommend_data?.id ?: ""
            } else if (_resourceType == ResourceTypeConstans.TYPE_MICRO_KNOWLEDGE) {
                recommend_data?.id ?: ""
            } else {
                id
            }
        }

    override val _resourceType: Int
        get() = resource_type

    override val _other: Map<String, String>
        get() {
            val hashMapOf = hashMapOf(
                IntentParamsConstants.WEB_PARAMS_TITLE to title,
                IntentParamsConstants.WEB_PARAMS_URL to url
            )

            if (_resourceType == ResourceTypeConstans.TYPE_PERIODICAL && basic_url.isNotEmpty()) {
                hashMapOf.put(IntentParamsConstants.PERIODICAL_PARAMS_BASICURL, basic_url)
            }

            if (_resourceType == ResourceTypeConstans.TYPE_SOURCE_FOLDER) {
                hashMapOf.put(
                    IntentParamsConstants.STUDYROOM_FOLDER_NAME,
                    recommend_data?.name ?: ""
                )
                hashMapOf.put(
                    IntentParamsConstants.STUDYROOM_STUDYLIST_FORM_RECOMMEND,
                    recommend_data?.is_admin.toString()
                )
            }
            return hashMapOf
        }

}