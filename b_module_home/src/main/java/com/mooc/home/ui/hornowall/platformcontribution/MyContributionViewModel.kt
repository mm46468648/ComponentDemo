package com.mooc.home.ui.hornowall.platformcontribution

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.home.HttpService
import com.mooc.home.model.honorwall.MyContributionBean

class MyContributionViewModel() : BaseViewModel() {

    val myContributiondata: MutableLiveData<MyContributionBean> by lazy {
        MutableLiveData<MyContributionBean>()
    }

    fun getData(type: String) {
        launchUI {
            val understandData = HttpService.homeApi.getMyContribution(type).await()
            if (understandData.isSuccess)
                myContributiondata.postValue(understandData.data)
        }
    }



}


