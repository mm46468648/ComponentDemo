package com.mooc.discover.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

/**
 * 任务完成情况
 */
@Parcelize
class TaskFinishBean:Parcelable{
    var today_count : Int = 0         // 今天需要完成的个数(针对每日任务)
    var today_finish_count : Int = 0    // 今天完成的个数

    var total_day_count : Int = 0   //总共需要完成的天数
    var finish_day_count : Int = 0   //已完成的天数
    var remain_days : Int = 0   //距任务截止天数
    var early_task_limit_time:String=""   //早起时间段
    var timedelta_remain : String? = ""   //已领取,距离任务开始的时间

    var total_count : Int = 0       // 需完成的所有个数
    var completed_count : Int = 0   // 目前完成的个数
    var last_day : String = "" // 任务还剩 xx 天结束  ,>=24小时，距离任务结束时间显示 XX天，<24小时，距离任务结束时间显示 时时:分分:秒秒。
    var time_remain : String = "" // 本次任务还有多长时间结束
    var last_finish_day = 0 //上次完成的日期(用于在进度中显示),单位秒
    var is_today_finish : Boolean = false // 今日是否完成(针对每日任务, 时间进度条)

    var total_days : Int = 0 //任务总共多少天
    var continue_days:Int= 0 //任务当前进行到了第几天

}