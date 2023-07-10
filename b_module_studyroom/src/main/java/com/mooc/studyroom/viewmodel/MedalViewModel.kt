package com.mooc.studyroom.viewmodel

import androidx.lifecycle.MutableLiveData
import com.mooc.commonbusiness.api.HttpService
import com.mooc.commonbusiness.base.BaseViewModel
import com.mooc.commonbusiness.constants.ShareTypeConstants
import com.mooc.commonbusiness.model.my.CMSShareBean
import com.mooc.commonbusiness.model.HttpResponse
import com.mooc.commonbusiness.model.sharedetail.ShareDetailModel
import okhttp3.RequestBody
import com.mooc.commonbusiness.net.ApiService
import com.mooc.studyroom.StudyRoomApi
import com.mooc.studyroom.model.MedalBean

/**

 * @Author limeng
 * @Date 2020/9/23-7:58 PM
 */
class MedalViewModel :BaseViewModel() {
    var  shareView=MutableLiveData<HttpResponse<CMSShareBean>>()
    var  shareData=MutableLiveData<ShareDetailModel>()
    val medalBean : MutableLiveData<MedalBean> by lazy {
        MutableLiveData<MedalBean>()
    }
    fun loadData(errorCallBack:(()->Unit)? = null){
        launchUI ({
           val medalresult= ApiService.getRetrofit().create(StudyRoomApi::class.java).getMedalData().await()
            medalBean.postValue(medalresult)
        },{
            errorCallBack?.invoke()
        })
    }

    fun getCMSShareData(requestBody: RequestBody) {
        launchUI {
//            val resule = HttpService.commonApi.getCMSShareData(requestBody).await()
            val resule = HttpService.commonApi.getShareDetailDataNew(ShareTypeConstants.SHARE_TYPE_MEDAL,"","")
            shareData.postValue(resule.data)
        }
    }
}