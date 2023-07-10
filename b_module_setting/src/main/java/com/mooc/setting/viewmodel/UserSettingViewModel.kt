package com.mooc.setting.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.model.setting.UserSettingBean
import com.mooc.commonbusiness.net.ApiService
import com.mooc.setting.SetApi
import okhttp3.RequestBody


class UserSettingViewModel : BaseViewModel() {

    val getSettingData: MutableLiveData<UserSettingBean> by lazy {
        MutableLiveData<UserSettingBean>()
    }

    var errorException = MutableLiveData<Exception?>();


    val updateSettingData: MutableLiveData<UserSettingBean> by lazy {
        MutableLiveData<UserSettingBean>()
    }

    fun getData() {

        launchUI({
            val bean = ApiService.retrofit.create(SetApi::class.java).getUserSettingDataAsync().await()
            if (bean.isSuccess)
                getSettingData.postValue(bean.data)
        }, {
            errorException.postValue(it)
        })
    }


    fun postData(body: RequestBody) {
        launchUI({
            val bean = ApiService.retrofit.create(SetApi::class.java).postUserSettingDataAsync(body).await()
            if (bean.isSuccess)
                updateSettingData.postValue(bean.data)
        }, {
            errorException.postValue(it)
        })

    }

}