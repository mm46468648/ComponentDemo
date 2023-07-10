package com.mooc.my.model

import com.chad.library.adapter.base.entity.MultiItemEntity
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.model.search.AlbumBean

/**

 * @Author limeng
 * @Date 2020/11/4-11:03 AM
 */
data class SchoolSourceBean(
    val id: String? = null,
    val user: String? = null,
    val avatar: String? = null,
    val avatar_identity: String? = null,
    val name: String? = null,
    val resource_type: Int = -1,
    val resource_id: String = "",
    val other_data: String? = null,
    val status: String? = null,
    var like_count: Int = 0,
    val enrolled: Boolean = false,
    var is_like: Boolean = false,
    val created_time: Long = 0,
    val detail_info: Any? = null,        //关联的资源详情

) : BaseResourceInterface, MultiItemEntity {
    override val itemType: Int
        get() = resource_type

    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>?
        get() {
            if (detail_info != null) {

                val toJson = GsonManager.getInstance().toJson(detail_info)
                val detail_info = GsonManager.getInstance().convert(toJson, DetailInfo::class.java)


                //h5跳转地址
                val hashMapOf = hashMapOf(
                    IntentParamsConstants.WEB_PARAMS_TITLE to detail_info.title,
                    IntentParamsConstants.WEB_PARAMS_URL to detail_info.url
                )

                //如果是单条音频需要传递专辑id
//                if (resource_type == ResourceTypeConstans.TYPE_TRACK){
//                    if(detail_info.subordinated_album!=null) {
//                        //前面gson会把每个int转换成double，这里面用的时候再转回来
//                        val toLong = detail_info.subordinated_album?.id?.toDouble()?.toLong()
//
////                        val s = detail_info.subordinated_album?.id ?: ""
//                        hashMapOf.put(IntentParamsConstants.ALBUM_PARAMS_ID,toLong.toString())
//                    }
//                }
                if (resource_type == ResourceTypeConstans.TYPE_PERIODICAL && detail_info.basic_url.isNotEmpty()) {
                    //如果是期刊资源，需要传递baseurl
                    hashMapOf.put(
                        IntentParamsConstants.PERIODICAL_PARAMS_BASICURL,
                        detail_info.basic_url
                    )
                }
                return hashMapOf
            }
            return null
        }
}

data class DetailInfo(
    val title: String = "",
    var url: String = "",
    var basic_url: String = "",


    var activity_type: Int = 0,//0为文本类型，1为语音类型
    var source_type_id: Int = 0,//与动态相关的资源类型的id

    var subordinated_album: AlbumBean?
)