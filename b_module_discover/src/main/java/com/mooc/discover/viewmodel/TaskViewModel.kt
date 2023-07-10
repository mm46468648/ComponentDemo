package com.mooc.discover.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.common.ktextends.toast
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.utils.format.RequestBodyUtil
import com.mooc.discover.httpserver.HttpService
import com.mooc.discover.model.EarlyContent
import com.mooc.discover.model.TaskBindStudyListBean
import com.mooc.discover.model.TaskDetailsBean
import okhttp3.RequestBody
import org.json.JSONObject


class TaskViewModel : BaseViewModel() {


    val commondata = MutableLiveData<TaskDetailsBean>()
    val bindData = MutableLiveData<HttpResponse<TaskBindStudyListBean>>()

    val taskDetailData = MutableLiveData<HttpResponse<TaskDetailsBean>>()
    val taskEarlyCheckin = MutableLiveData<HttpResponse<Any>>()

    val taskEarlyContent = MutableLiveData<HttpResponse<EarlyContent>>()

//    fun getData(task_id: String) {
//
//        launchUI {
//            val understandData = HttpService.discoverApi.getTaskSystemId(task_id).await()
//            if (understandData.isSuccess)
//                commondata.postValue(understandData.data)
//        }
//    }

//    fun bindTaskSource(body: RequestBody) {
//        launchUI {
//            val bindBean = HttpService.discoverApi.bindTaskSource(body).await()
//            bindData.postValue(bindBean)
//        }
//    }


    fun bindStudyList(body: RequestBody): LiveData<TaskBindStudyListBean> {
        val data = MutableLiveData<TaskBindStudyListBean>()
        launchUI {
            val bindBean = HttpService.discoverApi.bindTaskSource(body).await()
            toast(bindBean.msg ?: "")
            data.postValue(bindBean.data)
        }
        return data
    }

    /**
     * 获取任务详情
     */
    fun getTaskDetail(task_id: String) {
        launchUI {
            val understandData = HttpService.discoverApi.getTaskSystemId(task_id).await()
            taskDetailData.postValue(understandData)
        }
    }


    fun postTaskEarlyCheckin(resource_id: String, task_id: String, content: String) {
        launchUI {
            val requestData = JSONObject()
            requestData.put("task_id", task_id)
            requestData.put("resource_id", resource_id)
            requestData.put("content", content)
            val understandData =
                HttpService.discoverApi.postTaskEarlyCheckin(RequestBodyUtil.fromJson(requestData))
            taskEarlyCheckin.postValue(understandData)
        }
    }


    fun getEarlyTaskContent() {
        launchUI {
            val data = HttpService.discoverApi.getTaskEarlyContent();
            taskEarlyContent.postValue(data)
        }
    }

    /**
     * 领取任务
     */
    fun postTaskToReceive(taskId: String): MutableLiveData<HttpResponse<Any>> {
        val mutableLiveData = MutableLiveData<HttpResponse<Any>>()
        launchUI {
            val requestData = JSONObject()
            requestData.put("task_id", taskId)
            val bean =
                HttpService.discoverApi.postTaskToReceive(RequestBodyUtil.fromJson(requestData))
                    .await()
            mutableLiveData.postValue(bean)

        }
        return mutableLiveData
    }

}