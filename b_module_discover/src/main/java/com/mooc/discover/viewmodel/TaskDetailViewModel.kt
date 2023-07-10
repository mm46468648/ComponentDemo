package com.mooc.discover.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.common.ktextends.loge
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.discover.httpserver.HttpService

class TaskDetailViewModel : BaseViewModel() {

    /**
     * 获取引导页的配置
     */
    fun getGuideConfig() : MutableLiveData<HttpResponse<Boolean>>{
        val mutableLiveData = MutableLiveData<HttpResponse<Boolean>>()
        launchUI ({
            val recommendBaseResponse = HttpService.discoverApi.getTaskGuideConfig()
            mutableLiveData.postValue(recommendBaseResponse)
        },{
            loge(it.toString())
        })
        return mutableLiveData
    }

    fun postGuideConfig(){
        launchUI {
            HttpService.discoverApi.postTaskGuideConfig()
        }
    }
}