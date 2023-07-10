package com.mooc.commonbusiness.constants

/**
 * 任务相关常量类
 */
class TaskConstants {

    companion object{
        const val TASK_STATUS_UNACCPET = 0 //任务未领取

        //领取之后的状态
        const val TASK_STATUS_UNSTART = 6 //任务已报名,但是未开始
        const val TASK_STATUS_DOING = 1 //任务进行中
        const val TASK_STATUS_SUCCESS = 2 //任务成功
        const val TASK_STATUS_FAIL = 3 //任务失败
        const val TASK_STATUS_CHILD_NOT_COMPLETE= 41 //子任务收到父任务影响而未完成


        //未领取,未开始的状态
        const val TASK_STATUS_EXPIRED = 4 //任务未领取但是已过期
        const val TASK_STATUS_CANNOT_GET = 5 //任务领取还未开始,还不能领取的状态
        const val TASK_STATUS_CANNOT_GET_UNACCPET= 7 //任务到了领取时间,但是未领取

        //任务类型
        const val TASK_TYPE_CHECKIN = 2 //打卡类型任务
        const val TASK_TYPE_CHECKIN_MORNING = 3 //早起打卡类型任务
        const val TASK_TYPE_STUDY_RESOURCE = 4 //学习资源
        const val TASK_TYPE_PULL_NEW_USER = 5 //拉新任务
        const val TASK_TYPE_STUDY_PROJECT = 6 //学习项目类型
        const val TASK_TYPE_COMBINE_DO_ALL = 401 //组合型任务(全都需要做的类型)
        const val TASK_TYPE_COMBINE_DO_MUTUAL = 402 //组合型任务(互斥类型,完成这个就不能选别的)
        const val TASK_TYPE_REED_BOOK = 501 //青年读书会读书定制任务

        //任务计算类型
        const val TASK_CACULATE_TYPE_NOMAL = 1 //1普通任务(完成N个就行) 3累计任务(累计X天,每天完成N个)  2每日任务(连续X天,每天完成N个)
        const val TASK_CACULATE_TYPE_CONTINUE = 2 //2每日任务(连续X天,每天完成N个)
        const val TASK_CACULATE_TYPE_SUM = 3 //3累计任务(累计X天,每天完成N个)
        const val TASK_CACULATE_TYPE_COMBINE = 4 //4.组合型任务(下面包含一堆子任务)
        const val TASK_CACULATE_TYPE_CUSTOM = 5 //5.定制型任务(目前只有501读书任务)



        const val TASK_WEEKLINE_STATUS_NOTNEED = 0 //不需要做任务的时间(显示横杠)
        const val TASK_WEEKLINE_STATUS_SUCCESS = 1 //需要做已成功
        const val TASK_WEEKLINE_STATUS_FAIL = 2 //需要做已失败
        const val TASK_WEEKLINE_STATUS_WILLDO = 3 //需要做单未执行
        const val TASK_WEEKLINE_STATUS_WILLCOMPLETE= 4 //最终日期需要做但未做
        const val TASK_WEEKLINE_STATUS_COMPLETE = 5 //最终日期,需要做已做

        val taskTypeArray = arrayOf(2,4,5,6)        //当前版本支持的任务类型数组

        const val INTENT_MUTUAL_TAKS_TITLE = "mutual_taks_title"     //互斥任务标题(必做任务或其他)
        const val INTENT_MUTUAL_TAKS_LIST = "mutual_taks_list"     //互斥任务必做任务列表
        const val INTENT_MUTUAL_TAKS_DETAIL = "mutual_taks_detail"     //互斥任务详情
        const val INTENT_MUTUAL_TAKS_TAB_ID = "mutual_taks_tab_id"     //互斥任务非必选任务栏id
        const val STR_MUST_TASK= "必做任务"     //必做任务

    }
}