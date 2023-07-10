package com.mooc.commonbusiness.model.search

import android.text.TextUtils
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable

/**

 * @Author limeng
 * @Date 2020/8/10-4:32 PM
 */
data class ArticleBean(
        var id: String = "",
        var title: String = "",
        var source: String = "",
        var url: String = "",       //点击进文章详情展示的url
        var author: String? = "",
        var picture: String? = "",
        var publish_time: String? = "", //发布时间
        var _subscription: String? = "",  //简介
        var platform_zh: String? = "",   //平台
        val resource_status: String? = null,
        var task_finished: Boolean? = false,
        val is_task: Boolean? = false,
        var folder_id: String? = "",
        var from_folder_id: String? = "",     //运营推荐的学习清单id
        var task_count_down: String = "40"

) : StudyResourceEditable, BaseResourceInterface {
    override var _resourceId: String
        get() = id
        set(value) {}
    override var _resourceType: Int
        get() = ResourceTypeConstans.TYPE_ARTICLE
        set(value) {}
    override val _resourceStatus: Int
        get() {
            if (resource_status.isNullOrEmpty()) {
                return 0
            } else {
                return resource_status.toInt()
            }
        }
    override var _other: Map<String, String>?
        get() {
            val hashMapOf = hashMapOf(IntentParamsConstants.WEB_PARAMS_TITLE to title, IntentParamsConstants.WEB_PARAMS_URL to url)
            if (is_task == true) {   //文章任务标识,进入的时候需要弹验证码
                hashMapOf.put(IntentParamsConstants.WEB_PARAMS_IS_TASK, is_task.toString())
                hashMapOf.put(IntentParamsConstants.WEB_PARAMS_TASK_FINISH, task_finished.toString())

                //运营的清单传递另一个folderId
                val realFolderId = if(!TextUtils.isEmpty(from_folder_id)) from_folder_id else folder_id
                hashMapOf.put(IntentParamsConstants.STUDYROOM_FOLDER_ID, realFolderId?:"")
                hashMapOf.put(IntentParamsConstants.WEB_PARAMS_TASK_COUNT_DOWN, task_count_down)
            }

            return hashMapOf
        }
        set(value) {}

    override var resourceId: String
        get() = id
        set(value) {}
    override var sourceType: String
        get() = ResourceTypeConstans.TYPE_ARTICLE.toString()
        set(value) {}
}