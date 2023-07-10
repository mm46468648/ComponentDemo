package com.mooc.studyproject.model

import java.io.Serializable

/**

 * @Author limeng
 * @Date 2020/12/28-11:29 AM
 */
class ItemComment (): Serializable {

    val id: Int = 0
    val study_activity: Int = 0
    val comment_user: CommentUserBean? = null
    val comment_content: String? = null
    val comment_time: String? = null
    var comment_status: Int = 0
    val comment_to: String? = null
    var like_num: Int = 0
    var is_like: Boolean = false
    val comment_type: Int = 0
    val comment_content_long: String? = null//如果是音频类型，音频的时长

    val is_comment_user: Int = 0
    val playState: Int = 21
    val is_from_cms: Int = 0
    val comment_img_list: ArrayList<String>? = null//回复中的图片集合

    val from_cms_user_name: CommentUserBean? = null
    val is_studyplan_start_user: Boolean = false
    val is_top: String? = null
    val comment_to_info: CommentDataBean.CommentToInfoBean? = null
    val state: Boolean = false
    val refreshText: Boolean = false

    var measureEnd = false
    var isShowExpand :Boolean=false      //是否展示展开收起
    var expandState :Boolean=false      //当前是否是展开状态,默认展开

    class CommentUserBean {
        val id = 0
        val name: String? = null
        val avatar: String? = null
        val avatar_identity: String? = null
        val nickname: String? = null
    }

}