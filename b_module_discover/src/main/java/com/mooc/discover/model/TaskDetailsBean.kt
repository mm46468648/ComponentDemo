package com.mooc.discover.model

import android.os.Parcel
import android.os.Parcelable
import com.mooc.commonbusiness.constants.IntentParamsConstants
import com.mooc.commonbusiness.interfaces.BaseResourceInterface
import kotlinx.android.parcel.Parcelize

/**
 * 任务详情
 */
class TaskDetailsBean() : Parcelable {

    var id: String = ""
    var title: String = ""
    var start_time: String = "" //任务开始领取时间
    var end_time: String = ""   //任务结束领取时间
    var task_start_date: String = "" //任务开始时间
    var task_end_date: String = "" //任务结束时间

    var image_url: String = ""        //任务详情描述图片
    var content: String = ""      //任务详情富文本
    var type: Int = -1        //任务类型:2: 连续打卡系列任务 4:资源学习系列任务 5:拉新系列任务  6:参加学习项目系列任务
    var status: Int =
        0      //任务状态:0,1,2,3,4,5,6,7请查阅TaskConstTan TASK_TYPE类型 status=41 表示子任务受父任务影响而未完成
    var task_resource_type: Int = -1      //该任务下指定的资源类型
    var select_resource_type: Int = -1      //该任务下指定的资源类型,在今日学习中使用
    var calculate_type: Int = -1      //1普通任务(完成N个就行) 3累计任务(累计X天,每天完成N个)  2每日任务(连续X天,每天完成N个),4组合任务
    var success_score: Int = 0   //奖励积分
    var fail_score: Int = 0   //任务失败,需要扣除的积分
    var score: Score? = null       //新版本需要显示范围,所以直接改为String显示
    var join_num: String = "" //参与人数
    var limit_num: String = "" //限制人数
    var is_limit_num: Boolean = false  //是否限制人数
    var is_bind_folder: Boolean = false  //是否需要绑定学习清单
    var task_start: Boolean = false  //任务已经领取,但是未开始进行统计(有的任务在领取之后,需要第二天才能开始做)
    var other_data: FoldListBean? = null
    var bind_data: TaskBindStudyListBean? = null
    var finish_data: TaskFinishBean? = null

    //    var task_week_line: ArrayList<WeekLine>? = null
    var finish_source_data: ArrayList<TaskFinishResource>? = null   //任务中完成的资源
    var children_task: ArrayList<TaskDetailsBean>? = null   //组合任务中的子任务
    var children_list: ArrayList<TaskDetailsBean>? = null   //互斥任务中的非必做任务中子任务
    var task_rule: TaskRule? = null   //任务中完成的资源

    var task_config_data: TaskConfigData? = null

    var choice_task: Choice? = null
    var groupName:String = "";         //属于哪个互斥任务组
    var time_mode: Int = 0  //任务时间是否为不限时   1,不限时,永久任务,默认0

    constructor(parcel: Parcel) : this() {
        id = parcel.readString() ?: ""
        title = parcel.readString() ?: ""
        start_time = parcel.readString() ?: ""
        end_time = parcel.readString() ?: ""
        task_start_date = parcel.readString() ?: ""
        task_end_date = parcel.readString() ?: ""
        image_url = parcel.readString() ?: ""
        content = parcel.readString() ?: ""
        type = parcel.readInt()
        status = parcel.readInt()
        task_resource_type = parcel.readInt()
        select_resource_type = parcel.readInt()
        calculate_type = parcel.readInt()
        success_score = parcel.readInt()
        fail_score = parcel.readInt()
        join_num = parcel.readString() ?: ""
        limit_num = parcel.readString() ?: ""
        is_limit_num = parcel.readByte() != 0.toByte()
        is_bind_folder = parcel.readByte() != 0.toByte()
        task_start = parcel.readByte() != 0.toByte()
        other_data = parcel.readParcelable(FoldListBean::class.java.classLoader)
        bind_data = parcel.readParcelable(TaskBindStudyListBean::class.java.classLoader)
        finish_data = parcel.readParcelable(TaskFinishBean::class.java.classLoader)
        task_config_data = parcel.readParcelable(TaskConfigData::class.java.classLoader)
        choice_task = parcel.readParcelable(Choice::class.java.classLoader)
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(start_time)
        parcel.writeString(end_time)
        parcel.writeString(task_start_date)
        parcel.writeString(task_end_date)
        parcel.writeString(image_url)
        parcel.writeString(content)
        parcel.writeInt(type)
        parcel.writeInt(status)
        parcel.writeInt(task_resource_type)
        parcel.writeInt(select_resource_type)
        parcel.writeInt(calculate_type)
        parcel.writeInt(success_score)
        parcel.writeInt(fail_score)
        parcel.writeString(join_num)
        parcel.writeString(limit_num)
        parcel.writeByte(if (is_limit_num) 1 else 0)
        parcel.writeByte(if (is_bind_folder) 1 else 0)
        parcel.writeByte(if (task_start) 1 else 0)
        parcel.writeParcelable(other_data, flags)
        parcel.writeParcelable(bind_data, flags)
        parcel.writeParcelable(finish_data, flags)
        parcel.writeParcelable(task_config_data, flags)
        parcel.writeParcelable(choice_task, flags)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<TaskDetailsBean> {
        override fun createFromParcel(parcel: Parcel): TaskDetailsBean {
            return TaskDetailsBean(parcel)
        }

        override fun newArray(size: Int): Array<TaskDetailsBean?> {
            return arrayOfNulls(size)
        }
    }

}

/**
 * 互斥任务数据模型
 */
@Parcelize
data class Choice(
    var necessary: ArrayList<TaskDetailsBean>? = null,      //必做任务中的子任务
    var choice: List<ChoiceTask>? = null         //必做任务中的子任务
) : Parcelable

@Parcelize
data class ChoiceTask(
    var choice_list: ArrayList<TaskDetailsBean>? = null,
    var id: String = "",
    var title: String = ""
) : Parcelable

/**
 * 积分数据模型
 */
@Parcelize
data class Score(
    var success_score: String = "",//有可能是直接的奖励积分,有可能是积分的范围如:35~63
    var fail_score: String = ""         //同奖励积分
) : Parcelable {
}

class EarlyContent {

    var content: String = ""
    var resource_id: Int = -1
}

/**
 * 任务按钮的配置
 */
@Parcelize
class TaskConfigData : Parcelable {
    var click: Boolean = false        //打卡按钮是可以点击
}

/**
 * 任务规则
 */
class TaskRule {
    var base_rule: String? = ""       //任务规则,使用富文本展示
    var special_rule: String = ""      //任务描述
}

/**
 * 时间轴数据
 */
class WeekLine {
    var date = 0L
    var status: Int = 0   //0 领取时间大于当前时间  1 已完成  2 未完成  3 未执行(未到执行时间)
}

/**
 * 任务完成资源
 */
@Parcelize
class TaskFinishResource : BaseResourceInterface, Parcelable {
    var title: String = ""
    var sub_title: String = ""     //资源标题
    var status: Int = 0 // 0未完成 1已完成,2失败
    var resource_id: Int = 0
    var resource_type: Int = -1
    var link : String = ""

    override val _resourceId: String
        get() = resource_id.toString()
    override val _resourceType: Int
        get() = resource_type
    override val _other: Map<String, String>
        get() = hashMapOf(IntentParamsConstants.WEB_PARAMS_URL to link)
}