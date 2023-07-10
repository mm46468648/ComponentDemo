package com.mooc.commonbusiness.model.search

import android.os.Parcelable
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.commonbusiness.interfaces.StudyResourceEditable
import com.mooc.commonbusiness.model.course.BaseChapter
import com.mooc.commonbusiness.utils.HashUtil
import kotlinx.android.parcel.Parcelize
import org.jetbrains.annotations.NotNull
import java.io.Serializable

/**
 * @Author limeng
 * @Date 2020/8/10-4:13 PM
 */
@Parcelize
data class CourseBean(
    var id: String = "",
    var title: String = "",
    var platform_zh: String? = "",
    var org: String? = "",
    var picture: String = "",
    var classroom_id: String = "", //新学堂课程带有班级信息
    var platform: Int = -1,
    //记录上次播放章节id
    var lastPlayChapterId: String = "",
    var course_id: String = "",   //实际用来查询第三方课程的id
    var course_type: String = "",
    var verified_active_info: String = "",
    var create_datetime: String? = "",
    var isEnrolled: String? = "",
    var course_start_time: String? = "",
    var is_have_exam: String? = "",
    var verified_active: String? = "",
    var is_free: String? = "",
    var is_show_score: Boolean? = false,//工信部收费课程是否显示得分
    var is_have_exam_info: String? = "",
    var is_free_info: String? = "",
    var small_image: String? = null,
    var source: String? = null,
    var staffsBeenList: ArrayList<SearchResultStaffsBean?>? = null,
    var score: String = "", //得分   有的是0，有的是0.0
    var exercise_process: String = "", //练习进度
    var learned_process: String = "", //完成进度
    //用于停服之后的字段
    var course_status: String = "", //# 0 停服 1 可用
    var verified_status: String = "",//# 0 无证书 1 可申请证书 2 已申请证书
    val score_level: String = "", //platform 26 的时候成绩评定等级
    val resource_status: Int = 0,
    var identity_name: String = "",// 推荐模板右上角角标文案，只有合集和课程类型取这个字段

//        var name : String = ""

) : StudyResourceEditable, Parcelable, BaseResourceInterface, Serializable {

//    var id: String = ""

    //********资源跳转需要实现的接口Start********/
    override val _resourceId: String
        get() = id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_COURSE
    override val _resourceStatus: Int
        get() = resource_status

    override val _other: Map<String, String>
        get() {
            val hashMapOf = hashMapOf<String, String>()
            hashMapOf.put(IntentParamsConstants.COURSE_PARAMS_PLATFORM, platform.toString())
            hashMapOf.put(IntentParamsConstants.COURSE_PARAMS_CLASSROOM_ID, classroom_id)
            return hashMapOf
        }
    //********资源跳转需要实现的接口END********/


    //********资源编辑需要实现的接口Start********/
    //实现接口接口中定义的字段
    override val resourceId: String
        get() {    //课程id，有特殊，新学堂需要拼接班级id信息
            return if (classroom_id.isNotEmpty()) "${id}_$classroom_id" else id
        }

    override val sourceType: String
        get() = ResourceTypeConstans.TYPE_COURSE.toString()

    //********资源编辑需要实现的接口END********/

}

class ChaptersBean : Serializable, BaseChapter {

    var title: String = ""     //老学堂章节标题

    var sequentials: List<ChaptersBean>? = null //老学堂章节次标题

    var chapterName: String = ""//章节名字
    override var name: String = ""
    var level: Int = 0

    var order: Int = 0

    @NotNull
    override var id: String = "" //视频id
    override fun generateDownloadId(courseId: String, classRoomId: String): Long {
        return HashUtil.longHash("${courseId}_${classRoomId}_${id}")
    }

    val section_list: ArrayList<ChaptersBean>? = null   //新学堂，复用ChaptersBean name order字段
    var leaf_list: ArrayList<ChaptersBean>? = null   //新学堂，复用ChaptersBean
    var type: Int =
        -1//课件类型 VIDEO = 0 AUDIO = 1 LIVE = 2 ARTICLE = 3 DISCUSSION = 4 QUIZ = 5 EXERCISE = 6

    var courseId: String = ""
    var classRoomId: String = ""

    //下载模型
//    var downloadModel: DownloadInfo? = null

}
