package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.UnderstandContributionBean

class UnderstandContributionViewModel : BaseViewModel() {

    val commondata: MutableLiveData<UnderstandContributionBean> by lazy {
        MutableLiveData<UnderstandContributionBean>()
    }

    fun getData(type: String) {
        launchUI {
            val understandData = ApiService.getRetrofit().create(StudyRoomApi::class.java).getCommonData(type).await()
            if (understandData.isSuccess)
                commondata.postValue(understandData.data)
        }
    }



}


