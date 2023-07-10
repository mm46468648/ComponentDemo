package com.mooc.studyproject.model

import com.mooc.commonbusiness.model.studyproject.StudyDynamic
import java.io.Serializable
import java.util.*

/**
 * Created by huangzuoliang on 2017/9/19.
 */
data class CommentDataBean(
    val study_plan_id: Int = 0,
    val set_time: Long = 0,
    val comment_like_status: Int = 0,
    val is_start_user: Boolean = false,
    val comments: ArrayList<ItemComment>? = null,
    val activity: StudyDynamic? = null,
    val comment_to_info: CommentToInfoBean? = null

        /**
         * comment_to_info : {"user_id":"698","name":"悠扬1","comment_content":"他问我看看"}
         */

) : Serializable {




    class CommentToInfoBean {
        /**
         * user_id : 698
         * name : 悠扬1
         * comment_content : 他问我看看
         */
         val user_id: String? = null
         val name: String? = null
         val comment_content = ""
         val comment_type //"0"图文 "1"音频
                : String? = null
         val comment_content_long = "0" //如果是音频类型回复，音频时长
         val comment_img_list //回复中的图片集合
                : List<String>? = null
    }

}