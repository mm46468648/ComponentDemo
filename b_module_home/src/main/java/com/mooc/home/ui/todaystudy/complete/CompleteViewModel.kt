package com.mooc.home.ui.todaystudy.complete

import androidx.lifecycle.viewModelScope
import com.mooc.commonbusiness.base.BaseListViewModel
import com.mooc.home.HttpService
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.async
import com.mooc.home.model.todaystudy.TodayTask

/**
 * 今日学习已完成
 */
class CompleteViewModel : BaseListViewModel<TodayTask>() {

    companion object {
        const val CHECKIN_TITLE = "签到打卡"
        const val STUDYPROJEC_TTITLE = "学习项目"
        const val MOSTHOT_TITLE = "大家最近在学的资源"
        const val COLUMN_TITLE = "已订专栏"
        const val SPECIAL_TITLE = "已订专题"
        const val TASK_TITLE = "我的任务"
    }

    override suspend fun getData(): Deferred<List<TodayTask>?> {
        val async = viewModelScope.async {
            val response = HttpService.homeApi.getTodayFinishTask().await()
            //对已完成任务进行，组装
            val taskList = generateTaskList(response)
            taskList

        }
        return async
    }

    private fun generateTaskList(response: TodayTask): List<TodayTask> {
        val arrayListOf = arrayListOf<TodayTask>()
        //目前接口中没有返回课程，电子书和音频类型，暂不处理
        //获取签到数据
        if (!response.checkin_status.isNullOrEmpty()) {
            val todayTask = TodayTask()
            todayTask.name = CHECKIN_TITLE
            todayTask.nameShow = true
            todayTask.checkin_status = response.checkin_status
            arrayListOf.add(todayTask)
        }

        //获取任务数据
        response.task_system_status?.forEachIndexed { index, taskStatus ->
            val todayTask = TodayTask()
            todayTask.name = TASK_TITLE
            todayTask.url = taskStatus.url
            todayTask.desc = taskStatus.title
//            todayTask.resource_id = taskStatus.resource_id
//            todayTask.resource_type = taskStatus.resource_type
//            todayTask.link = taskStatus.source_data?.source_link?:""
//            todayTask.basic_url = taskStatus.source_data?.basic_url?:""
//            todayTask.display_status = taskStatus.display_status
            arrayListOf.add(todayTask)
        }

        //获取学习项目数据
        response.studyplan_status?.forEachIndexed { _, studyProject ->
            studyProject.resource_list.forEach {
                val todayTask = TodayTask()
                todayTask.name = STUDYPROJEC_TTITLE
                todayTask.title = studyProject.plan_name?:""
                todayTask.desc = it.source_title?:""
                todayTask.resource_id = it.source_select_id
                todayTask.resource_type = it.source_type
                todayTask.link = it.link?:""
                arrayListOf.add(todayTask)
            }

        }
        //获取更多热门数据
        response.most_hot_status?.forEachIndexed { _, hotStatus ->
            hotStatus.resource_list.forEach {
                val todayTask = TodayTask()
                todayTask.name = MOSTHOT_TITLE
                todayTask.desc = it.title
                todayTask.resource_id = it.resource_id
                todayTask.resource_type = it.resource_type
                todayTask.link = it.source_link
                arrayListOf.add(todayTask)
            }
        }

        //获取专栏数据
        response.colum_status?.forEachIndexed { _, column ->
            column.resource_list.forEach {
                val todayTask = TodayTask()
                todayTask.name = COLUMN_TITLE
                todayTask.title = column.colum_name
                todayTask.desc = it.title
                todayTask.resource_id = it.resource_id
                todayTask.resource_type = it.type
                todayTask.link = it.link?:""

                arrayListOf.add(todayTask)
            }
        }

        //获取专题数据
        response.special_status?.forEachIndexed { _, special ->
            special.resource_list.forEach {
                val todayTask = TodayTask()
                todayTask.name = SPECIAL_TITLE
                todayTask.title = special.colum_name
                todayTask.desc = it.title
                todayTask.resource_id = it.resource_id
                todayTask.resource_type = it.type
                todayTask.link = it.link?:""
                arrayListOf.add(todayTask)
            }
        }
        return arrayListOf
    }
}