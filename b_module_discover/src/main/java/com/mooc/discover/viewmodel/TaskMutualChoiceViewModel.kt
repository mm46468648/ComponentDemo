package com.mooc.discover.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.common.utils.GsonManager
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.commonbusiness.constants.TaskConstants
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.Choice
import com.mooc.discover.model.TaskChoiceBean

/**
 * 互斥任务选择ViewModel
 * 记录选择的任务id
 */
class TaskMutualChoiceViewModel : BaseViewModel() {

    val choiceTaskList = MutableLiveData<ArrayList<TaskChoiceBean>>()

    var choic: Choice? = null
        set(value) {
            val list = arrayListOf<TaskChoiceBean>()
            //插入一个必选id,占位
            val n = TaskChoiceBean()
            n.tab_id = TaskConstants.STR_MUST_TASK
            n.choice_id = TaskConstants.STR_MUST_TASK
            list.add(n)

            value?.choice?.forEach {
                val c = TaskChoiceBean()
                c.tab_id = it.id
                list.add(c)
            }

            choiceTaskList.postValue(list)
            field = value
        }


    /**
     * 更新选择数据
     */
    fun updateChoice(tabId: String, taskId: String) {
        val list = choiceTaskList.value

        list?.forEach {
            if (it.tab_id == tabId) {
                it.choice_id = taskId
            }
        }
        choiceTaskList.postValue(list)
    }

    /**
     * 领取任务
     * 上传选中的任务列表
     */
    fun postTaskToReceive(taskId: String,choiceList:ArrayList<TaskChoiceBean>): MutableLiveData<HttpResponse<Any>> {
        val mutableLiveData = MutableLiveData<HttpResponse<Any>>()

        launchUI ({
            val hashMapOf = hashMapOf<String, Any>("task_id" to taskId, "bind_tab" to choiceList)
            val toJson = GsonManager.getInstance().toJson(hashMapOf)
            val bean =
                HttpService.discoverApi.postTaskToReceive(RequestBodyUtil.fromJsonStr(toJson))
                    .await()
            mutableLiveData.postValue(bean)
        },{
            mutableLiveData.postValue(null)
        })
        return mutableLiveData
    }

}