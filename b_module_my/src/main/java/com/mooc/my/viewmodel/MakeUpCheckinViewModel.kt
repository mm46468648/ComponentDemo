package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.net.ApiService
import com.mooc.search.api.MyModelApi
import okhttp3.RequestBody

class MakeUpCheckinViewModel : BaseViewModel(){

    val commondata: MutableLiveData<HttpResponse<Any>> by lazy {
        MutableLiveData<HttpResponse<Any>>()
    }

    fun postData(body: RequestBody) {
        launchUI {
            val bean =
                ApiService.getRetrofit().create(MyModelApi::class.java).postMakeUpCheckinResource(body).await()
            commondata.postValue(bean)
        }
    }



}