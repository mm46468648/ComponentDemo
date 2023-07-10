package com.mooc.home.model.todaystudy

import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.constants.ResourceTypeConstans
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import com.mooc.discover.model.SourceData
import com.mooc.discover.model.TaskDetailsBean


/**
 * 今日学习必看数据
 */
data class TodayStudyMust(
        var order_list: ArrayList<Int>?,//资源排序，默认是空，代表默认排序，不为空的时候，最热资源300  打卡200
        var checkin_status: ArrayList<CheckInStatus>?, //签到状态数据
        var studyplan_status: ArrayList<StudyplanStatus>, //学习计划
        var most_hot_status: ArrayList<MostHotStatus>,   //最热资源
        var course_status: ArrayList<CourseStatus>,     //课程
        var album_status: ArrayList<AlbumStatus>,      //音频课
        var ebook_status: ArrayList<EbookStatus>,       //电子书
        var kanwu_status: ArrayList<Publication>?,       //刊物
        var task_system_status: ArrayList<TaskStatus>?       //任务

)


data class CheckInStatus(
        var continues_days: String,   //连续签到天数
        var has_checkin: Boolean    //今天是否签到
)

data class CourseStatus(     //课程状态
        var url: String,
        var update_time: Long,
        var type: Int,
        var title: String,
        var learned_process: Float = 0f, //课程学习进度  示例"0.0003"
        val score: String = "", //课程得分
        var is_show_score: Boolean = false,//工信部收费课程是否显示得分
        val platform: Int = -1, //平台
        val classroom_id: Int = -1, //班级id
        val platform_category: Int = 0     //工信部课程分类  1.付费 0，免费

) : BaseResourceInterface {
    override val _resourceId: String
        get() = url
    override val _resourceType: Int
        get() = type
    override val _other: Map<String, String>?
        get() = null
}

data class AlbumStatus(     //音频（虽然是和课程字段一样，单独定义方便在适配器中区分）
        var url: String,
        var update_time: Long,
        var type: Int,
        var title: String
) : BaseResourceInterface {
    override val _resourceId: String
        get() = url
    override val _resourceType: Int
        get() = type
    override val _other: Map<String, String>?
        get() = null
}

data class Publication(
        var url: String,
        var update_time: Long,
        var type: Int,
        var title: String
) : BaseResourceInterface {
    override val _resourceId: String
        get() = url
    override val _resourceType: Int
        get() = type
    override val _other: Map<String, String>?
        get() = null
}


data class EbookStatus(     //电子书状态
        var url: String,
        var update_time: Long,
        var type: Int,
        var title: String,
        var read_process: Float = 0f,       //阅读进度
        var read_times: Int = 0       //阅读时长,格式:分钟
) : BaseResourceInterface {
    override val _resourceId: String
        get() = url
    override val _resourceType: Int
        get() = type
    override val _other: Map<String, String>?
        get() = null
}

//最热资源，目前只有课程数据
data class MostHotStatus(
        var resource_type: Int = -1, //资源类型
        val resource_list: ArrayList<HotResource>? = null
)

data class StudyplanStatus(
        var resource_list: ArrayList<StudyPlanResource>? = null,
        val plan_starttime: Long = 0L,
        val plan_endtime: Long = 0,
        val join_end_time: Long = 0,
        val join_start_time: Long = 0,
        val plan_user_do: Int = 0,
        val plan_resource_num: Int = 0,
        val need_score_status: Int = 0,
        val is_review_checkin: Int = 0,
        val plan_name: String = "",
        val study_plan_id: String = "",
        val num_activity_limit: Int = 0,
        val set_resource_end_time: Long = 0,
        val before_resource_check_status: String = ""
) : BaseResourceInterface {
    override val _resourceId: String
        get() = study_plan_id
    override val _resourceType: Int
        get() = ResourceTypeConstans.TYPE_STUDY_PLAN
    override val _other: Map<String, String>?
        get() = null
}

//学习计划中的列表，
class StudyPlanResource(
        var study_plan: String = "",  //学习计划名称
        val plan_user_do: Int = 0,
        val plan_resource_num: Int = 0,


        )

//最热资源列表公共数据模型
class HotResource(
        var parentName: String = "",  //学习计划名称
        var title: String = "",
        var resource_id: String = "",
        var resource_type: Int = -1,
        var source_link: String = "",
        var task_type: Int  //任务类型，点击上传资源已完成的时候需要用

) : BaseResourceInterface {
    override val _resourceId: String
        get() = resource_id
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>?
        get() = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to source_link, IntentParamsConstants.WEB_PARAMS_TITLE to title)
}


data class TaskStatus(
        //我的任务

        var id: String = "",
        var type: Int,       //任务类型
        var title: String = "",
        var url: String = "",

        var remain_days: String = "", //任务结束还有多少天
        var calculate_type: Int = -1,      //1普通任务(完成N个就行) 3累计任务(累计X天,每天完成N个)  2每日任务(连续X天,每天完成N个)
        var total_count: Int = 0,      //需完成总个数
        var completed_count: Int = 0,      //已完成个数
        var today_finish : Int = 0,   //今日完成个数
        var time_mode : Int = 0,    //是否是永久任务  ,1.为永久任务,默认0

        var task_data : TaskDetailsBean?

)



