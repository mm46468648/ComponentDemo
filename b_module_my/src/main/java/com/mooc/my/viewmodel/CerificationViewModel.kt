package com.mooc.my.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.my.repository.MyModelRepository
import okhttp3.RequestBody

/**

 * @Author limeng
 * @Date 2020/9/23-7:58 PM
 */
class CerificationViewModel :BaseViewModel() {
    private val repository = MyModelRepository()
    val cerificationBean : MutableLiveData<HttpResponse<Any>> by lazy {
        MutableLiveData<HttpResponse<Any>>()
    }
    fun loadData(body: RequestBody){
        launchUI {
           val medalresult= repository.applyMoocCertificate(body)
            cerificationBean.postValue(medalresult)
        }
    }
}