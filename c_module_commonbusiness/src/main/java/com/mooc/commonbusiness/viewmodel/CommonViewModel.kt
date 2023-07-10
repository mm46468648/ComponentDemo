package com.mooc.commonbusiness.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.my.WebUrlBean
import com.mooc.commonbusiness.model.privacy.PrivacyPolicyCheckBean
import com.mooc.commonbusiness.module.report.CommonRepository


class CommonViewModel : BaseViewModel() {

    private val commonRepository = CommonRepository()
    var errorException = MutableLiveData<Exception?>()
    val liveData = MutableLiveData<HttpResponse<PrivacyPolicyCheckBean>>()
    val liveDataWebUrl = MutableLiveData<HttpResponse<WebUrlBean>>()
    var errorExceptionWebUrl = MutableLiveData<Exception?>()

    /**
     * 隐私政策弹窗查询
     */
    fun getPrivacyPolicyCheck(version: String): LiveData<HttpResponse<PrivacyPolicyCheckBean>> {

        launchUI({
            val joinBean = commonRepository.getPrivacyPolicyCheck(version)
            liveData.postValue(joinBean)
        }, {
            errorException.postValue(it)
        })
        return liveData
    }

    /**
     * 隐私政策弹窗查询
     */
    fun getWebUrl(): LiveData<HttpResponse<WebUrlBean>> {

        launchUI({
            val webUrl = commonRepository.getWebUrl()
            liveDataWebUrl.postValue(webUrl)
        }, {
            errorExceptionWebUrl.postValue(it)
        })
        return liveDataWebUrl
    }


    fun getFeedBackLoadUrl(error:(()->Unit)? = null):LiveData<HttpResponse<WebUrlBean>>{
        val liveData = MutableLiveData<HttpResponse<WebUrlBean>>()
        launchUI ({
            val webUrl = commonRepository.getWebUrl()
            liveData.postValue(webUrl)
        },{error?.invoke()})
        return liveData
    }
}